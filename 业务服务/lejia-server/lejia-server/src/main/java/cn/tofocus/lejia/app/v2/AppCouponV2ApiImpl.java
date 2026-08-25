package cn.tofocus.lejia.app.v2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.lejia.app.AppTags;
import cn.tofocus.lejia.bean.dto.v2.card.MemberCardV2OnList;
import cn.tofocus.lejia.domain.v2.CouponV2Manager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@RequestMapping("/v2/app/market/lm/coupon")
@RestController
public class AppCouponV2ApiImpl
{
    @Autowired
    private CouponV2Manager manager;
    
    @Operation(summary = "获取待核销的优惠券信息", tags = AppTags.mobileGiftV2)
    @PostMapping("/writeOff/load")
    public Result<MemberCardV2OnList> load4WriteOff(
        @RequestParam(name = "cardNumber") @Parameter(description = "核销码") String cardNumber)
    {
        return new Result<>(manager.load4WriteOff(cardNumber));
    }
    
    @Operation(summary = "核销优惠券", tags = AppTags.mobileGiftV2)
    @PostMapping("/writeOff")
    public Result<Boolean> writeOff(
        @RequestParam(name = "cardNumber") @Parameter(description = "核销码") String cardNumber)
    {
        return new Result<>(manager.writeOff(cardNumber));
    }
}
