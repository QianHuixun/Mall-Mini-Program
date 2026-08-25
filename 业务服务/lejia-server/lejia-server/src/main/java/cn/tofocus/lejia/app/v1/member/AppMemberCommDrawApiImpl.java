package cn.tofocus.lejia.app.v1.member;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.lejia.domain.app.AppMemberCommManager;

@RequestMapping("/v1/app/market/lm/member/comm/draw")
@RestController
public class AppMemberCommDrawApiImpl implements AppMemberCommDrawApi
{
	
    @Autowired
    private AppMemberCommManager manager;
    
	@Override
	public Result<Boolean> ins(BigDecimal comms, String custCard, String custName, String accountBank, String remark) {
		return new Result<>(manager.ins(comms, custCard, custName, accountBank, remark));
	}

}
