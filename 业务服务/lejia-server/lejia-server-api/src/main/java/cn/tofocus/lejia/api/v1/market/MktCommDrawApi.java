package cn.tofocus.lejia.api.v1.market;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.exception.DefaultFallbackFactory;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.market.MktCommDrawOnList;
import cn.tofocus.lejia.bean.enums.CommDrawStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@FeignClient(value = "lejia-server", contextId = "lejia-server-comm-draw", path = "/v1/market/comm/draw", 
fallbackFactory = DefaultFallbackFactory.class, configuration = FeignConfig.class)
public interface MktCommDrawApi 
{

	@Operation(summary = "获取提现申请列表", tags = ApiTags.custCommsDraw)
	@PostMapping("/query")
	public Result<PageResult<MktCommDrawOnList>> queryCommDraw( 
			@RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
			@RequestParam(value = "pagesize", defaultValue = "10") @Parameter(description = "每页大小") int pagesize,
			@RequestParam(value = "status", required = false) @Parameter(description = "状态 初始/已发") CommDrawStatus status, 
			@RequestParam(value = "orderNumber", required = false) @Parameter(description = "订单号") String orderNumber);
	
	@Operation(summary = "同意提现", tags = ApiTags.custCommsDraw)
	@PostMapping("/agree")
	public Result<Boolean> agreeCommDraw(
			@RequestParam(value = "pkey", required = true) @Parameter(description = "提现申请的pkey") Integer pkey, 
			@RequestParam(value = "remark", required = false) @Parameter(description = "备注") String remark);
	
	@Operation(summary = "拒绝提现", tags = ApiTags.custCommsDraw)
	@PostMapping("/refuse")
	public Result<Boolean> refuseCommDraw(
			@RequestParam(value = "pkey", required = true) @Parameter(description = "提现申请的pkey") Integer pkey, 
			@RequestParam(value = "remark", required = false) @Parameter(description = "备注") String remark);
	
	@Operation(summary = "填写备注", tags = ApiTags.custCommsDraw)
	@PostMapping("/upd")
	public Result<Boolean> updCommDraw(
			@RequestParam(value = "pkey", required = true) @Parameter(description = "pkey") Integer pkey, 
			@RequestParam(value = "remark", required = true) @Parameter(description = "备注") String remark);
	
	@Operation(summary = "同意提现", tags = ApiTags.custCommsDraw)
	@PostMapping("/paid")
	public Result<Boolean> paidDraw(
			@RequestParam(value = "pkey", required = true) @Parameter(description = "提现申请的pkey") Integer pkey);
}
