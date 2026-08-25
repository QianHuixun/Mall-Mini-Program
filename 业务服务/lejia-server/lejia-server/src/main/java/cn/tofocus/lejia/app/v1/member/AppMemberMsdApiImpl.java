package cn.tofocus.lejia.app.v1.member;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.lejia.bean.entity.member.MktMember;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.domain.app.AppMemberMsdManager;

@RequestMapping("/v1/app/market/lm/member/msd")
@RestController
public class AppMemberMsdApiImpl implements AppMemberMsdApi
{
    @Autowired
    private AppMemberMsdManager appMemberMsdManager;
    
    @Override
    public Result<BigDecimal> getBalance()
    {
        BigDecimal balance = appMemberMsdManager.getBalance();
        return new Result<>(balance);
    }
    
    @Override
    public Result<Boolean> rechargeCard(String cardNumber, String cardPassword)
    {
        Integer appid = MobileSession.appid();
        MktMember member = MobileSession.member();
        Boolean res =
            appMemberMsdManager.rechargeCard(cardNumber, cardPassword, member.getPkey(), member.getMobile(), appid);
        return new Result<>(res);
    }
}
