package cn.tofocus.lejia.app.v1.market.goods;

import cn.tofocus.lejia.app.AppTags;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.app.market.AppCollectionDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@FeignClient(value = "lejia-server", contextId = "lejia-server-app-goods-collection", path = "/v1/app/market/lm/goods/collection",
        fallbackFactory = AppCookfdFallback.class, configuration = FeignConfig.class)
public interface AppCollectionApi {

    @Operation(summary = "新增收藏", tags = AppTags.mobileGoodsCollection)
    @PostMapping(value = "/ins")
    public Result<Integer> insCollection(
            @RequestParam(value = "objKey") @Parameter(description = "对象主键 ") Integer objKey,
            @RequestParam(value = "ctype") @Parameter(description = "类型  0: 菜谱/ 1:商品 2:商户 ") Integer ctype);

    @Operation(summary = "获取收藏列表", tags = AppTags.mobileGoodsCollection)
    @PostMapping(value = "/query")
    public Result<PageResult<AppCollectionDTO>> queryCollection(
            @RequestParam(value = "page", defaultValue = "0", required = false) @Parameter(description = "页号") int page,
            @RequestParam(value = "pagesize", defaultValue = "20", required = false) @Parameter(description = "每页大小") int pagesize,
            @RequestParam(value = "ctype", defaultValue = "1", required = false) @Parameter(description = "类型  0: 菜谱/ 1:商品2:商户  ") Integer ctype);

    @Operation(summary = "获取收藏分类数量", tags = AppTags.mobileGoodsCollection)
    @PostMapping(value = "/get/ctype/num")
    public Result<Map<String,Integer>> getCtypeNum();

    
    @Operation(summary = "删除收藏", tags = AppTags.mobileGoodsCollection)
    @PostMapping(value = "/del")
    public Result<Boolean> delCollection(
            @RequestParam(value = "pkey") @Parameter(description = "收藏主键") int pkey);
}
