package cn.tofocus.lejia.app.v3;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.app.AppTags;
import cn.tofocus.lejia.bean.dto.app.vendor.AppWalletBillOnInfo;
import cn.tofocus.lejia.bean.dto.app.vendor.AppWalletOnInfo;
import cn.tofocus.lejia.bean.dto.app.vendor.AppWalletOrderOnInfo;
import cn.tofocus.lejia.bean.dto.app.vendor.AppWalletVendorOrderInfo;
import cn.tofocus.lejia.bean.dto.app.vendor.VendorWalletBankInfo;
import cn.tofocus.lejia.bean.dto.vendor.WalletDetailsOnPage;
import cn.tofocus.lejia.bean.enums.SettlementType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

public interface AppVendorWalletV3Api
{
    @Operation(summary = "获取商户钱包信息(可提现和待结算)", tags = AppTags.mobileVendorV3)
    @PostMapping("/get")
    Result<AppWalletOnInfo> getAppWalletOnInfo();

    @Operation(summary = "获取待结算和已结算列表", tags = AppTags.mobileVendorV3)
    @PostMapping("/list/bill")
    Result<AppWalletBillOnInfo> listBill(@RequestParam(value = "day", defaultValue = "3", required = false) @Parameter(description = "前端不用传") Integer day,
        @RequestParam(value = "startDate", required = false)@Parameter(description = "格式:yyyy-MM")String startDate, 
        @RequestParam(value = "endDate", required = false)@Parameter(description = "格式:yyyy-MM")String endDate);
    
    @Operation(summary = "按日期获取结算订单列表", tags = AppTags.mobileVendorV3)
    @PostMapping("/list/order")
    Result<AppWalletOrderOnInfo> listOrder(
        @RequestParam(value = "time") @Parameter(description = "日期,格式 yyyy-MM-dd") String time,
        @RequestParam(value = "status", required = false) List<SettlementType> status);
    
    @Operation(summary = "按订单获取结算详情", tags = AppTags.mobileVendorV3)
    @PostMapping("/get/order")
    Result<AppWalletVendorOrderInfo> getVendorOrderWallet(@RequestParam(value = "pkey") @Parameter(description = "订单主键") Integer pkey);
    
    @Operation(summary = "获取钱包明细", tags = AppTags.mobileVendorV3)
    @PostMapping(value = "/line/query")
    Result<PageResult<WalletDetailsOnPage>> queryAppWalletLine(
        @RequestParam(value = "page", defaultValue = "0", required = false) int page,
        @RequestParam(value = "pagesize", defaultValue = "10", required = false) int pagesize);
    
    @Operation(summary = "获取提现账户信息", tags = AppTags.mobileVendorV3)
    @PostMapping(value = "/get/bankInfo")
    Result<VendorWalletBankInfo> getBankOnInfo();
    
    @Operation(summary = "编辑提现账户信息", tags = AppTags.mobileVendorV3)
    @PostMapping(value = "/upd/bankInfo")
    Result<Boolean> updBankOnInfo(@RequestBody VendorWalletBankInfo info);
    
    @Operation(summary = "申请提现", tags = AppTags.mobileVendorV3)
    @PostMapping(value = "/apply/withdrawal")
    Result<Boolean> applyWithdrawal(@RequestParam(value = "amount") BigDecimal amount);
    
    // 银行下拉选
    
}
