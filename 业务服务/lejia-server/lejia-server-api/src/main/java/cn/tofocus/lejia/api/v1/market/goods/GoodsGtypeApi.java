package cn.tofocus.lejia.api.v1.market.goods;

import java.util.List;

import cn.tofocus.lejia.bean.dto.goods.TwoGtypeDropWithGoods;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.goods.GtypeDropInfo;
import cn.tofocus.lejia.bean.dto.goods.GtypeDropV2Info;
import cn.tofocus.lejia.bean.dto.market.DropIntegerDown;
import cn.tofocus.lejia.bean.dto.market.MktGtypeOnList;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@FeignClient(value = "lejia-server", contextId = "lejia-server-goods-gtype", path = "/v1/market/goods/gtype", fallbackFactory = GoodsGtypeFallback.class, configuration = FeignConfig.class)
public interface GoodsGtypeApi
{
    @Operation(summary = "新增商品分类", tags = ApiTags.custGoodsGtype)
    @PostMapping("/ins")
    public Result<MktGtypeOnList> insGtype(@RequestBody MktGtypeOnList entity);
    
    @Operation(summary = "获取商品分类", tags = ApiTags.custGoodsGtype)
    @PostMapping("/get")
    public Result<MktGtypeOnList> getGtype(@RequestParam(value = "pkey") @Parameter(description = "商品分类主键") Integer pkey);
    
    @Operation(summary = "获取商品分类列表", tags = ApiTags.custGoodsGtype)
    @PostMapping(value = "/query")
    public Result<PageResult<MktGtypeOnList>> queryGtype(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号", hidden = true) int page,
        @RequestParam(value = "pagesize", defaultValue = "100000") @Parameter(description = "每页大小", hidden = true) int pagesize,
        @RequestParam(value = "gtyprName", required = false) @Parameter(description = "商品分类名") String gtyprName,
        @RequestParam(name = "showPoint", required = false) @Parameter(description = "积分商城") Boolean showPoint,
        @RequestParam(name = "showMarket", required = false) @Parameter(description = "市场商城") Boolean showMarket);
    
    //	@Operation(summary = "修改商品分类", tags = ApiTags.custGoodsGtype)
    //    @PostMapping(value = "/upd")
    //    public Result<MktGtypeOnList> updGtype(@RequestBody MktGtypeOnList entity);
    @Operation(summary = "修改商品分类", tags = ApiTags.custGoodsGtype)
    @PostMapping(value = "/upd")
    public Result<MktGtypeOnList> updGtype(@RequestParam(name = "pkey") Integer pkey,
        @RequestParam(name = "name", required = false) String name,
        @RequestParam(name = "sort", required = false) Integer sort,
        @RequestParam(name = "marketSort", required = false) Integer marketSort,
        @RequestParam(name = "pointSort", required = false) Integer pointSort,
        @RequestParam(name = "photo", required = false) String photo,
        @RequestParam(name = "remark", required = false) String remark,
        @RequestParam(name = "showPoint", required = false) @Parameter(description = "积分商城") Boolean showPoint,
        @RequestParam(name = "showMarket", required = false) @Parameter(description = "市场商城") Boolean showMarket);
    
    @Operation(summary = "删除商品分类", tags = ApiTags.custGoodsGtype)
    @PostMapping(value = "/del")
    public Result<Boolean> delGtype(@RequestParam(name = "pkey") Integer pkey);
    
    @Operation(summary = "商品分类启用", tags = ApiTags.custGoodsGtype)
    @PostMapping(value = "/enable/start")
    public Result<Boolean> startGtype(@RequestParam(name = "pkey") Integer pkey);
    
    @Operation(summary = "商品分类停用", tags = ApiTags.custGoodsGtype)
    @PostMapping(value = "/enable/stop")
    public Result<Boolean> stopGtype(@RequestParam(name = "pkey") Integer pkey);
    
    @Operation(summary = "获取商品分类列表", tags = ApiTags.custGoodsGtype)
    @PostMapping(value = "/drop")
    public Result<List<GtypeDropInfo>> dropyGtype(@RequestParam(name = "key",required = false, defaultValue = "0")Integer key);

    @Operation(summary = "获取三级分类下拉列表", tags = ApiTags.custGoodsGtype)
    @PostMapping(value = "/three/drop")
    public Result<List<GtypeDropV2Info>> dropyGtypeV2(@RequestParam(name = "key",required = false, defaultValue = "0")Integer key);
    
    @Operation(summary = "获取两级分类下拉列表（带商品）", tags = ApiTags.custGoodsGtype)
    @PostMapping(value = "/two/withGoods/drop")
    public Result<List<TwoGtypeDropWithGoods>> dropTwoGtypeWithGoods(
        @RequestParam(name = "farmer") @Parameter(description = "市场主键") String farmer);

    // 一级分类下拉
    @Operation(summary = "获取品类下拉-用于新增有优惠券使用", tags = ApiTags.custGoodsGtype)
    @PostMapping(value = "/card/drop")
    public Result<List<DropIntegerDown>> dropCardGtype(
        @RequestParam(name = "farmer", required = false) @Parameter(description = "市场主键") String farmer);
    
}
