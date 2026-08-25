package cn.tofocus.lejia.app.v1.market.goods;

import cn.tofocus.core.Result;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.lejia.app.AppTags;
import cn.tofocus.lejia.bean.dto.app.goods.AppGtypeDTO;
import cn.tofocus.lejia.bean.dto.app.goods.AppMallGtypeTwoLevelsDTO;
import cn.tofocus.lejia.bean.enums.MType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "lejia-server", contextId = "lejia-server-app-goods-gtype", path = "/v1/app/market/goods/gtype",
        fallbackFactory = AppGoodsGtypeFallback.class, configuration = FeignConfig.class)
public interface AppGoodsGtypeApi {

    @Operation(summary = "获取商品分类列表", tags = AppTags.mobileGoodsGtype)
    @PostMapping(value = "/query")
    public Result<List<AppGtypeDTO>> queryGtype(
            @RequestParam(value = "showPoint", defaultValue = "false", required = false) @Parameter(description = "积分商城") Boolean showPoint,
            @RequestParam(value = "showMarket", defaultValue = "false", required = false) @Parameter(description = "市场商城") Boolean showMarket,
            @RequestParam(value = "flag", defaultValue = "false", required = false)@Parameter(description = "商户列表") Boolean flag,
            @RequestParam(value = "mtype", required = false)@Parameter(description = "商品类型") MType mtype);

    @Operation(summary = "获取商城一二级分类嵌套列表", tags = AppTags.mobileGoodsGtype)
    @PostMapping(value = "/mall/twoLevels/list")
    public Result<List<AppMallGtypeTwoLevelsDTO>> listMallTwoLevelsGtype(
            @RequestParam(value = "mtype") @Parameter(required = true, description = "商品类型：INTEGRAL_GOODS/INTEGRAL_PRESALE_GOODS/INTEGRAL_BNYP_GOODS/INTEGRAL_MSD_GOODS") MType mtype);

}
