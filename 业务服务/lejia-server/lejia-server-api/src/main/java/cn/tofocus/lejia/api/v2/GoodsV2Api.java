package cn.tofocus.lejia.api.v2;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.PkeyNameDTO;
import cn.tofocus.lejia.bean.dto.goods.GoodsCouponInfo;
import cn.tofocus.lejia.bean.dto.goods.GoodsCouponOnPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

public interface GoodsV2Api
{
    @Operation(summary = "新增优惠券商品", tags = ApiTags.LEJIA_V2_GOODS)
    @PostMapping("/ins/coupon")
    public Result<Integer> insGoodsCoupon(@RequestBody GoodsCouponInfo entity);
    
    @Operation(summary = "编辑优惠券商品", tags = ApiTags.LEJIA_V2_GOODS)
    @PostMapping("/upd/coupon")
    public Result<Integer> updGoodsCoupon(@RequestBody GoodsCouponInfo entity);
    
    @Operation(summary = "失效按钮", tags = ApiTags.LEJIA_V2_GOODS)
    @PostMapping("/invalid/coupon")
    public Result<Boolean> invalidGoodsCoupon(@RequestParam(value = "pkey") Integer pkey);
    
    @Operation(summary = "获取优惠券商品详情", tags = ApiTags.LEJIA_V2_GOODS)
    @PostMapping(value = "/get")
    public Result<GoodsCouponInfo> getGoods(@RequestParam(value = "pkey") Integer pkey);
    
    @Operation(summary = "获取优惠券商品列表", tags = ApiTags.LEJIA_V2_GOODS)
    @PostMapping(value = "/query")
    public Result<PageResult<GoodsCouponOnPage>> queryGoods(
        @RequestParam(value = "page", required = false, defaultValue = "0") @Parameter(description = "页号") Integer page,
        @RequestParam(value = "pagesize", required = false, defaultValue = "10") @Parameter(description = "每页大小") Integer pagesize,
        @RequestParam(value = "title", required = false) @Parameter(description = "标题") String title,
        @RequestParam(value = "goodsMain", required = false) @Parameter(description = "商品库id") Integer goodsMain,
        @RequestParam(value = "enabled", required = false) @Parameter(description = "是否上架") Boolean enabled);
    
    @Operation(summary = "商品批量启用", tags = ApiTags.LEJIA_V2_GOODS)
    @PostMapping(value = "/enable/start")
    public Result<Boolean> startGoods(@RequestParam(value = "pkeys") List<Integer> pkeys);
    
    @Operation(summary = "商品批量停用", tags = ApiTags.LEJIA_V2_GOODS)
    @PostMapping(value = "/enable/stop")
    public Result<Boolean> stopGoods(@RequestParam(value = "pkeys") List<Integer> pkeys);
    
    @Operation(summary = "获取市场商品下拉", tags = ApiTags.LEJIA_V2_GOODS)
    @PostMapping(value = "/market/drop")
    public Result<List<PkeyNameDTO>> dropMarketGoodsV2(@RequestParam(value = "farmer", required = false) String farmer,
        @RequestParam(value = "gtype", required = false) Integer gtype, 
        @RequestParam(value = "mtype", required = false) List<Integer> mtype);
    
}
