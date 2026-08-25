package cn.tofocus.lejia.api.v1.vendor;

import java.util.Date;
import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.data.NamedBean;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.vendor.BankInfo;
import cn.tofocus.lejia.bean.dto.vendor.ReportInfo;
import cn.tofocus.lejia.bean.dto.vendor.SettlementInfo;
import cn.tofocus.lejia.bean.dto.vendor.SettlementProcess;
import cn.tofocus.lejia.bean.dto.vendor.VendorOrderInfo;
import cn.tofocus.lejia.bean.dto.vendor.VendorSettleDateInfo;
import cn.tofocus.lejia.bean.enums.SettlementType;
import cn.tofocus.lejia.bean.enums.v3.SettleSortType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

public interface SettlementApi
{
    @Operation(summary = "生成报表", tags = ApiTags.ZYYSC_VENDOR_ORDER)
    @PostMapping(value = "/report/query")
    public Result<ReportInfo> queryReport(@RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "pagesize", defaultValue = "10") int pagesize,
        @RequestParam(value = "startTime") String startTime, @RequestParam(value = "endTime") String endTime,
        @RequestParam(value = "marketKeys", required = false) @Parameter(description = "市场主键")List<String> marketKeys,
        @RequestParam(value = "sortType", required = false) @Parameter(description = "排序") SettleSortType sortType,
        @RequestParam(value = "sort", required = false, defaultValue = "false") @Parameter(description = "true:正序，false:倒序") Boolean sort);
    
    @Operation(summary = "结算", tags = ApiTags.ZYYSC_VENDOR_ORDER)
    @PostMapping(value = "/report/add")
    public Result<Boolean> addReport(@RequestParam(value = "queryTime") Date queryTime,
        @RequestParam(value = "startTime") String startTime, @RequestParam(value = "endTime") String endTime,
        @RequestParam(value = "rem", required = false) String rem,
        @RequestParam(value = "marketKeys", required = false) @Parameter(description = "市场主键")List<String> marketKeys);
    
    @Operation(summary = "日期区间下拉", tags = ApiTags.ZYYSC_VENDOR_ORDER)
    @PostMapping(value = "/report/settlementList")
    public Result<List<NamedBean>> settlementList(@RequestParam(value = "type", required = false) SettlementType type);
    
    @Operation(summary = "结算明细查询", tags = ApiTags.ZYYSC_VENDOR_ORDER)
    @PostMapping(value = "/report/queryLine")
    public Result<SettlementInfo> queryLine(@RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "pagesize", defaultValue = "10") int pagesize,
        @RequestParam(value = "marketKeys", required = false) @Parameter(description = "市场主键")List<String> marketKeys,
        @RequestParam(value = "startTime", required = false) String startTime, 
        @RequestParam(value = "endTime", required = false) String endTime,
        @RequestParam(value = "sortType", required = false) @Parameter(description = "排序") SettleSortType sortType,
        @RequestParam(value = "sort", required = false, defaultValue = "false") @Parameter(description = "true:正序，false:倒序") Boolean sort);
    
    @Operation(summary = "采购流程", tags = ApiTags.ZYYSC_VENDOR_ORDER)
    @PostMapping(value = "/report/process")
    public Result<List<SettlementProcess>> process(@RequestParam(value = "linePkey") Long linePkey);
    
    @Operation(summary = "商户对账", tags = ApiTags.ZYYSC_VENDOR_ORDER)
    @PostMapping(value = "/report/check")
    Result<VendorOrderInfo> check(@RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", defaultValue = "10") @Parameter(description = "每页大小") int pagesize,
        @RequestParam(value = "startDate", required = false) @Parameter(description = "付款时间-开始") String startDate,
        @RequestParam(value = "endDate", required = false) @Parameter(description = "付款时间-结束") String endDate,
        @RequestParam(value = "startSettlementDate", required = false) @Parameter(description = "结算时间-开始") String startSettlementDate,
        @RequestParam(value = "endSettlementDate", required = false) @Parameter(description = "结算时间-结束") String endSettlementDate,
        @RequestParam(value = "startVendorTime", required = false) @Parameter(description = "采购时间-开始") String startVendorTime,
        @RequestParam(value = "endVendorTime", required = false) @Parameter(description = "采购时间-结束") String endVendorTime,
        @RequestParam(value = "vendorName", required = false) @Parameter(description = "商户名称") String vendorName,
        @RequestParam(value = "booth", required = false) @Parameter(description = "摊位号") String booth,
        @RequestParam(value = "code", required = false) @Parameter(description = "订单号") String code,
        @RequestParam(value = "status", required = false) @Parameter(description = "结算状态") List<SettlementType> status);
    
    @Operation(summary = "获取已使用日期区间", tags = ApiTags.ZYYSC_VENDOR_ORDER)
    @PostMapping(value = "/report/date")
    public Result<List<VendorSettleDateInfo>> getDate(@RequestParam(value = "marketKeys", required = false) @Parameter(description = "市场主键")List<String> marketKeys);
    
    @Operation(summary = "获取银行账户信息", tags = ApiTags.ZYYSC_VENDOR_ORDER)
    @PostMapping(value = "/vendorBill/bankInfo")
    public Result<BankInfo> getBankInfo(@RequestParam(value = "vendor") @Parameter(description = "商户pkey") Integer vendor);
    
}
