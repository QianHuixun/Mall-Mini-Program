package cn.tofocus.lejia.api.v1.sys;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import cn.tofocus.lejia.api.BaseExportApiImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.common.excel.ExcelHelper;
import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.data.AbnormalExcel;
import cn.tofocus.lejia.bean.dto.data.CardExcel;
import cn.tofocus.lejia.bean.dto.data.CommsDetailExcel;
import cn.tofocus.lejia.bean.dto.data.CommsExcel;
import cn.tofocus.lejia.bean.dto.data.CompanyExcel;
import cn.tofocus.lejia.bean.dto.data.DrawWinExcel;
import cn.tofocus.lejia.bean.dto.data.ExpressCourierExcel;
import cn.tofocus.lejia.bean.dto.data.FarmerExcel;
import cn.tofocus.lejia.bean.dto.data.GoodsIntegralSalesExcel;
import cn.tofocus.lejia.bean.dto.data.GoodsSalesExcel;
import cn.tofocus.lejia.bean.dto.data.GoodsTypeExcel;
import cn.tofocus.lejia.bean.dto.data.HourSalesExcel;
import cn.tofocus.lejia.bean.dto.data.PaidMemberExcel;
import cn.tofocus.lejia.bean.dto.data.PostageExcel;
import cn.tofocus.lejia.bean.dto.data.SpecialAreaExcel;
import cn.tofocus.lejia.bean.dto.data.SpecialAreaOnPage;
import cn.tofocus.lejia.bean.dto.data.VendorSalesExcel;
import cn.tofocus.lejia.bean.dto.excel.goods.ExportGoodsLineSummary;
import cn.tofocus.lejia.bean.dto.excel.market.ExportMktSupplierOrderLineOnPage;
import cn.tofocus.lejia.bean.dto.excel.market.ExportMktSupplierSaleSummary;
import cn.tofocus.lejia.bean.dto.excel.order.ExportMktGoodsOrderLineOnPage;
import cn.tofocus.lejia.bean.dto.goods.GoodsLineSum;
import cn.tofocus.lejia.bean.dto.goods.GoodsLineSummary;
import cn.tofocus.lejia.bean.dto.market.CommsDetailOnPage;
import cn.tofocus.lejia.bean.dto.market.DropStringDown;
import cn.tofocus.lejia.bean.dto.market.MktMemberOnList;
import cn.tofocus.lejia.bean.dto.market.MktSupplierSaleSummary;
import cn.tofocus.lejia.bean.dto.order.MktGoodsOrderLineOnPage;
import cn.tofocus.lejia.bean.dto.order.MktGoodsOrderLineSummary;
import cn.tofocus.lejia.bean.dto.order.MktSupplierOrderLineOnPage;
import cn.tofocus.lejia.bean.dto.sys.FarmerOption;
import cn.tofocus.lejia.bean.enums.LevelType;
import cn.tofocus.lejia.bean.enums.OrderStatus;
import cn.tofocus.lejia.bean.enums.PayType;
import cn.tofocus.lejia.domain.DataCenterManager;
import cn.tofocus.lejia.domain.market.MemberManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@RequestMapping("/v1/sys/data/center")
@RestController
public class LejiaDataCenterApiImpl extends BaseExportApiImpl implements LejiaDataCenterApi
{
    @Autowired
    private DataCenterManager manager;
    
    @Autowired
    private MemberManager memerbManager;
    
    @Override
    public Result<PageResult<SpecialAreaOnPage>> mTypeData(int page, int pagesize, String marketPkey,
        String companyPkey, String startTime, String endTime)
    {
        return new Result<>(manager.mTypeData(marketPkey, companyPkey, startTime, endTime, page, pagesize));
    }
    
    @Override
    public Result<PageResult<Map<String, Object>>> goodsData(int page, int pagesize, String marketPkey,
        String companyPkey, String startTime, String endTime)
    {
        
        return new Result<>(manager.goodsData(marketPkey, companyPkey, startTime, endTime, page, pagesize));
    }
    
    @Override
    public Result<List<Map<String, Object>>> goodsAnalysis(Integer goodsPkey, String startTime, String endTime)
    {
        
        return new Result<>(manager.goodsAnalysis(goodsPkey, startTime, endTime));
    }
    
    @Override
    public Result<PageResult<Map<String, Object>>> goodsAbnormal(int page, int pagesize)
    {
        
        return new Result<>(manager.goodsAbnormal(page, pagesize));
    }
    
    @Override
    public Result<List<Map<String, Object>>> drawWin()
    {
        
        return new Result<>(manager.drawWin());
    }
    
    @Override
    public Result<List<Map<String, Object>>> goodsHourAnalysis(Integer goodsPkey, String time)
    {
        return new Result<>(manager.goodsHourAnalysis(goodsPkey, time));
    }
    
    @Override
    public Result<PageResult<Map<String, Object>>> goodsHourDetail(int page, int pagesize, Integer goodsPkey,
        String time)
    {
        
        return new Result<>(manager.goodsHourDetail(goodsPkey, time, page, pagesize));
    }
    
    @Override
    public Result<List<Map<String, Object>>> annualMemberPay(String startTime, String endTime)
    {
        return new Result<>(manager.annualMemberPay(startTime, endTime));
    }
    
    @Override
    public Result<PageResult<Map<String, Object>>> goodsIntegralSales(int page, int pagesize, String startTime,
        String endTime)
    {
        return new Result<>(manager.goodsIntegralSales(startTime, endTime, page, pagesize));
    }
    
    @Override
    public Result<PageResult<Map<String, Object>>> memberGoodsSales(int page, int pagesize, String startTime,
        String endTime)
    {
        return new Result<>(manager.memberGoodsSales(startTime, endTime, page, pagesize));
    }
    
    @Override
    public Result<List<Map<String, Object>>> getAddMemberCount(String startTime, String endTime)
    {
        return new Result<>(manager.getAddMemberCount(startTime, endTime));
    }
    
    @Override
    public Result<PageResult<Map<String, Object>>> queryFarmerCardCount(int page, int pagesize, String marketPkey,
        String companyPkey, String startTime, String endTime)
    {
        return new Result<>(manager.queryFarmerCardCount(marketPkey, companyPkey, startTime, endTime, page, pagesize));
    }
    
    @Override
    public Result<PageResult<Map<String, Object>>> goodsTypeSales(int page, int pagesize, String marketPkey,
        String companyPkey, String startTime, String endTime)
    {
        return new Result<>(manager.goodsTypeSales(marketPkey, companyPkey, startTime, endTime, page, pagesize));
    }
    
    @Override
    public Result<PageResult<Map<String, Object>>> vendorSales(int page, int pagesize, String vendorName,
        String startTime, String endTime)
    {
        return new Result<>(manager.vendorSales(vendorName, startTime, endTime, page, pagesize));
    }
    
    @Override
    public Result<PageResult<Map<String, Object>>> getFarmerSales(int page, int pagesize, String marketPkey,
        String companyPkey, String startTime, String endTime)
    {
        return new Result<>(manager.getFarmerSales(marketPkey, companyPkey, startTime, endTime, page, pagesize));
    }
    
    @Override
    public Result<PageResult<Map<String, Object>>> getCompanySales(int page, int pagesize, String companyPkey,
        String startTime, String endTime)
    {
        return new Result<>(manager.getCompanySales(companyPkey, startTime, endTime, page, pagesize));
    }
    
    @Override
    public Result<PageResult<Map<String, Object>>> getExpressCourierCount(int page, int pagesize, String startTime,
        String endTime)
    {
        return new Result<>(manager.getExpressCourierCount(startTime, endTime, page, pagesize));
    }
    
    @Override
    public Result<List<Map<String, Object>>> getPostageCount(String startTime, String endTime)
    {
        return new Result<>(manager.getPostageCount(startTime, endTime));
    }
    
    @Override
    public Result<List<Map<String, Object>>> getMallAccessNum(String startTime, String endTime)
    {
        return new Result<>(manager.getMallAccessNum(startTime, endTime));
    }
    
    @Override
    public Result<PageResult<Map<String, Object>>> getComms(int page, int pagesize, String memberName)
    {
        return new Result<>(manager.getComms(memberName, page, pagesize));
    }
    
    @Override
    public Result<PageResult<CommsDetailOnPage>> getCommsDetail(int page, int pagesize, String startTime,
        String endTime)
    {
        return new Result<>(manager.getCommsDetail(startTime, endTime, page, pagesize));
    }
    
    @Override
    public Result<Map<String, Object>> getForeignDetail(String startTime, String endTime)
    {
        return new Result<>(manager.getForeignDetail(startTime, endTime));
    }
    
    @Override
    public Result<Map<String, Object>> getForeignDetailOrder()
    {
        return new Result<>(manager.getForeignDetailOrder());
    }

    @Override
    public Result<PageResult<MktSupplierSaleSummary>> getSupplierSales(int page, int pagesize, String startTime,
        String endTime, String supplierName)
    {
        return new Result<>(manager.getSupplierSales(page, pagesize, startTime, endTime, supplierName));
    }
    
    @Override
    public Result<MktSupplierSaleSummary> sumSupplierSales(String startTime, String endTime, String supplierName)
    {
        return new Result<>(manager.sumSupplierSales(startTime, endTime, supplierName));
    }
    
    @Override
    public Result<PageResult<MktSupplierOrderLineOnPage>> querySupplierOrderLine(int page, int pagesize,
        String startTime, String endTime, String kcCode, String supplierName, String goodsName, List<PayType> payTypes, List<Integer> tags)
    {
        return new Result<>(
            manager.querySupplierOrderLine(page, pagesize, startTime, endTime, kcCode, supplierName, goodsName, payTypes, tags));
    }
    
    @Override
    public Result<BigDecimal> sumSupplierOrderLine(String startTime, String endTime, String kcCode, String supplierName,
        String goodsName, List<PayType> payTypes, List<Integer> tags)
    {
        return new Result<>(manager.sumSupplierOrderLine(startTime, endTime, kcCode, supplierName, goodsName, payTypes, tags));
    }
    
    @Override
    public Result<List<FarmerOption>> listFarmerOptions()
    {
        return new Result<>(manager.listFarmerOptions());
    }
    
    @Override
    public Result<PageResult<GoodsLineSummary>> goodsLineSummary(int page, int pagesize, String startTime,
        String endTime, String goodsName, String farmer)
    {
        return new Result<>(manager.goodsLineSummary(page, pagesize, startTime, endTime, goodsName, farmer));
    }
    
    @Override
    public Result<GoodsLineSum> goodsLineSum(String startTime, String endTime, String goodsName, String farmer)
    {
        return new Result<>(manager.goodsLineSum(startTime, endTime, goodsName, farmer));
    }
    
    @Override
    public Result<PageResult<MktGoodsOrderLineOnPage>> queryGoodsOrderLine(int page, int pagesize, String startTime,
        String endTime, String kcCode, String memberMobile, OrderStatus status, Integer deliveryType, Integer goods,
        String goodsName, Integer space)
    {
        return new Result<>(manager.queryGoodsOrderLine(page,
            pagesize,
            startTime,
            endTime,
            kcCode,
            memberMobile,
            status,
            deliveryType,
            goods,
            goodsName,
            space));
    }
    
    @Override
    public Result<MktGoodsOrderLineSummary> sumGoodsOrderLine(String startTime, String endTime, String kcCode,
        String memberMobile, OrderStatus status, Integer deliveryType, Integer goods, String goodsName, Integer space)
    {
        return new Result<>(manager.sumGoodsOrderLine(startTime,
            endTime,
            kcCode,
            memberMobile,
            status,
            deliveryType,
            goods,
            goodsName,
            space));
    }



    @Operation(summary = "导出商品明细统计", tags = ApiTags.custDataCenter)
    @PostMapping(value = "/goods/line/export")
    public void exportGoodsLineSummary(
        @RequestParam(value = "startTime", required = false) @Parameter(description = "开始时间yyyy-MM-dd") String startTime,
        @RequestParam(value = "endTime", required = false) @Parameter(description = "结束时间yyyy-MM-dd") String endTime,
        @RequestParam(value = "goodsName", required = false) @Parameter(description = "商品名称") String goodsName,
        @RequestParam(value = "farmer", required = false) @Parameter(description = "市场/运营端主键") String farmer,
        HttpServletResponse response)
    {
        PageResult<GoodsLineSummary> pageResult =
            manager.goodsLineSummary(0, 10000, startTime, endTime, goodsName, farmer);
        List<ExportGoodsLineSummary> list =
            BeanUtil.beanListFrom(ExportGoodsLineSummary.class, pageResult.getContent());
        exportExcel(list, response, ExportGoodsLineSummary.class, "商品明细统计");
    }
    
    @Operation(summary = "导出商品明细统计-明细", tags = ApiTags.custDataCenter)
    @PostMapping(value = "/goods/order/line/export")
    public void exportGoodsOrderLine(
        @RequestParam(value = "startTime", required = false) @Parameter(description = "开始时间yyyy-MM-dd") String startTime,
        @RequestParam(value = "endTime", required = false) @Parameter(description = "结束时间yyyy-MM-dd") String endTime,
        @RequestParam(value = "kcCode", required = false) @Parameter(description = "订单编号") String kcCode,
        @RequestParam(value = "memberMobile", required = false) @Parameter(description = "用户手机号") String memberMobile,
        @RequestParam(value = "status", required = false) @Parameter(description = "订单状态") OrderStatus status,
        @RequestParam(value = "deliveryType", required = false) @Parameter(description = "配送方式（1：配送，2：自提）") Integer deliveryType,
        @RequestParam(value = "goods", required = false) @Parameter(description = "商品主键") Integer goods,
        @RequestParam(value = "goodsName", required = false) @Parameter(description = "商品名称") String goodsName,
        @RequestParam(value = "space", required = false) @Parameter(description = "规格") Integer space,
        HttpServletResponse response)
    {
        PageResult<MktGoodsOrderLineOnPage> pageResult = manager.queryGoodsOrderLine(0,
            10000,
            startTime,
            endTime,
            kcCode,
            memberMobile,
            status,
            deliveryType,
            goods,
            goodsName,
            space);
        List<ExportMktGoodsOrderLineOnPage> list =
            BeanUtil.beanListFrom(ExportMktGoodsOrderLineOnPage.class, pageResult.getContent());
        exportExcel(list, response, ExportMktGoodsOrderLineOnPage.class, "商品明细统计-明细");
    }

    @Operation(summary = "导出供应商销售统计", tags = ApiTags.custDataCenter)
    @PostMapping(value = "/supplier/sales/export")
    public void exportSupplierSales(
        @RequestParam(value = "startTime", required = false) @Parameter(description = "开始时间yyyy-MM-dd") String startTime,
        @RequestParam(value = "endTime", required = false) @Parameter(description = "结束时间yyyy-MM-dd") String endTime,
        @RequestParam(value = "supplierName", required = false) @Parameter(description = "供应商名称") String supplierName,
        HttpServletResponse response)
    {
        PageResult<MktSupplierSaleSummary> pageResult =
            manager.getSupplierSales(0, 10000, startTime, endTime, supplierName);
        List<ExportMktSupplierSaleSummary> list =
            BeanUtil.beanListFrom(ExportMktSupplierSaleSummary.class, pageResult.getContent());
        exportExcel(list, response, ExportMktSupplierSaleSummary.class, "供应商销售统计");
    }
    
    @Operation(summary = "导出供应商交易明细查询", tags = ApiTags.custDataCenter)
    @PostMapping(value = "/supplier/order/line/export")
    public void exportSupplierOrderLine(
        @RequestParam(value = "startTime", required = false) @Parameter(description = "开始时间yyyy-MM-dd") String startTime,
        @RequestParam(value = "endTime", required = false) @Parameter(description = "结束时间yyyy-MM-dd") String endTime,
        @RequestParam(value = "kcCode", required = false) @Parameter(description = "订单编号") String kcCode,
        @RequestParam(value = "supplierName", required = false) @Parameter(description = "供应商名称") String supplierName,
        @RequestParam(value = "goodsName", required = false) @Parameter(description = "商品名称") String goodsName,
        @RequestParam(value = "payTypes", required = false) @Parameter(description = "支付方式") List<PayType> payTypes, 
        @RequestParam(value = "tags", required = false) @Parameter(description = "用户标签") List<Integer> tags,
        HttpServletResponse response)
    {
        PageResult<MktSupplierOrderLineOnPage> pageResult =
            manager.querySupplierOrderLine(0, 10000, startTime, endTime, kcCode, supplierName, goodsName, payTypes, tags);
        List<ExportMktSupplierOrderLineOnPage> list =
            BeanUtil.beanListFrom(ExportMktSupplierOrderLineOnPage.class, pageResult.getContent());
        exportExcel(list, response, ExportMktSupplierOrderLineOnPage.class, "供应商销售明细统计");
    }
    
    @Operation(summary = "导出专区营业报表", tags = ApiTags.custDataCenter)
    @PostMapping(value = "/export/mtype")
    public void exportMtype(@RequestParam(value = "marketPkey", required = false) String marketPkey,
        @RequestParam(value = "companyPkey", required = false) String companyPkey,
        @RequestParam(value = "startTime", required = false) String startTime,
        @RequestParam(value = "endTime", required = false) String endTime, HttpServletResponse response)
    {
        PageResult<SpecialAreaOnPage> pageResult =
            manager.mTypeData(marketPkey, companyPkey, startTime, endTime, 0, 100000);
        List<SpecialAreaExcel> list = new ArrayList<>();
        for (SpecialAreaOnPage sa : pageResult.getContent())
        {
            SpecialAreaExcel e = new SpecialAreaExcel();
            e.setName(sa.getName());
            e.setSales(sa.getSales());
            e.setSalesNum(sa.getSalesNum());
            list.add(e);
        }
        exportExcel(list, response, SpecialAreaExcel.class, "专区营业报表");
    }
    
    @Operation(summary = "导出商品销售统计报表", tags = ApiTags.custDataCenter)
    @PostMapping(value = "/export/goods")
    public void exportGoods(@RequestParam(value = "marketPkey", required = false) String marketPkey,
        @RequestParam(value = "companyPkey", required = false) String companyPkey,
        @RequestParam(value = "startTime", required = false) String startTime,
        @RequestParam(value = "endTime", required = false) String endTime, HttpServletResponse response)
    {
        PageResult<Map<String, Object>> pageResult =
            manager.goodsData(marketPkey, companyPkey, startTime, endTime, 0, 100000);
        List<GoodsSalesExcel> list = new ArrayList<>();
        for (Map<String, Object> m : pageResult.getContent())
        {
            GoodsSalesExcel gs = new GoodsSalesExcel();
            gs.setName(m.get("name").toString());
            gs.setSales(m.get("Sales").toString());
            gs.setSalesNum(m.get("SalesNum").toString());
            list.add(gs);
        }
        exportExcel(list, response, GoodsSalesExcel.class, "商品销售统计");
    }
    
    @Operation(summary = "导出奖品统计", tags = ApiTags.custDataCenter)
    @PostMapping(value = "/export/drawwin")
    public void exportDrawWin(HttpServletResponse response)
    {
        List<Map<String, Object>> drawWin = manager.drawWin();
        List<DrawWinExcel> list = new ArrayList<>();
        for (Map<String, Object> m : drawWin)
        {
            DrawWinExcel gs = new DrawWinExcel();
            gs.setName(m.get("name").toString());
            gs.setType(m.get("type").toString());
            gs.setNum(m.get("num").toString());
            list.add(gs);
        }
        exportExcel(list, response, GoodsSalesExcel.class, "奖品统计");
    }
    
    @Operation(summary = "导出时间段销售额", tags = ApiTags.custDataCenter)
    @PostMapping(value = "/export/goods/hour/detail")
    public void exportGoodsHourDetail(@RequestParam(value = "goodsPkey", required = false) Integer goodsPkey,
        @RequestParam(value = "time", required = false) String time, HttpServletResponse response)
    {
        PageResult<Map<String, Object>> pageResult = manager.goodsHourDetail(goodsPkey, time, 0, 100000);
        List<HourSalesExcel> list = new ArrayList<>();
        for (Map<String, Object> m : pageResult.getContent())
        {
            HourSalesExcel gs = new HourSalesExcel();
            gs.setKcCode(m.get("kcCode").toString());
            gs.setName(m.get("name").toString());
            gs.setNum(m.get("num").toString());
            gs.setPricen(m.get("pricen").toString());
            gs.setCreatedTime(m.get("createdTime").toString());
            list.add(gs);
        }
        exportExcel(list, response, HourSalesExcel.class, "时间段销售额");
    }
    
    @Operation(summary = "导出付费会员明细", tags = ApiTags.custDataCenter)
    @PostMapping("/export/member/paid")
    public void queryMember(@RequestParam(value = "name", required = false) @Parameter(description = "名称") String name,
        @RequestParam(value = "mobile", required = false) @Parameter(description = "手机") String mobile, HttpServletResponse response)
    {
        PageResult<MktMemberOnList> pageResult =
            memerbManager.queryMember(0, 100000, LevelType.PAID_MEMBER, name, mobile);
        List<PaidMemberExcel> list = BeanUtil.beanListFrom(PaidMemberExcel.class, pageResult.getContent());
        exportExcel(list, response, PaidMemberExcel.class, "付费会员明细");
    }
    
    @Operation(summary = "导出积分兑换统计报表", tags = ApiTags.custDataCenter)
    @PostMapping(value = "/export/goods/integral/sales")
    public void exportGoodsIntegralSales(@RequestParam(value = "startTime", required = false) String startTime,
        @RequestParam(value = "endTime", required = false) String endTime, HttpServletResponse response)
    {
        PageResult<Map<String, Object>> pageResult = manager.goodsIntegralSales(startTime, endTime, 0, 100000);
        List<GoodsIntegralSalesExcel> list = new ArrayList<>();
        for (Map<String, Object> m : pageResult.getContent())
        {
            GoodsIntegralSalesExcel gs = new GoodsIntegralSalesExcel();
            gs.setName(m.get("name").toString());
            gs.setSales(m.get("Sales").toString());
            gs.setSalesNum(m.get("SalesNum").toString());
            gs.setPointn(m.get("pointn").toString());
            list.add(gs);
        }
        exportExcel(list, response, GoodsIntegralSalesExcel.class, "积分兑换统计");
    }
    
    @Operation(summary = "导出付费会员消费报表", tags = ApiTags.custDataCenter)
    @PostMapping(value = "/export/member/goods/sales")
    public void exportMemberGoodsSales(@RequestParam(value = "startTime", required = false) String startTime,
        @RequestParam(value = "endTime", required = false) String endTime, HttpServletResponse response)
    {
        PageResult<Map<String, Object>> pageResult = manager.memberGoodsSales(startTime, endTime, 0, 10000);
        List<GoodsSalesExcel> list = new ArrayList<>();
        if(pageResult != null && pageResult.getContent() != null)
        {
            for (Map<String, Object> m : pageResult.getContent())
            {
                GoodsSalesExcel gs = new GoodsSalesExcel();
                gs.setName(m.get("name").toString());
                gs.setSales(m.get("Sales").toString());
                gs.setSalesNum(m.get("SalesNum").toString());
                list.add(gs);
            }
        }
        exportExcel(list, response, GoodsSalesExcel.class, "付费会员消费");
    }
    
    @Operation(summary = "导出卡券使用统计报表", tags = ApiTags.custDataCenter)
    @PostMapping(value = "/export/farmer/card")
    public void exportFarmerCardCount(@RequestParam(value = "marketPkey", required = false) String marketPkey,
        @RequestParam(value = "companyPkey", required = false) String companyPkey,
        @RequestParam(value = "startTime", required = false) String startTime,
        @RequestParam(value = "endTime", required = false) String endTime, HttpServletResponse response)
    {
        PageResult<Map<String, Object>> pageResult =
            manager.queryFarmerCardCount(marketPkey, companyPkey, startTime, endTime, 0, 100000);
        List<CardExcel> list = new ArrayList<>();
        for (Map<String, Object> m : pageResult.getContent())
        {
            CardExcel gs = new CardExcel();
            if(m.containsKey("name"))
                gs.setName(m.get("name").toString());
            if(m.containsKey("num"))
                gs.setNum(m.get("num").toString());
            if(m.containsKey("cardPrice"))
                gs.setCardPrice(m.get("cardPrice").toString());
            list.add(gs);
        }
        exportExcel(list, response, CardExcel.class, "卡券使用统计");
        
    }
    
    @Operation(summary = "导出品类销售统计报表", tags = ApiTags.custDataCenter)
    @PostMapping(value = "/export/goods/type/sales")
    public void exportGoodsTypeSales(@RequestParam(value = "marketPkey", required = false) String marketPkey,
        @RequestParam(value = "companyPkey", required = false) String companyPkey,
        @RequestParam(value = "startTime", required = false) String startTime,
        @RequestParam(value = "endTime", required = false) String endTime, HttpServletResponse response)
    {
        PageResult<Map<String, Object>> pageResult =
            manager.goodsTypeSales(marketPkey, companyPkey, startTime, endTime, 0, 100000);
        List<GoodsTypeExcel> list = new ArrayList<>();
        for (Map<String, Object> m : pageResult.getContent())
        {
            GoodsTypeExcel gs = new GoodsTypeExcel();
            gs.setName(m.get("name").toString());
            gs.setSales(m.get("Sales").toString());
            gs.setSalesNum(m.get("SalesNum").toString());
            list.add(gs);
        }
        exportExcel(list, response, GoodsTypeExcel.class, "品类销售统计");
    }
    
    @Operation(summary = "导出商户积分统计报表", tags = ApiTags.custDataCenter)
    @PostMapping(value = "/export/vendor/sales")
    public void exportVendorSales(@RequestParam(value = "vendorName", required = false) String vendorName,
        @RequestParam(value = "startTime", required = false) String startTime,
        @RequestParam(value = "endTime", required = false) String endTime, HttpServletResponse response)
    {
        PageResult<Map<String, Object>> pageResult = manager.vendorSales(vendorName, startTime, endTime, 0, 100000);
        List<VendorSalesExcel> list = new ArrayList<>();
        int i = 1;
        for (Map<String, Object> m : pageResult.getContent())
        {
            VendorSalesExcel gs = new VendorSalesExcel();
            gs.setRank(i);
            gs.setName(m.get("name").toString());
            gs.setMobile(m.get("mobile").toString());
            gs.setPointSum(m.get("pointSum").toString());
            list.add(gs);
            i++;
        }
        exportExcel(list, response, VendorSalesExcel.class, "商户积分统计");
    }
    
    @Operation(summary = "导出公司销售统计报表", tags = ApiTags.custDataCenter)
    @PostMapping(value = "/export/company/sales")
    public void exportCompanySales(@RequestParam(value = "companyPkey", required = false) String companyPkey,
        @RequestParam(value = "startTime", required = false) String startTime,
        @RequestParam(value = "endTime", required = false) String endTime, HttpServletResponse response)
    {
        PageResult<Map<String, Object>> pageResult =
            manager.getCompanySales(companyPkey, startTime, endTime, 0, 100000);
        List<CompanyExcel> list = new ArrayList<>();
        for (Map<String, Object> m : pageResult.getContent())
        {
            CompanyExcel gs = new CompanyExcel();
            gs.setName(m.get("companyName").toString());
            gs.setSales(m.get("Sales").toString());
            gs.setSalesNum(m.get("SalesNum").toString());
            list.add(gs);
        }
        exportExcel(list, response, CompanyExcel.class, "公司销售统计");
    }
    
    @Operation(summary = "导出市场销售统计报表", tags = ApiTags.custDataCenter)
    @PostMapping(value = "/export/farmer/sales")
    public void exportFarmerSales(@RequestParam(value = "marketPkey", required = false) String marketPkey,
        @RequestParam(value = "companyPkey", required = false) String companyPkey,
        @RequestParam(value = "startTime", required = false) String startTime,
        @RequestParam(value = "endTime", required = false) String endTime, HttpServletResponse response)
    {
        PageResult<Map<String, Object>> pageResult =
            manager.getFarmerSales(marketPkey, companyPkey, startTime, endTime, 0, 100000);
        List<FarmerExcel> list = new ArrayList<>();
        for (Map<String, Object> m : pageResult.getContent())
        {
            FarmerExcel gs = new FarmerExcel();
            gs.setName(m.get("farmerName").toString());
            gs.setCompanyName(m.get("companyName").toString());
            gs.setSales(m.get("Sales").toString());
            gs.setSalesNum(m.get("SalesNum").toString());
            list.add(gs);
        }
        exportExcel(list, response, FarmerExcel.class, "市场销售统计");
    }
    
    @Operation(summary = "导出运费报表", tags = ApiTags.custDataCenter)
    @PostMapping(value = "/export/postage/count")
    public void exportPostageCount(@RequestParam(value = "startTime", required = false) String startTime,
        @RequestParam(value = "endTime", required = false) String endTime, HttpServletResponse response)
    {
        List<Map<String, Object>> postageCount = manager.getPostageCount(startTime, endTime);
        List<PostageExcel> list = new ArrayList<>();
        for (Map<String, Object> m : postageCount)
        {
            PostageExcel gs = new PostageExcel();
            gs.setName(m.get("name").toString());
            gs.setCount(m.get("count").toString());
            gs.setPostageSum(m.get("postageSum").toString());
            list.add(gs);
        }
        exportExcel(list, response, PostageExcel.class, "运费报表");
    }
    
    @Operation(summary = "导出异常货品分析", tags = ApiTags.custDataCenter)
    @PostMapping(value = "/export/goods/abnormal")
    public void exportGoodsAbnormal(HttpServletResponse response)
    {
        PageResult<Map<String, Object>> pageResult = manager.goodsAbnormal(0, 100000);
        List<AbnormalExcel> list = new ArrayList<>();
        for (Map<String, Object> m : pageResult.getContent())
        {
            AbnormalExcel gs = new AbnormalExcel();
            gs.setName(m.get("name").toString());
            gs.setSales(m.get("Sales").toString());
            gs.setSalesNum(m.get("SalesNum").toString());
            list.add(gs);
        }
        exportExcel(list, response, AbnormalExcel.class, "异常货品分析");
    }
    
    @Operation(summary = "导出佣金达人报表", tags = ApiTags.custDataCenter)
    @PostMapping(value = "/export/comms")
    public void exportComms(@RequestParam(value = "memberName", required = false) String memberName,
        HttpServletResponse response)
    {
        PageResult<Map<String, Object>> pageResult = manager.getComms(memberName, 0, 100000);
        List<CommsExcel> list = new ArrayList<>();
        for (Map<String, Object> m : pageResult.getContent())
        {
            CommsExcel gs = new CommsExcel();
            gs.setName(m.get("name").toString());
            gs.setGoodsNum(m.get("goodsNum").toString());
            gs.setBuyNum(m.get("buyNum").toString());
            if (m.containsKey("comms")) gs.setComms(m.get("comms").toString());
            list.add(gs);
        }
        exportExcel(list, response, CommsExcel.class, "佣金达人");
    }
    
    @Operation(summary = "导出佣金收入明细报表", tags = ApiTags.custDataCenter)
    @PostMapping(value = "/export/comms/detail")
    public void exportCommsDetail(@RequestParam(value = "startTime", required = false) String startTime,
        @RequestParam(value = "endTime", required = false) String endTime, HttpServletResponse response)
    {
        PageResult<CommsDetailOnPage> pageResult = manager.getCommsDetail(startTime, endTime, 0, 100000);
        List<CommsDetailExcel> list = BeanUtil.beanListFrom(CommsDetailExcel.class, pageResult.getContent());
        int i = 1;
        for (CommsDetailExcel cd : list)
        {
            cd.setRank(i);
            i++;
        }
        exportExcel(list, response, CommsDetailExcel.class, "佣金收入明细");
    }
    
    @Operation(summary = "导出配送员绩效表报表", tags = ApiTags.custDataCenter)
    @PostMapping(value = "/export/express/courier/count")
    public void exportExpressCourierCount(@RequestParam(value = "startTime", required = false) String startTime,
        @RequestParam(value = "endTime", required = false) String endTime, HttpServletResponse response)
    {
        PageResult<Map<String, Object>> pageResult = manager.getExpressCourierCount(startTime, endTime, 0, 100000);
        List<ExpressCourierExcel> list = new ArrayList<>();
        int i = 1;
        for (Map<String, Object> m : pageResult.getContent())
        {
            ExpressCourierExcel gs = new ExpressCourierExcel();
            gs.setRank(i);
            gs.setName(m.get("name").toString());
            gs.setSuccessNum(m.get("successNum").toString());
            if (m.containsKey("orderNum")) gs.setOrderNum(m.get("orderNum").toString());
            list.add(gs);
            i++;
        }
        exportExcel(list, response, ExpressCourierExcel.class, "配送员绩效表");
    }

    @Override
    public Result<List<DropStringDown>> listDrop()
    {
        List<DropStringDown> res = new ArrayList<>();
        for(PayType pt : PayType.values())
        {
            DropStringDown dsd = new DropStringDown();
            dsd.setPkey(pt.name());
            dsd.setName(pt.getName());
            res.add(dsd);
        }
        return new Result<>(res);
    }
}
