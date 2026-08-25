package cn.tofocus.lejia.app.v1.member;

import cn.tofocus.core.Result;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.app.AppTags;
import cn.tofocus.lejia.bean.dto.app.market.AppMemberCommLineOnList;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(value = "lejia-server", contextId = "lejia-server-app-member-comm", path = "/v1/app/market/lm/member/comm",
        fallbackFactory = AppMemberGwcFallback.class, configuration = FeignConfig.class)
public interface AppMemberCommApi {

    @Operation(summary = "获取余额", tags = AppTags.mobileComm)
    @PostMapping(value = "/get")
    public Result<BigDecimal> get();

    @Operation(summary = "获取余额流水", tags = AppTags.mobileComm)
    @PostMapping(value = "/line")
    public Result<PageResult<AppMemberCommLineOnList>> line(
            @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号", hidden = true) int page,
            @RequestParam(value = "pagesize", defaultValue = "10") @Parameter(description = "每页大小", hidden = true) int pagesize,
            @RequestParam(value = "direct", required = false) @Parameter(description = "借贷标志 借(-)/贷(+)") Boolean direct);

    @Operation(summary = "卡密充值", tags = AppTags.mobileComm)
    @PostMapping(value = "/recharge/card")
    public Result<Boolean> rechargeCard(@RequestParam(value = "cardNumber")String cardNumber, 
        @RequestParam(value = "cardPassword")String cardPassword);
}
