package cn.tofocus.lejia.app.v1.supplier;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.lejia.app.AppTags;
import cn.tofocus.lejia.bean.dto.app.supplier.AppSupplierInfo;
import cn.tofocus.lejia.bean.dto.app.supplier.AppSupplierOrderInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

public interface AppSupplierApi
{
    @Operation(summary = "供应商信息", tags = AppTags.mobileSupplier)
    @PostMapping("/get")
    Result<AppSupplierInfo> get();
    
    @Operation(summary = "获取订单信息（扫核销码）", tags = AppTags.mobileSupplier)
    @PostMapping("/order/verifyCode/scan")
    Result<AppSupplierOrderInfo> getOrderByScanVerifyCode(
        @RequestParam(value = "kcCode") @Parameter(description = "订单号") String kcCode,
        @RequestParam(value = "verifyCode") @Parameter(description = "核销码") String verifyCode);
    
    @Operation(summary = "核销订单", tags = AppTags.mobileSupplier)
    @PostMapping("/order/writeOff")
    Result<Boolean> writeOffOrder(@RequestParam(value = "kcCode") @Parameter(description = "订单号") String kcCode,
        @RequestParam(value = "verifyCode") @Parameter(description = "核销码") String verifyCode);
}
