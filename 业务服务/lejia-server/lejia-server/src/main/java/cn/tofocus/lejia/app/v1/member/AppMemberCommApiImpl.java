package cn.tofocus.lejia.app.v1.member;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.app.market.AppMemberCommLineOnList;
import cn.tofocus.lejia.bean.entity.member.MktMember;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.domain.RechargeCardManager;
import cn.tofocus.lejia.domain.app.AppMemberCommManager;


@RequestMapping("/v1/app/market/lm/member/comm")
@RestController
public class AppMemberCommApiImpl implements AppMemberCommApi 
{
    @Autowired
    private AppMemberCommManager appMemberCommManager;
    
    @Autowired
    private RechargeCardManager manager;

    @Override
    public Result<BigDecimal> get() {
        return new Result<>(appMemberCommManager.loadComm());
    }

    @Override
    public Result<PageResult<AppMemberCommLineOnList>> line(int page, int pagesize, Boolean direct) {
        return new Result<>(appMemberCommManager.queryLine(page, pagesize, direct));
    }

    @Override
    public Result<Boolean> rechargeCard(String cardNumber, String cardPassword)
    {
        Integer appid = MobileSession.appid();
        MktMember member = MobileSession.member();
        Boolean res = manager.rechargeCard(cardNumber, cardPassword, member.getPkey(), member.getMobile(), appid);
        return new Result<>(res);
    }
}
