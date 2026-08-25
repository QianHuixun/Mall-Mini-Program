package cn.tofocus.lejia.api.v1.zx;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.zx.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

public interface ZxUserApi
{
    @Operation(summary = "查询账户列表", tags = ApiTags.ZX_USER)
    @PostMapping("/query")
    Result<PageResult<ZxUserInfoOnPage>> query(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", defaultValue = "10000") @Parameter(description = "每页大小") int pagesize,
        @RequestParam(value = "name", required = false) @Parameter(description = "账户名称") String name);
    
    @Operation(summary = "获取账户信息", tags = ApiTags.ZX_USER)
    @PostMapping("/userInfo/get")
    Result<ZxUserInfoForUpdUser> getUserInfo(
        @RequestParam(value = "pkey", required = false) @Parameter(description = "主键") Integer pkey);
    
    @Operation(summary = "编辑账户信息", tags = ApiTags.ZX_USER)
    @PostMapping("/userInfo/upd")
    Result<Boolean> updUserInfo(@RequestBody @Valid ZxUserInfoForUpdUser forUpd);
    
    @Operation(summary = "获取银行卡信息", tags = ApiTags.ZX_USER)
    @PostMapping("/userBank/get")
    Result<ZxUserInfoForUpdBank> getUserBank(
        @RequestParam(value = "pkey", required = false) @Parameter(description = "主键") Integer pkey);
    
    @Operation(summary = "编辑银行卡信息", tags = ApiTags.ZX_USER)
    @PostMapping("/userBank/upd")
    Result<Boolean> updUserBank(@RequestBody @Valid ZxUserInfoForUpdBank forUpd);
    
    @Operation(summary = "启停市场自动提现", tags = ApiTags.ZX_USER)
    @PostMapping("/marketAuto/enable")
    Result<Boolean> enableMarketAuto(@RequestParam(value = "pkey") @Parameter(description = "主键") Integer pkey,
        @RequestParam(value = "enabled") @Parameter(description = "启停") Boolean enabled);
    
    @Operation(summary = "启停商户自动提现", tags = ApiTags.ZX_USER)
    @PostMapping("/vendorAuto/enable")
    Result<Boolean> enableVendorAuto(@RequestParam(value = "pkey") @Parameter(description = "主键") Integer pkey,
        @RequestParam(value = "enabled") @Parameter(description = "启停") Boolean enabled);
    
    @Operation(summary = "获取商户账户信息", tags = ApiTags.ZX_USER)
    @PostMapping("/vendor/userInfo/get")
    Result<ZxUserInfoForUpdVendorUser> getVendorUserInfo(
        @RequestParam(value = "pkey") @Parameter(description = "商户主键") Integer vendor);
    
    @Operation(summary = "编辑商户账户信息", tags = ApiTags.ZX_USER)
    @PostMapping("/vendor/userInfo/upd")
    Result<Boolean> updVendorUserInfo(@RequestBody @Valid ZxUserInfoForUpdVendorUser forUpd);
    
    @Operation(summary = "获取商户银行卡信息", tags = ApiTags.ZX_USER)
    @PostMapping("/vendor/userBank/get")
    Result<ZxUserInfoForUpdVendorBank> getVendorUserBank(
        @RequestParam(value = "pkey") @Parameter(description = "商户主键") Integer vendor);
    
    @Operation(summary = "编辑商户银行卡信息", tags = ApiTags.ZX_USER)
    @PostMapping("/vendor/userBank/upd")
    Result<Boolean> updVendorUserBank(@RequestBody @Valid ZxUserInfoForUpdVendorBank forUpd);
    
    @Operation(summary = "账户管理-划账下拉", tags = ApiTags.ZX_USER)
    @PostMapping(value = "/drop")
    public Result<List<ZxUserInfoDrop>> allocatioDrop();
    
    @Operation(summary = "账户管理-划账", tags = ApiTags.ZX_USER)
    @PostMapping(value = "/allocation")
    public Result<Boolean> allocation(
        @RequestParam(value = "pkey")Integer pkey,
        @RequestParam(value = "amt")BigDecimal amt,
        @RequestParam(value = "remark", required = false)String remark);
}
