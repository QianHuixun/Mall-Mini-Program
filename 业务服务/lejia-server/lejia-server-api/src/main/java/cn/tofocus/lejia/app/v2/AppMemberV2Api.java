package cn.tofocus.lejia.app.v2;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.lejia.app.AppTags;
import cn.tofocus.lejia.bean.dto.v2.card.MemberCardOrderInfo;
import cn.tofocus.lejia.bean.dto.v2.card.MemberCardV2OnList;
import cn.tofocus.lejia.bean.dto.v2.gift.MemberGiftV2OnList;
import cn.tofocus.lejia.bean.dto.v2.order.OrderTotalV2Info;
import cn.tofocus.lejia.bean.enums.CardStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

public interface AppMemberV2Api
{
    @Operation(summary = "获取卡券列表", tags = AppTags.mobileMemberV2)
    @PostMapping("/list/memberCard")
    public Result<List<MemberCardV2OnList>> listMemberCard(@RequestParam(value = "status") @Parameter(description = "状态") CardStatus status);
    
//    @Operation(summary = "下单页面获取可用优惠券列表", tags = AppTags.mobileMemberV2)
//    @PostMapping("/listCard")
//    public Result<List<MemberCardV2OnList>> listCard(@RequestBody OrderTotalV2Info info);
    
    @Operation(summary = "获取礼券列表", tags = AppTags.mobileMemberV2)
    @PostMapping("/list/memberGift")
    public Result<List<MemberGiftV2OnList>> listMemberGift(@RequestParam(value = "status") @Parameter(description = "状态") CardStatus status);

    @Operation(summary = "下单页面获取可用优惠券列表", tags = AppTags.mobileMemberV2)
    @PostMapping("/listCard")
    public Result<MemberCardOrderInfo> listCardV2(@RequestBody OrderTotalV2Info info);
    
}
