package cn.tofocus.lejia.app.v2.goods;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.lejia.app.AppTags;
import cn.tofocus.lejia.bean.dto.PkeyNameDTO;
import cn.tofocus.lejia.bean.dto.app.goods.AppGtypeDTO;
import cn.tofocus.lejia.bean.enums.MType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

public interface AppGoodsGtypeApiV2
{
    @Operation(summary = "获取商品二级分类", tags = AppTags.mobileGoodsGtypeV2)
    @PostMapping(value = "/query")
    public Result<List<PkeyNameDTO>> queryGtype(
        @RequestParam(value = "gtype") @Parameter(required = true, description = "一级分类pkey") Integer gtype,
        @RequestParam(value = "hasRecommend", defaultValue = "false") @Parameter(description = "是否包含商品推荐") boolean hasRecommend);
 
    @Operation(summary = "获取商品一级分类", tags = AppTags.mobileGoodsGtypeV2)
    @PostMapping(value = "/list")
    public Result<List<AppGtypeDTO>> listGtype(
            @RequestParam(value = "showPoint", defaultValue = "false", required = false) @Parameter(description = "积分商城") Boolean showPoint,
            @RequestParam(value = "showMarket", defaultValue = "false", required = false) @Parameter(description = "市场商城") Boolean showMarket,
            @RequestParam(value = "flag", defaultValue = "false", required = false)@Parameter(description = "商户列表") Boolean flag,
            @RequestParam(value = "mtype", required = false)@Parameter(description = "商品类型") MType mtype);
    
    @Operation(summary = "获取分类页商户一分类列表", tags = AppTags.mobileGoodsGtypeV2)
    @PostMapping(value = "/vendor/one/list")
    public Result<List<PkeyNameDTO>> queryVendorOneGtype();
    
    @Operation(summary = "获取商户商品二级分类列表", tags = AppTags.mobileGoodsGtypeV2)
    @PostMapping(value = "/vendor/list")
    public Result<List<PkeyNameDTO>> queryVendorGtype(@RequestParam(value = "vendor") @Parameter(description = "商户主键") Integer vendor);

}
