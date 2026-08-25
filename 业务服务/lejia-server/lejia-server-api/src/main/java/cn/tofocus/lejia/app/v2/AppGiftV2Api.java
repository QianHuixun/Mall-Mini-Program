package cn.tofocus.lejia.app.v2;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.lejia.app.AppTags;
import cn.tofocus.lejia.bean.dto.app.AppGiftV2ForWriteOff;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

public interface AppGiftV2Api
{
    @Operation(summary = "获取待核销的礼品券信息", tags = AppTags.mobileGiftV2)
    @PostMapping("/writeOff/load")
    public Result<AppGiftV2ForWriteOff> load4WriteOff(
        @RequestParam(name = "cardNumber") @Parameter(description = "核销码") String cardNumber);
    
    @Operation(summary = "核销礼品券", tags = AppTags.mobileGiftV2)
    @PostMapping("/writeOff")
    public Result<Boolean> writeOff(
        @RequestParam(name = "cardNumber") @Parameter(description = "核销码") String cardNumber);
}
