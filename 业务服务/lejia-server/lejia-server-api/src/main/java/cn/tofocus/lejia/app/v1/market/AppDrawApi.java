package cn.tofocus.lejia.app.v1.market;

import cn.tofocus.core.Result;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.lejia.app.AppTags;
import cn.tofocus.lejia.bean.dto.app.market.AppDrawMsgDTO;
import cn.tofocus.lejia.bean.dto.app.market.AppDrawPrizeDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "lejia-server", contextId = "lejia-server-app-draw", path = "/v1/app/market/lm/draw",
        fallbackFactory = AppDrawFallback.class, configuration = FeignConfig.class)
public interface AppDrawApi {

    @Operation(summary = "获取积分抽奖信息", tags = AppTags.mobileDraw)
    @PostMapping("/get")
    public Result<AppDrawMsgDTO> getDrawMessage();

    @Operation(summary = "抽奖", tags = AppTags.mobileDraw)
    @PostMapping("/draw")
    public Result<AppDrawPrizeDTO> draw();
    
    @Operation(summary = "填写中奖地址", tags = AppTags.mobileDraw)
    @PostMapping("/insAddr")
    public Result<Boolean> insDrawAddr(
    		@RequestParam(value = "pkey") @Parameter(description = "抽奖返回的pkey")Integer pkey,
    		@RequestParam(value = "addr") @Parameter(description = "地址")String addr);
}
