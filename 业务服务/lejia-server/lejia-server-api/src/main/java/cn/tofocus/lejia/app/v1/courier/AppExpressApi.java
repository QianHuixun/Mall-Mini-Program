package cn.tofocus.lejia.app.v1.courier;

import javax.validation.Valid;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.app.AppTags;
import cn.tofocus.lejia.bean.dto.app.AppExpressArrivedParam;
import cn.tofocus.lejia.bean.dto.app.AppExpressDTO;
import cn.tofocus.lejia.bean.dto.app.AppExpressDetailsDTO;
import cn.tofocus.lejia.bean.dto.app.AppExpressFulfillDTO;
import cn.tofocus.lejia.bean.enums.ExpressStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@FeignClient(value = "lejia-server", contextId = "lejia-server-app-express", path = "/v1/app/courier/express", 
fallbackFactory = AppExpressApiFallback.class, configuration = FeignConfig.class)
public interface AppExpressApi 
{
	
	@Operation(summary = "获取跑腿单列表", tags = AppTags.mobileExpress)
    @PostMapping(value = "/query")
    public Result<PageResult<AppExpressDTO>> queryExpress(
        @RequestParam(value = "page", defaultValue = "0", required = false) @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", defaultValue = "100", required = false) @Parameter(description = "每页大小") int pagesize,
        @RequestParam(value = "status") @Parameter(description = "状态") ExpressStatus status);
	
	@Operation(summary = "获取已完成订单列表", tags = AppTags.mobileExpress)
    @PostMapping(value = "/query/fulfill")
    public Result<PageResult<AppExpressFulfillDTO>> queryFulfillExpress(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", defaultValue = "100") @Parameter(description = "每页大小") int pagesize);
	
	
	@Operation(summary = "获取订单详情", tags = AppTags.mobileExpress)
    @PostMapping(value = "/get")
    public Result<AppExpressDetailsDTO> getExpress(@RequestParam(value = "pkey") Integer pkey);
	
	@Operation(summary = "揽货", tags = AppTags.mobileExpress)
    @PostMapping(value = "/upd/goods")
	public Result<Boolean> goodsExpress(@RequestParam(value = "pkey") Integer pkey);
    
    @Operation(summary = "已到货", tags = AppTags.mobileExpress)
    @PostMapping(value = "/upd/arrived")
    public Result<Boolean> arrivedExpress(@RequestBody @Valid AppExpressArrivedParam param);
	
	
}
