package cn.tofocus.lejia.app.v1.market;

import cn.tofocus.core.Result;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.app.AppTags;
import cn.tofocus.lejia.bean.dto.PkeyNameDTO;
import cn.tofocus.lejia.bean.dto.app.AppDemeanourPageDTO;
import cn.tofocus.lejia.bean.dto.app.AppVendor;
import cn.tofocus.lejia.bean.dto.app.goods.AppGoodsV4OnList;
import cn.tofocus.lejia.bean.dto.market.MktVendorQueryParamDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 商户风采接口
 */
@FeignClient(value = "lejia-server", contextId = "lejia-server-app-demeanour", path = "/v1/app/market/demeanour",
        fallbackFactory = AppDemeanourFallback.class, configuration = FeignConfig.class)
public interface AppDemeanourApi
{
    @Operation(summary = "商户风采：一级分类列表", tags = AppTags.mobileDemeanour)
    @PostMapping("/gtypePkeyNameList")
    Result<List<PkeyNameDTO>> gtypePkeyNameList();

    @Operation(summary = "商户风采分页数据", tags = AppTags.mobileDemeanour)
    @PostMapping("/pageList")
    Result<PageResult<AppDemeanourPageDTO>> pageList(@ModelAttribute MktVendorQueryParamDTO paramDTO);

    @Operation(summary = "获取商户详情", tags = AppTags.mobileDemeanour)
    @PostMapping("/get")
    Result<AppVendor> getVendor(@RequestParam(value = "pkey") @Parameter(description = "商户主键", required = true) Integer pkey);
    
    @Operation(summary = "获取商户商品列表", tags = AppTags.mobileDemeanour)
    @PostMapping(value = "/query")
    public Result<PageResult<AppGoodsV4OnList>> queryAppVendorGoods(
        @RequestParam(value = "page", defaultValue = "0", required = false) @Parameter(description = "页号") Integer page,
        @RequestParam(value = "pagesize", defaultValue = "10", required = false) @Parameter(description = "每页大小") Integer pagesize,
        @RequestParam(value = "vendor", required = true) @Parameter(description = "商户主键") Integer vendor,
        @RequestParam(value = "name", required = false) @Parameter(description = "商品名称") String name,
        @RequestParam(value = "goodsMain", required = false) @Parameter(description = "二级分类pkey") Integer goodsMain,
        @RequestParam(value = "priceSort", required = false, defaultValue = "false")@Parameter(description = "价格排序")Boolean priceSort,
        @RequestParam(value = "xsNumSort", required = false)@Parameter(description = "销量排序")Boolean xsNumSort);
}
