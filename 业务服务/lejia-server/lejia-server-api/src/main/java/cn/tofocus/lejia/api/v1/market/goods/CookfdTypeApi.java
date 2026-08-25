package cn.tofocus.lejia.api.v1.market.goods;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.market.MktCookfdTypeOnList;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@FeignClient(value = "lejia-server", contextId = "lejia-server-goods-cookfd-type", path = "/v1/market/goods/cookfd/type", 
fallbackFactory = CookfdFallback.class, configuration = FeignConfig.class)
public interface CookfdTypeApi 
{
	@Operation(summary = "新增菜谱分类", tags = ApiTags.custCookfdType)
	@PostMapping("/ins")
	public Result<Integer> insCookfdType(@RequestParam(value = "name") @Parameter(description = "名称") String name, 
			 @RequestParam(value = "sort", defaultValue = "0") @Parameter(description = "排序,默认为0") int sort);
	
	@Operation(summary = "获取菜谱分类列表", tags = ApiTags.custCookfdType)
    @PostMapping(value = "/query")
    public Result<PageResult<MktCookfdTypeOnList>> queryCookfdType(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", defaultValue = "10") @Parameter(description = "每页大小") int pagesize,
        @RequestParam(value = "name", required = false) @Parameter(description = "菜谱名称") String name,
		@RequestParam(name = "enabled", required = false) @Parameter(description = "启停") Boolean enabled);
	
	@Operation(summary = "修改菜谱分类", tags = ApiTags.custCookfdType)
    @PostMapping(value = "/upd")
    public Result<Boolean> updCookfdType(@RequestBody MktCookfdTypeOnList entity);
	
	@Operation(summary = "删除菜谱分类", tags = ApiTags.custCookfdType)
    @PostMapping(value = "/del")
    public Result<Boolean> delCookfdType(@RequestParam(name = "pkey") Integer pkey);
	
	@Operation(summary = "菜谱分类启用", tags = ApiTags.custCookfdType)
    @PostMapping(value = "/enable/start")
    public Result<Boolean> startCookfdType(@RequestParam(name = "pkey") Integer pkey);
	
	@Operation(summary = "菜谱分类停用", tags = ApiTags.custCookfdType)
    @PostMapping(value = "/enable/stop")
    public Result<Boolean> stopCookfdType(@RequestParam(name = "pkey") Integer pkey);
	
}
