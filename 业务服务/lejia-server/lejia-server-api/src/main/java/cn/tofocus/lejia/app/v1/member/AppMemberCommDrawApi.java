package cn.tofocus.lejia.app.v1.member;

import java.math.BigDecimal;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.exception.DefaultFallbackFactory;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.lejia.app.AppTags;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@FeignClient(value = "lejia-server", contextId = "lejia-server-app-member-comm-draw", path = "/v1/app/market/lm/member/comm/draw", 
fallbackFactory = DefaultFallbackFactory.class, configuration = FeignConfig.class)
public interface AppMemberCommDrawApi 
{
	
	@Operation(summary = "提现", tags = AppTags.mobileComm)
	@PostMapping(value = "/ins")
	public Result<Boolean> ins(
			 @RequestParam(value = "comms", required = true) @Parameter(description = "提现金额") BigDecimal comms, 
			 @RequestParam(value = "custCard", required = true) @Parameter(description = "提现银行卡") String custCard, 
			 @RequestParam(value = "custName", required = true) @Parameter(description = "提现银行卡用户名") String custName, 
			 @RequestParam(value = "accountBank", required = true) @Parameter(description = "提现银行卡 开户行") String accountBank, 
			 @RequestParam(value = "remark", required = false) @Parameter(description = "备注") String remark);

}
