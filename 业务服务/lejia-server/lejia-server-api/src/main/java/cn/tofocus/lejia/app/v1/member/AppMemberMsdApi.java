package cn.tofocus.lejia.app.v1.member;

import java.math.BigDecimal;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.lejia.app.AppTags;
import io.swagger.v3.oas.annotations.Operation;

public interface AppMemberMsdApi
{
    @Operation(summary = "获取热力豆余额", tags = AppTags.mobileMsd)
    @PostMapping(value = "/balance")
    Result<BigDecimal> getBalance();
    
    @Operation(summary = "热力豆卡密充值", tags = AppTags.mobileMsd)
    @PostMapping(value = "/recharge/card")
    Result<Boolean> rechargeCard(@RequestParam(value = "cardNumber") String cardNumber,
        @RequestParam(value = "cardPassword") String cardPassword);
}
