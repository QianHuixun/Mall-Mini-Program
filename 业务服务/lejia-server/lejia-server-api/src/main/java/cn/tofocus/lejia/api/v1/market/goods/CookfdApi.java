package cn.tofocus.lejia.api.v1.market.goods;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.market.MktCookfdOnList;
import cn.tofocus.lejia.bean.dto.market.MktCookfdUpDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@FeignClient(value = "lejia-server", contextId = "lejia-server-goods-cookfd", path = "/v1/market/goods/cookfd", 
fallbackFactory = CookfdFallback.class, configuration = FeignConfig.class)
public interface CookfdApi 
{
	@Operation(summary = "新增菜谱", tags = ApiTags.custCookfd)
	@PostMapping("/ins")
	public Result<Integer> insCookfd(@RequestBody MktCookfdOnList entity);
	
	@Operation(summary = "获取菜谱", tags = ApiTags.custCookfd)
	@PostMapping("/get")
	public Result<MktCookfdOnList> getCookfd(@RequestParam(value = "pkey") @Parameter(description = "菜谱主键") Integer pkey);
	
	@Operation(summary = "获取菜谱列表", tags = ApiTags.custCookfd)
    @PostMapping(value = "/query")
    public Result<PageResult<MktCookfdOnList>> queryCookfd(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", defaultValue = "10") @Parameter(description = "每页大小") int pagesize,
        @RequestParam(value = "name", required = false) @Parameter(description = "菜谱名称") String name,
		@RequestParam(name = "recom", required = false) @Parameter(description = "今日推荐") Boolean recom,
		@RequestParam(name = "enabled", required = false) @Parameter(description = "启停") Boolean enabled,
		@RequestParam(name = "ctype", required = false) @Parameter(description = "分类") Integer ctype);
	
	@Operation(summary = "修改菜谱", tags = ApiTags.custCookfd)
    @PostMapping(value = "/upd")
    public Result<Boolean> updCookfd(@RequestBody MktCookfdUpDTO entity);
	
	@Operation(summary = "删除菜谱", tags = ApiTags.custCookfd)
    @PostMapping(value = "/del")
    public Result<Boolean> delCookfd(@RequestParam(name = "pkey") Integer pkey);
	
	@Operation(summary = "菜谱启用", tags = ApiTags.custCookfd)
    @PostMapping(value = "/enable/start")
    public Result<Boolean> startCookfd(@RequestParam(name = "pkey") Integer pkey);
	
	@Operation(summary = "菜谱停用", tags = ApiTags.custCookfd)
    @PostMapping(value = "/enable/stop")
    public Result<Boolean> stopCookfd(@RequestParam(name = "pkey") Integer pkey);
	
	@Operation(summary = "加入今日推荐", tags = ApiTags.custCookfd)
    @PostMapping(value = "/recom/start")
    public Result<Boolean> startRecomCookfd(@RequestParam(name = "pkey") Integer pkey);
	
	@Operation(summary = "退出今日推荐", tags = ApiTags.custCookfd)
    @PostMapping(value = "/recom/stop")
    public Result<Boolean> stopRecomCookfd(@RequestParam(name = "pkey") Integer pkey);
}
