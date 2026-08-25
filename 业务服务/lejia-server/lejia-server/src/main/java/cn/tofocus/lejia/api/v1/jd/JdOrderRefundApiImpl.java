package cn.tofocus.lejia.api.v1.jd;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.common.excel.ExcelHelper;
import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.core.Result;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.jd.JdOrderRefundExcel;
import cn.tofocus.lejia.bean.dto.jd.JdRefundOrderDetails;
import cn.tofocus.lejia.bean.dto.refund.OrderRefundOnInfo;
import cn.tofocus.lejia.bean.enums.RefundStatus;
import cn.tofocus.lejia.domain.jd.JdOrderRefundManager;
import cn.tofocus.lejia.domain.jdvop.JdVOPOrderManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@RequestMapping("/v1/jd/order/refund/manager")
@RestController
public class JdOrderRefundApiImpl
{
    @Autowired
    private JdOrderRefundManager manager;
    
    @Autowired
    private ExcelHelper excelHelper;
    
    @Autowired
    private JdVOPOrderManager jdVOPOrderManager;
    
    @Operation(summary = "获取市场退款订单列表", tags = ApiTags.JD_ORDER_REFUND)
    @PostMapping("/query")
    public Result<OrderRefundOnInfo> queryOrderRefund(
        @RequestParam(value = "page", defaultValue = "0", required = false) int page, 
        @RequestParam(value = "pagesize", defaultValue = "0", required = false) int pagesize, 
        @RequestParam(value = "code", required = false) String code, 
        @RequestParam(value = "status", required = false) List<RefundStatus> status, 
        @RequestParam(value = "startDate", required = false) String startDate, 
        @RequestParam(value = "endDate", required = false) String endDate)
    {
        return new Result<>(manager.queryOrderRefund(page, pagesize, code, status, startDate, endDate));
    }
    
    @Operation(summary = "查看退款订单详情", tags = ApiTags.JD_ORDER_REFUND)
    @PostMapping("/get")
    public Result<JdRefundOrderDetails> getRefund(@RequestParam(value = "pkey")
    @Parameter(description = "退款订单主键") Integer pkey)
    {
        return new Result<>(manager.getRefundOrder(pkey));
    }
    
    @Operation(summary = "同意退款", tags = ApiTags.WEB_REFUND)
    @PostMapping("/agree")
    public Result<Boolean> agreeRefund(@RequestParam(value = "pkey") @Parameter(description = "退款订单主键") Integer pkey, 
        @RequestParam(value = "delDesc", required = false) @Parameter(description = "处理意见,可不填") String delDesc)
    {
        return new Result<>(manager.agreeRefund(pkey, delDesc));
    }
    
    @Operation(summary = "测试退款", tags = ApiTags.WEB_REFUND)
    @PostMapping("/cancelOrder/test")
    public Result<Boolean> testCancelOrder(
        @RequestParam(value = "jdCode") Long jdCode, 
        @RequestParam(value = "code") String code, 
        @RequestParam(value = "cancelReason") String cancelReason)
    {
        jdVOPOrderManager.cancelOrder(jdCode, code, cancelReason);
        return new Result<>(true);
    }

    @Operation(summary = "测试退款2", tags = ApiTags.WEB_REFUND)
    @PostMapping("/refund/test")
    public Result<Boolean> testRefund(@RequestParam(value = "jdCode") Long jdCode)
    {
        manager.testRefund(jdCode);
        return new Result<>(true);
    }
    
    @Operation(summary = "拒绝退款", tags = ApiTags.JD_ORDER_REFUND)
    @PostMapping("/refuse")
    public Result<Boolean> refuseRefund(
        @RequestParam(value = "pkey") @Parameter(description = "退款订单主键") Integer pkey, 
        @RequestParam(value = "delDesc", required = false) @Parameter(description = "处理意见,可不填") String delDesc)
    {
        return new Result<>(manager.refuseRefund(pkey, delDesc));
    }
    
    @Operation(summary = "导出市场退款订单列表EXCEL", tags = ApiTags.WEB_REFUND)
    @PostMapping("/export")
    public Result<Boolean> queryOrderRefund(@RequestParam(value = "code", required = false)String code, 
        @RequestParam(value = "status", required = false) List<RefundStatus> status, 
        @RequestParam(value = "startDate", required = false) String startDate, 
        @RequestParam(value = "endDate", required = false) String endDate, HttpServletResponse response)
    {
        OrderRefundOnInfo info = manager.queryOrderRefund(0, 50000, code, status, startDate, endDate);
        OutputStream out = null;
        try
        {
            String fileName = java.net.URLEncoder.encode("京东退款订单", "UTF-8") + ".xlsx";
            response.setHeader("Content-disposition", "attachment; filename = " + fileName);
            out = response.getOutputStream();
            excelHelper.exportExcel(BeanUtil.beanListFrom(JdOrderRefundExcel.class,
                info.getOnPage().getContent()), "Sheet1", out, JdOrderRefundExcel.class, null);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return new Result<>(true);
    }
}
