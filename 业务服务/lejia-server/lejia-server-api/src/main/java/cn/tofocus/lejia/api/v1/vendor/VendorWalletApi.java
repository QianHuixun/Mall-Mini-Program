package cn.tofocus.lejia.api.v1.vendor;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.vendor.WalletDetailsOnPage;
import cn.tofocus.lejia.bean.dto.vendor.WalletOnInfo;
import cn.tofocus.lejia.bean.dto.vendor.WithdrawalOnInfo;
import cn.tofocus.lejia.bean.enums.vendor.WithdrawalStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

public interface VendorWalletApi
{
    @Operation(summary = "商户钱包-查询", tags = ApiTags.ZYYSC_VENDOR_WALLET)
    @PostMapping(value = "/query")
    public Result<WalletOnInfo> queryWallet(@RequestParam(value = "page", defaultValue = "0", required = false) int page,
        @RequestParam(value = "pagesize", defaultValue = "10", required = false) int pagesize,
        @RequestParam(value = "vendorName", required = false)@Parameter(description = "商户名称")String vendorName, 
        @RequestParam(value = "booth", required = false)@Parameter(description = "摊位号")String booth);
    
    @Operation(summary = "商户钱包-明细查询", tags = ApiTags.ZYYSC_VENDOR_WALLET)
    @PostMapping(value = "/line/query")
    public Result<PageResult<WalletDetailsOnPage>> queryWalletLine(
        @RequestParam(value = "page", defaultValue = "0", required = false) int page,
        @RequestParam(value = "pagesize", defaultValue = "10", required = false) int pagesize,
        @RequestParam(value = "pkey")@Parameter(description = "商户主键")Integer pkey);
    
    @Operation(summary = "提现打款-查询", tags = ApiTags.ZYYSC_VENDOR_WALLET)
    @PostMapping(value = "/withdrawal/query")
    public Result<WithdrawalOnInfo> queryWithdrawal(@RequestParam(value = "page", defaultValue = "0", required = false) int page,
        @RequestParam(value = "pagesize", defaultValue = "10", required = false) int pagesize,
        @RequestParam(value = "startDate", required = false) @Parameter(description = "申请时间-开始") String startDate,
        @RequestParam(value = "endDate", required = false) @Parameter(description = "申请时间-结束") String endDate,
        @RequestParam(value = "vendorName", required = false)@Parameter(description = "商户名称") String vendorName, 
        @RequestParam(value = "booth", required = false)@Parameter(description = "摊位号") String booth,
        @RequestParam(value = "status", required = false)@Parameter(description = "打款状态") WithdrawalStatus status);
    
    @Operation(summary = "提现打款-点击打款", tags = ApiTags.ZYYSC_VENDOR_WALLET)
    @PostMapping(value = "/withdrawal/confirm")
    public Result<Boolean> confirmWithdrawal(@RequestParam(value = "pkey") int pkey);
}
