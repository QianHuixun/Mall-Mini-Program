package cn.tofocus.lejia.api.v1.market;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.market.MktRefundOnList;
import cn.tofocus.lejia.bean.enums.RefundStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@FeignClient(value = "lejia-server", contextId = "lejia-server-refund", path = "/v1/market/refund", 
fallbackFactory = MktRefundApiFallback.class, configuration = FeignConfig.class)
public interface MktRefundApi 
{
	
	@Operation(summary = "获取退款列表", tags = ApiTags.custRefund)
    @PostMapping(value = "/query")
    public Result<PageResult<MktRefundOnList>> queryRefund(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号", hidden = true) int page,
        @RequestParam(value = "pagesize", defaultValue = "100") @Parameter(description = "每页大小", hidden = true) int pagesize,
        @RequestParam(value = "code", required = false) @Parameter(description = "订单编号") String code,
        @RequestParam(value = "status", required = false) RefundStatus status);
	
	
	@Operation(summary = "修改退款状态", tags = ApiTags.custRefund)
    @PostMapping(value = "/upd")
    public Result<MktRefundOnList> updRefund(@RequestParam int pkey,@RequestParam RefundStatus status);
	
	
}
