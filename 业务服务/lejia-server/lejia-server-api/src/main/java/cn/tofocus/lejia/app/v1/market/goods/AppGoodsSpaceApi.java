package cn.tofocus.lejia.app.v1.market.goods;

import cn.tofocus.core.Result;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.lejia.app.AppTags;
import cn.tofocus.lejia.bean.dto.app.goods.AppSpaceDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "lejia-server", contextId = "lejia-server-app-goods-space", path = "/v1/app/market/goods/space",
        fallbackFactory = AppGoodsSpaceFallback.class, configuration = FeignConfig.class)
public interface AppGoodsSpaceApi {

    @Operation(summary = "获取商品规格列表", tags = AppTags.mobileGoodsSpace)
    @PostMapping(value = "/get")
    public Result<AppSpaceDTO> get(
            @RequestParam(value = "pkey") @Parameter(description = "商品pkey") Integer pkey);
    
    @Operation(summary = "获取商品规格列表", tags = AppTags.mobileGoodsSpace)
    @PostMapping(value = "/get/member")
    public Result<AppSpaceDTO> getMember(
        @RequestParam(value = "pkey") @Parameter(description = "商品pkey") Integer pkey);

    @Operation(summary = "获取商品规格数量", tags = AppTags.mobileGoodsSpace)
    @PostMapping(value = "/totalAmount")
    public Result<Integer> totalAmount(
            @RequestParam(value = "pkey") @Parameter(description = "商品pkey") Integer pkey);

}
