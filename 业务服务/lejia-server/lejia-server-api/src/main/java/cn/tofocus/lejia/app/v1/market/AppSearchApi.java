package cn.tofocus.lejia.app.v1.market;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.lejia.app.AppTags;
import cn.tofocus.lejia.bean.dto.app.market.AppSearchAppOnList;
import cn.tofocus.lejia.bean.enums.SearchKeywordModule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@FeignClient(value = "lejia-server", contextId = "lejia-server-app-search", path = "/v1/app/market/search", fallbackFactory = AppSearchFallback.class, configuration = FeignConfig.class)
public interface AppSearchApi
{
    @Operation(summary = "获取用户搜索历史和热门搜索", tags = AppTags.mobileSearch)
    @PostMapping("/query")
    public Result<AppSearchAppOnList> getSearch(
        @RequestParam(value = "stype") @Parameter(description = "搜索类型 商品/菜谱/积分商城") Integer stype);
    
    @Operation(summary = "获取用户搜索历史和热门搜索", tags = AppTags.mobileSearch)
    @PostMapping("/del")
    public Result<Boolean> delSearch();
    
    @Operation(summary = "列表搜索关键词", tags = AppTags.mobileSearch)
    @PostMapping("/keyword/list")
    public Result<List<String>> listKeywords(@RequestParam(value = "module") SearchKeywordModule module);
}
