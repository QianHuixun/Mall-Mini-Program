package cn.tofocus.lejia.api.v1.jd;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import cn.tofocus.lejia.app.AppTags;
import cn.tofocus.lejia.bean.dto.app.jd.JdOrderDeliveryInfo;
import cn.tofocus.lejia.bean.dto.app.jd.JdOrderDetails;
import cn.tofocus.lejia.bean.dto.excel.jd.ExportJdOrderGoodsReport;
import cn.tofocus.lejia.bean.dto.market.jd.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.common.excel.ExcelHelper;
import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.BaseExportApiImpl;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.excel.jd.ExportJdOrderReport;
import cn.tofocus.lejia.bean.dto.jd.JdOrderExcel;
import cn.tofocus.lejia.bean.dto.jd.JdOrderLineExcel;
import cn.tofocus.lejia.bean.enums.OrderStatus;
import cn.tofocus.lejia.domain.jd.JdAppOrderManager;
import cn.tofocus.lejia.domain.jd.JdOrderManager;
import cn.tofocus.lejia.domain.jdvop.JdVOPOrderManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@RequestMapping("/v1/jd/order/manager")
@RestController
public class JdOrderApiImpl extends BaseExportApiImpl
{
    @Autowired
    private JdOrderManager manager;
    
    @Autowired
    private JdAppOrderManager appOrdermanager;
    
    @Autowired
    private JdVOPOrderManager jdVOPOrderManager;
    
    @Autowired
    private ExcelHelper excelHelper;
    
    @Operation(summary = "获取订单信息列表", tags = ApiTags.JD_ORDER)
    @PostMapping(value = "/query")
    public Result<PageResult<JdOrderFullOnPage>> queryOrder(
        @RequestParam(value = "page", defaultValue = "0", required = false) @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", defaultValue = "10", required = false) @Parameter(description = "每页大小") int pagesize,
        @RequestParam(value = "startDate", required = false) @Parameter(description = "订单时间-开始") String startDate,
        @RequestParam(value = "endDate", required = false) @Parameter(description = "订单时间-结束") String endDate,
        @RequestParam(value = "status", required = false) @Parameter(description = "状态") OrderStatus status,
        @RequestParam(value = "code", required = false) @Parameter(description = "订单号") String code,
        @RequestParam(value = "mobile", required = false) @Parameter(description = "收货手机") String mobile,
        @RequestParam(value = "tags", required = false) @Parameter(description = "用户标签") List<Integer> tags)
    {
        return new Result<>(manager
            .queryOrder(page, pagesize, startDate, endDate, status, code, mobile, tags, JdOrderFullOnPage.class));
    }
    
    @Operation(summary = "获取订单信息统计金额和笔数", tags = ApiTags.JD_ORDER)
    @PostMapping(value = "/sum")
    public Result<JdOrderTotal> orderSum(
        @RequestParam(value = "startDate", required = false) @Parameter(description = "订单时间-开始") String startDate,
        @RequestParam(value = "endDate", required = false) @Parameter(description = "订单时间-结束") String endDate,
        @RequestParam(value = "status", required = false) @Parameter(description = "状态") OrderStatus status,
        @RequestParam(value = "code", required = false) @Parameter(description = "订单号") String code,
        @RequestParam(value = "mobile", required = false) @Parameter(description = "收货手机") String mobile,
        @RequestParam(value = "tags", required = false) @Parameter(description = "用户标签") List<Integer> tags)
    {
        return new Result<>(manager.orderSum(startDate, endDate, status, code, mobile, tags));
    }
    
    @Operation(summary = "读取订单信息", tags = ApiTags.JD_ORDER)
    @PostMapping(value = "/loadOrder")
    public Result<JdOrderDetails> loadOrder(@RequestParam(value = "pkey") @Parameter(description = "订单PKEY") Integer pkey)
    {
        return new Result<>(manager.loadOrder(pkey));
    }
    
    @Operation(summary = "查询配送信息", tags = AppTags.mobileJdOrderV2)
    @PostMapping(value = "/deliveryInfo")
    public Result<JdOrderDeliveryInfo> queryDeliveryInfo(
        @RequestParam(value = "pkey") @Parameter(description = "订单PKEY") Integer pkey)
    {
        return new Result<>(manager.queryDeliveryInfo(pkey));
    }
    
    @Operation(summary = "导出京东订单", tags = ApiTags.JD_ORDER)
    @PostMapping(value = "/export")
    public void exportOrder(
        @RequestParam(value = "startDate", required = false) @Parameter(description = "订单时间-开始") String startDate,
        @RequestParam(value = "endDate", required = false) @Parameter(description = "订单时间-结束") String endDate,
        @RequestParam(value = "status", required = false) @Parameter(description = "状态") OrderStatus status,
        @RequestParam(value = "code", required = false) @Parameter(description = "订单号") String code,
        @RequestParam(value = "mobile", required = false) @Parameter(description = "收货手机") String mobile,
        @RequestParam(value = "tags", required = false) @Parameter(description = "用户标签") List<Integer> tags,
        HttpServletResponse response)
    {
        List<JdOrderExcel> list = manager.exportOrder(startDate, endDate, status, code, mobile, tags);
        // HttpServletResponse 的输出流由 Servlet 容器管理，请求结束自动关闭，无需手动 close
        try
        {
            OutputStream out = response.getOutputStream();
            String fileName = java.net.URLEncoder.encode("京东订单", "UTF-8") + ".xlsx";
            response.setHeader("Content-disposition", "attachment; filename = " + fileName);
            excelHelper.exportExcel(list, "Sheet1", out, JdOrderExcel.class, null);
            out.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    @Operation(summary = "导出京东订单明细", tags = ApiTags.JD_ORDER)
    @PostMapping(value = "/export/orderLine")
    public void exportOrderLine(
        @RequestParam(value = "startDate", required = false) @Parameter(description = "订单时间-开始") String startDate,
        @RequestParam(value = "endDate", required = false) @Parameter(description = "订单时间-结束") String endDate,
        @RequestParam(value = "status", required = false) @Parameter(description = "状态") OrderStatus status,
        @RequestParam(value = "code", required = false) @Parameter(description = "订单号") String code,
        @RequestParam(value = "mobile", required = false) @Parameter(description = "收货手机") String mobile,
        @RequestParam(value = "tags", required = false) @Parameter(description = "用户标签") List<Integer> tags,
        HttpServletResponse response)
    {
        List<JdOrderLineExcel> list = manager.exportOrderLine(startDate, endDate, status, code, mobile, tags);
        // HttpServletResponse 的输出流由 Servlet 容器管理，请求结束自动关闭，无需手动 close
        try
        {
            OutputStream out = response.getOutputStream();
            String fileName = java.net.URLEncoder.encode("京东订单明细", "UTF-8") + ".xlsx";
            response.setHeader("Content-disposition", "attachment; filename = " + fileName);
            excelHelper.exportExcel(list, "Sheet1", out, JdOrderLineExcel.class, null);
            out.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    @Operation(summary = "京东销售订单统计", tags = ApiTags.JD_ORDER)
    @PostMapping(value = "/report/byOrder")
    public Result<PageResult<JdOrderReport>> reportByOrder(
        @RequestParam(value = "page", defaultValue = "0", required = false) @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", defaultValue = "10", required = false) @Parameter(description = "每页大小") int pagesize,
        @RequestParam(value = "startDate", required = false) @Parameter(description = "订单时间-开始") String startDate,
        @RequestParam(value = "endDate", required = false) @Parameter(description = "订单时间-结束") String endDate,
        @RequestParam(value = "code", required = false) @Parameter(description = "订单号") String code)
    {
        return new Result<>(manager.reportByOrder(page, pagesize, startDate, endDate, code));
    }
    
    @Operation(summary = "京东销售订单统计导出", tags = ApiTags.JD_ORDER)
    @PostMapping(value = "/report/byOrder/export")
    public void exportReportByOrder(
        @RequestParam(value = "startDate", required = false) @Parameter(description = "订单时间-开始") String startDate,
        @RequestParam(value = "endDate", required = false) @Parameter(description = "订单时间-结束") String endDate,
        @RequestParam(value = "code", required = false) @Parameter(description = "订单号") String code,
        HttpServletResponse response)
    {
        PageResult<JdOrderReport> pageResult = manager.reportByOrder(0, 50000, startDate, endDate, code);
        List<ExportJdOrderReport> list = BeanUtil.beanListFrom(ExportJdOrderReport.class, pageResult.getContent());
        exportExcel(list, response, ExportJdOrderReport.class, "京东销售订单统计");
    }
    
    @Operation(summary = "京东销售订单统计合计", tags = ApiTags.JD_ORDER)
    @PostMapping(value = "/report/byOrder/sum")
    public Result<JdOrderReport> sumReportByOrder(
        @RequestParam(value = "startDate", required = false) @Parameter(description = "订单时间-开始") String startDate,
        @RequestParam(value = "endDate", required = false) @Parameter(description = "订单时间-结束") String endDate,
        @RequestParam(value = "code", required = false) @Parameter(description = "订单号") String code)
    {
        return new Result<>(manager.sumReportByOrder(startDate, endDate, code));
    }
    
    @Operation(summary = "京东销售商品统计统计", tags = ApiTags.JD_ORDER)
    @PostMapping(value = "/report/byGoods")
    public Result<PageResult<JdOrderGoodsReport>> reportByGoods(
        @RequestParam(value = "page", defaultValue = "0", required = false) @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", defaultValue = "10", required = false) @Parameter(description = "每页大小") int pagesize,
        @RequestParam(value = "startDate", required = false) @Parameter(description = "订单时间-开始") String startDate,
        @RequestParam(value = "endDate", required = false) @Parameter(description = "订单时间-结束") String endDate,
        @RequestParam(value = "goodsName", required = false) @Parameter(description = "商品名称") String goodsName)
    {
        return new Result<>(manager.reportByGoods(page, pagesize, startDate, endDate, goodsName));
    }
    
    @Operation(summary = "京东销售商品统计导出", tags = ApiTags.JD_ORDER)
    @PostMapping(value = "/report/byGoods/export")
    public void exportReportByGoods(
        @RequestParam(value = "startDate", required = false) @Parameter(description = "订单时间-开始") String startDate,
        @RequestParam(value = "endDate", required = false) @Parameter(description = "订单时间-结束") String endDate,
        @RequestParam(value = "goodsName", required = false) @Parameter(description = "商品名称") String goodsName,
        HttpServletResponse response)
    {
        PageResult<JdOrderGoodsReport> pageResult = manager.reportByGoods(0, 10000, startDate, endDate, goodsName);
        List<ExportJdOrderGoodsReport> list =
            BeanUtil.beanListFrom(ExportJdOrderGoodsReport.class, pageResult.getContent());
        exportExcel(list, response, ExportJdOrderGoodsReport.class, "京东销售商品统计");
    }
    
    @Operation(summary = "京东销售商品统计合计", tags = ApiTags.JD_ORDER)
    @PostMapping(value = "/report/byGoods/sum")
    public Result<JdOrderGoodsReport> sumReportByGoods(
        @RequestParam(value = "startDate", required = false) @Parameter(description = "订单时间-开始") String startDate,
        @RequestParam(value = "endDate", required = false) @Parameter(description = "订单时间-结束") String endDate,
        @RequestParam(value = "goodsName", required = false) @Parameter(description = "商品名称") String goodsName)
    {
        return new Result<>(manager.sumReportByGoods(startDate, endDate, goodsName));
    }

    @Operation(summary = "手动调拆分接口-测试", tags = ApiTags.JD_ORDER)
    @PostMapping(value = "/orderSplit/test")
    public Result<Boolean> orderSplit(@RequestParam(value = "pOrder")Long pOrder)
    {
//        appOrdermanager.orderSplit(pOrder);
        jdVOPOrderManager.queryOrderDetail(pOrder, null);
        return new Result<>();
    }

    @Operation(summary = "京东完成订单-测试", tags = ApiTags.JD_ORDER)
    @PostMapping(value = "/confirmReceiveByOrder/test")
    public Result<Boolean> confirmReceiveByOrder(@RequestParam(value = "orderPkey")Integer orderPkey)
    {
        appOrdermanager.confirmReceiveByOrderTest(orderPkey);
        return new Result<>();
    }
}
