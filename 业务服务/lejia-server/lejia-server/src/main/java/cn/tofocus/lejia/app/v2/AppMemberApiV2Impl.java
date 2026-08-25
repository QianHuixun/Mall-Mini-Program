package cn.tofocus.lejia.app.v2;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.lejia.bean.dto.v2.card.MemberCardOrderInfo;
import cn.tofocus.lejia.bean.dto.v2.card.MemberCardV2OnList;
import cn.tofocus.lejia.bean.dto.v2.gift.MemberGiftV2OnList;
import cn.tofocus.lejia.bean.dto.v2.order.OrderTotalV2Info;
import cn.tofocus.lejia.bean.enums.CardStatus;
import cn.tofocus.lejia.domain.v2.CardV2Manager;
import cn.tofocus.lejia.domain.v2.GiftV2Manager;

@RequestMapping("/v2/app/market/lm/member")
@RestController
public class AppMemberApiV2Impl implements AppMemberV2Api
{
    @Autowired
    private CardV2Manager manager;
    
    @Autowired
    private GiftV2Manager giftManager;
    
    @Override
    public Result<List<MemberCardV2OnList>> listMemberCard(CardStatus status)
    {
        return new Result<>(manager.listMemberCard(status));
    }

//    @Override
//    public Result<List<MemberCardV2OnList>> listCard(OrderTotalV2Info info)
//    {
//        return new Result<>(manager.listCard(info));
//    }
    
    @Override
    public Result<List<MemberGiftV2OnList>> listMemberGift(CardStatus status)
    {
        return new Result<>(giftManager.listMemberGift(status));
    }

    @Override
    public Result<MemberCardOrderInfo> listCardV2(OrderTotalV2Info info)
    {
        return new Result<>(manager.listCardV2(info));
    }
}
