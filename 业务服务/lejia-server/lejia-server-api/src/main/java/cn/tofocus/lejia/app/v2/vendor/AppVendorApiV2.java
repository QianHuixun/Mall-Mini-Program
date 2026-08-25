package cn.tofocus.lejia.app.v2.vendor;

import cn.tofocus.core.Result;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.lejia.app.AppTags;
import cn.tofocus.lejia.bean.dto.app.AppVendorMerchant;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 移动端-商户接口V2
 */
@FeignClient(value = "lejia-server", contextId = "lejia-server-app-vendor-v2", path = "/v2/app/vendor",
    fallbackFactory = AppVendorApiV2Fallback.class, configuration = FeignConfig.class)
public interface AppVendorApiV2
{
    
    @Operation(summary = "商户信息", tags = AppTags.mobileVendorV2)
    @PostMapping("/get")
    Result<AppVendorMerchant> getVendor();

    @Operation(summary = "更新商户信息", tags = AppTags.mobileVendorV2)
    @PostMapping("/upd")
    Result<Boolean> upd(@RequestBody AppVendorMerchant appVendor);

    @Operation(summary = "运营端是否开启统一配置", tags = AppTags.mobileVendorV2)
    @PostMapping(value = "/isUnified")
    Result<Boolean> isUnified();
}
