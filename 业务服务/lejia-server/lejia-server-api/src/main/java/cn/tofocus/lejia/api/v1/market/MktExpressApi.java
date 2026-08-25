package cn.tofocus.lejia.api.v1.market;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.market.MktExpressOnList;
import cn.tofocus.lejia.bean.enums.ExpressStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@FeignClient(value = "lejia-server", contextId = "lejia-server-express", path = "/v1/market/express", 
fallbackFactory = MktExpressApiFallback.class, configuration = FeignConfig.class)
public interface MktExpressApi 
{
	
	
	@Operation(summary = "获取跑腿单列表", tags = ApiTags.custExpress)
    @PostMapping(value = "/query")
    public Result<PageResult<MktExpressOnList>> queryExpress(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", defaultValue = "100") @Parameter(description = "每页大小") int pagesize,
        @RequestParam(value = "status", required = false) @Parameter(description = "状态") ExpressStatus status,
        @RequestParam(value = "startTime", required = false) String startTime,
        @RequestParam(value = "endTime", required = false) String endTime,
        @RequestParam(value = "courierName", required = false) @Parameter(description = "标题") String courierName,
        @RequestParam(value = "orderId", required = false) String orderId);
	
}
