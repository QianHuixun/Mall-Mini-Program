package cn.tofocus.lejia.app.v1.market.goods;

import cn.tofocus.core.Result;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.app.AppTags;
import cn.tofocus.lejia.bean.dto.app.market.*;
import cn.tofocus.lejia.bean.dto.goods.GoodsProcessOnInfo;
import cn.tofocus.lejia.bean.enums.GoodsRecommendZone;
import cn.tofocus.lejia.bean.enums.MType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "lejia-server", contextId = "lejia-server-app-goods", path = "/v1/app/market/goods",
        fallbackFactory = AppGoodsFallback.class, configuration = FeignConfig.class)
public interface AppGoodsApi {

    @Operation(summary = "获取商品列表", tags = AppTags.mobileGoods)
    @PostMapping(value = "/query")
    public Result<PageResult<AppGoodsAppOnList>> queryAppGoods(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") Integer page,
        @RequestParam(value = "pagesize", defaultValue = "10") @Parameter(description = "每页大小") Integer pagesize,
        @RequestParam(value = "gtype", required = false) @Parameter(description = "分类id") Integer gtype,
        @RequestParam(value = "goodsMain", required = false) @Parameter(description = "二级分类pkey") Integer goodsMain,
        @RequestParam(value = "mType", required = false) @Parameter(description = "商品属性") MType mType,
        @RequestParam(value = "title", required = false) @Parameter(description = "标题") String title,
        @RequestParam(value = "hotSort", defaultValue = "0") @Parameter(description = "销量排序 0-不排序/1-降序/2-升序") Integer hotSort,
        @RequestParam(value = "priceSort", defaultValue = "0") @Parameter(description = "价格排序 0-不排序/1-降序/2-升序") Integer priceSort,
        @RequestParam(value = "date", required = false) @Parameter(description = "售卖日期") String date,
        @RequestParam(value = "isOnPresale", defaultValue = "false") @Parameter(description = "是否正在预售（针对预售商品列表）") Boolean isOnPresale,
        @RequestParam(value = "guessLike", required = false) @Parameter(description = "猜我喜欢") Boolean guessLike,
        @RequestParam(value = "vendor", required = false) @Parameter(description = "商户主键") Integer vendor,
        @RequestParam(value = "topGoods", required = false) @Parameter(description = "置顶商品主键") Integer topGoods);
    
    
    @Operation(summary = "获取猜你喜欢商品列表", tags = AppTags.mobileGoods)
    @PostMapping(value = "/query/guessLike")
    public Result<PageResult<AppGoodsAppOnList>> queryAppGuessLikeGoods(
            @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") Integer page,
            @RequestParam(value = "pagesize", defaultValue = "10") @Parameter(description = "每页大小") Integer pagesize);
    
    @Operation(summary = "获取会员商品列表", tags = AppTags.mobileGoods)
    @PostMapping(value = "/query/member")
    public Result<PageResult<AppGoodsAppOnList>> queryAppMemberGoods(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") Integer page,
        @RequestParam(value = "pagesize", defaultValue = "10") @Parameter(description = "每页大小") Integer pagesize);

    @Operation(summary = "获取商品详情", tags = AppTags.mobileGoods)
    @PostMapping(value = "/get")
    public Result<AppGoodsDetailsDTO> getGoods(
            @RequestParam(value = "pkey") @Parameter(description = "商品pkey") Integer pkey);
    
    @Operation(summary = "获取会员商品详情", tags = AppTags.mobileGoods)
    @PostMapping(value = "/get/member")
    public Result<AppGoodsDetailsDTO> getMemberGoods(
        @RequestParam(value = "pkey") @Parameter(description = "商品pkey") Integer pkey);
    
    @Operation(summary = "分页获取商品评价", tags = AppTags.mobileGoods)
    @PostMapping(value = "/comment/query")
    public Result<PageResult<AppGoodsCommentOnList>> queryGoodsComments(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") Integer page,
        @RequestParam(value = "pagesize", defaultValue = "10") @Parameter(description = "每页大小") Integer pagesize,
        @RequestParam(value = "pkey") @Parameter(description = "商品pkey") Integer pkey);

    @Operation(summary = "获取特价商品日期范围", tags = AppTags.mobileGoods)
    @PostMapping(value = "/getSpecialGoodsSellDate")
    public Result<List<MktAppSpecialGoodsSellDateDTO>> getSpecialGoodsSellDate();

    @Operation(summary = "获取商品加工选项", tags = AppTags.mobileGoods)
    @PostMapping(value = "/list/process")
    public Result<List<GoodsProcessOnInfo>> listGoodsProcessOnInfo(@RequestParam(value = "pkey") @Parameter(description = "商品pkey")Integer pkey);
    
    @Operation(summary = "查询为你推荐", tags = AppTags.mobileGoods)
    @PostMapping(value = "/recommend/query")
    public Result<PageResult<AppRecommendGoodsOnPage>> queryAppGoodsRecommend(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") Integer page,
        @RequestParam(value = "pagesize", defaultValue = "10") @Parameter(description = "每页大小") Integer pagesize,
        @RequestParam(value = "zone") @Parameter(description = "商品推荐区域") GoodsRecommendZone zone,
        @RequestParam(value = "sourceGoods", required = false) @Parameter(description = "来源商品") Integer sourceGoods);

    @Operation(summary = "分页查询商城商品", tags = AppTags.mobileGoods)
    @PostMapping(value = "/mall/query")
    public Result<PageResult<AppMallGoodsOnPage>> queryMallGoods(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") Integer page,
        @RequestParam(value = "pagesize", defaultValue = "10") @Parameter(description = "每页大小") Integer pagesize,
        @RequestParam(value = "mtype") @Parameter(description = "商品类型：INTEGRAL_GOODS/INTEGRAL_PRESALE_GOODS/INTEGRAL_BNYP_GOODS/INTEGRAL_MSD_GOODS") MType mtype,
        @RequestParam(value = "gtype") @Parameter(description = "商城一级分类pkey") Integer gtype,
        @RequestParam(value = "goodsMain") @Parameter(description = "商城二级分类pkey") Integer goodsMain,
        @RequestParam(value = "title", required = false) @Parameter(description = "标题模糊搜索") String title,
        @RequestParam(value = "hotSort", defaultValue = "0") @Parameter(description = "销量排序 0-不排序/1-降序/2-升序") Integer hotSort,
        @RequestParam(value = "priceSort", defaultValue = "0") @Parameter(description = "价格排序 0-不排序/1-降序/2-升序") Integer priceSort);

    @Operation(summary = "民生商品搜索（滚动查询）", tags = AppTags.mobileGoods)
    @PostMapping(value = "/msd/search")
    public Result<AppMsdGoodsOnScroll> searchMsdGoods(
        @RequestParam(value = "title", required = false) @Parameter(description = "商品标题模糊搜索") String title,
        @RequestParam(value = "offset", defaultValue = "0") @Parameter(description = "起始值") Integer offset,
        @RequestParam(value = "limit", defaultValue = "10") @Parameter(description = "查询条数") Integer limit);
}
