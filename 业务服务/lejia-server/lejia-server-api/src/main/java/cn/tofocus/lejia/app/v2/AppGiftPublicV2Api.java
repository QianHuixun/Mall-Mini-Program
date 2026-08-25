package cn.tofocus.lejia.app.v2;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.lejia.app.AppTags;
import cn.tofocus.lejia.bean.dto.app.AppGiftV2ForPublicWriteOff;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

public interface AppGiftPublicV2Api
{
    @Operation(summary = "【公开】获取待核销的礼品券信息", tags = AppTags.mobileGiftV2)
    @PostMapping("/writeOff/load")
    public Result<AppGiftV2ForPublicWriteOff> load4WriteOff(
        @RequestParam(name = "cardNumber") @Parameter(description = "礼品卡pkey") Integer cardNumber);
    
    @Operation(summary = "【公开】核销礼品券", tags = AppTags.mobileGiftV2)
    @PostMapping("/writeOff")
    public Result<Boolean> writeOff(
        @RequestParam(name = "cardNumber") @Parameter(description = "礼品卡pkey") Integer cardNumber,
        @RequestParam(name = "password") @Parameter(description = "核销口令") String password);
}
