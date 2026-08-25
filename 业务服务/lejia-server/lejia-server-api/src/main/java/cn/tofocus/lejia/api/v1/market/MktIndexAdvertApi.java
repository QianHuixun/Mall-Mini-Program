package cn.tofocus.lejia.api.v1.market;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.exception.DefaultFallbackFactory;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.market.MktIndexAdvertOnList;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@FeignClient(value = "lejia-server", contextId = "lejia-server-index-advert", path = "/v1/market/index/img", 
fallbackFactory = DefaultFallbackFactory.class, configuration = FeignConfig.class)
public interface MktIndexAdvertApi {

	@Operation(summary = "新增弹窗广告", tags = ApiTags.custIndexAdvert)
    @PostMapping(value = "/ins")
	public Result<Integer> insIndexAdvert(@RequestBody MktIndexAdvertOnList entity);
	
	@Operation(summary = "获取弹窗广告列表", tags = ApiTags.custIndexAdvert)
    @PostMapping(value = "/query")
    public Result<PageResult<MktIndexAdvertOnList>> queryDrawWin( 
    		@RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
            @RequestParam(value = "pagesize", defaultValue = "10") @Parameter(description = "每页大小") int pagesize);
	
	@Operation(summary = "编辑弹窗广告", tags = ApiTags.custIndexAdvert)
    @PostMapping(value = "/upd")
	public Result<Integer> updIndexAdvert(@RequestBody MktIndexAdvertOnList entity);
	
	@Operation(summary = "删除弹窗广告", tags = ApiTags.custIndexAdvert)
    @PostMapping(value = "/del")
	public Result<Boolean> delIndexAdvert(@RequestParam(value = "pkey")Integer pkey);
	
	
}
