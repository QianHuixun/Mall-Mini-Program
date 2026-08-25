package cn.tofocus.lejia.app.v1.market.goods;

import cn.tofocus.core.Result;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.app.AppTags;
import cn.tofocus.lejia.bean.dto.app.market.MktAppCookfdDetailsDTO;
import cn.tofocus.lejia.bean.dto.app.market.MktAppCookfdTypeOnList;
import cn.tofocus.lejia.bean.dto.app.market.MktCookfdAppOnList;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "lejia-server", contextId = "lejia-server-app-goods-cookfd", path = "/v1/app/market/goods/cookfd",
        fallbackFactory = AppCookfdFallback.class, configuration = FeignConfig.class)
public interface AppCookfdApi {


    @Operation(summary = "获取菜谱", tags = AppTags.mobileCookfd)
    @PostMapping("/get")
    public Result<MktAppCookfdDetailsDTO> getCookfd(@RequestParam(value = "pkey") @Parameter(description = "菜谱主键") Integer pkey);

    @Operation(summary = "获取菜谱列表", tags = AppTags.mobileCookfd)
    @PostMapping(value = "/query")
    public Result<PageResult<MktCookfdAppOnList>> queryCookfd(
            @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") Integer page,
            @RequestParam(value = "pagesize", defaultValue = "10") @Parameter(description = "每页大小") Integer pagesize,
            @RequestParam(value = "name", required = false) @Parameter(description = "菜谱名称") String name,
            @RequestParam(value = "ctype", required = false) @Parameter(description = "分类") Integer ctype,
            @RequestParam(name = "recom", required = false) @Parameter(description = "今日推荐") Boolean recom,
            @RequestParam(name = "hot", required = false) @Parameter(description = "热门排序") Boolean hot);

    @Operation(summary = "根据商品获取相关菜谱", tags = AppTags.mobileCookfd)
    @PostMapping(value = "/query/related")
    public Result<List<Map<String,Object>>> queryRelatedCookfd(@RequestParam(value = "goods") @Parameter(description = "商品pkey") Integer goodsPkey);
    
    @Operation(summary = "根据菜谱分类", tags = AppTags.mobileCookfd)
    @PostMapping(value = "/query/ctype")
    public Result<List<MktAppCookfdTypeOnList>> queryCookfdType();
}
