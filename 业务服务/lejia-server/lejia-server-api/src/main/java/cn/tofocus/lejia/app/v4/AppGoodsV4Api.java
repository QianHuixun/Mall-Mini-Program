package cn.tofocus.lejia.app.v4;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.GroupResult;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.app.AppTags;
import cn.tofocus.lejia.bean.dto.app.AppVendorGtypeInfo;
import cn.tofocus.lejia.bean.dto.app.goods.AppGoodsV4OnList;
import cn.tofocus.lejia.bean.dto.goods.GoodsListItem;
import cn.tofocus.lejia.bean.dto.goods.GoodsListItemV2;
import cn.tofocus.lejia.bean.enums.GoodsSortType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

public interface AppGoodsV4Api
{
    @Operation(summary = "获取商品列表", tags = AppTags.mobileGoodsV4)
    @PostMapping(value = "/query")
    public Result<PageResult<AppGoodsV4OnList>> queryAppGoods(
        @RequestParam(value = "page", defaultValue = "0", required = false) @Parameter(description = "页号") Integer page,
        @RequestParam(value = "pagesize", defaultValue = "10", required = false) @Parameter(description = "每页大小") Integer pagesize,
        @RequestParam(value = "gtype", required = false) @Parameter(description = "分类id") Integer gtype,
        @RequestParam(value = "goodsMain", required = false) @Parameter(description = "二级分类pkey") Integer goodsMain);
    
    @Operation(summary = "获取三级分类下的商品列表", tags = AppTags.mobileGoodsV4)
    @PostMapping(value = "/threeGtype/query")
    public Result<PageResult<AppGoodsV4OnList>> queryThreeGtypeAppGoods(
        @RequestParam(value = "page", defaultValue = "0", required = false) @Parameter(description = "页号") Integer page,
        @RequestParam(value = "pagesize", defaultValue = "10", required = false) @Parameter(description = "每页大小") Integer pagesize,
        @RequestParam(value = "threeGtype") @Parameter(description = "三级分类id") Integer threeGtype,
        @RequestParam(value = "goodsSortType", required = false, defaultValue = "SALED") @Parameter(description = "排序分类") GoodsSortType goodsSortType,
        @RequestParam(value = "sortDesc", required = false, defaultValue = "false") @Parameter(description = "true:降序, false:升序") Boolean sortDesc);
    
    @Operation(summary = "分类页获取商户列表", tags = AppTags.mobileGoodsV4)
    @PostMapping(value = "/gtype/vendor/query")
    public Result<PageResult<AppVendorGtypeInfo>> queryGtypeVendor(
        @RequestParam(value = "page", defaultValue = "0", required = false) @Parameter(description = "页号") Integer page,
        @RequestParam(value = "pagesize", defaultValue = "10", required = false) @Parameter(description = "每页大小") Integer pagesize,
        @RequestParam(value = "gtype", required = false) @Parameter(description = "分类id") Integer gtype,
        @RequestParam(value = "name", required = false) @Parameter(description = "商户名称") String name);
    
    @Operation(summary = "一级分类商品列表", tags = AppTags.mobileGoodsV4)
    @PostMapping(value = "/gtype/query")
    public Result<GroupResult<String, GoodsListItemV2>> queryAppGtypeGoods(
        @RequestParam(value = "from", defaultValue = "0", required = false) int from,
        @RequestParam(value = "limit", defaultValue = "10", required = false) int limit,
        @RequestParam(value = "gtype", required = false) @Parameter(description = "分类id") Integer gtype,
        @RequestParam(value = "goodsSortType", required = false, defaultValue = "SALED") @Parameter(description = "排序分类") GoodsSortType goodsSortType,
        @RequestParam(value = "sortDesc", required = false, defaultValue = "false") @Parameter(description = "true:降序, false:升序") Boolean sortDesc);
    
    @Operation(summary = "二级分类商品列表", tags = AppTags.mobileGoodsV4)
    @PostMapping(value = "/goodsMain/query")
    public Result<GroupResult<String, GoodsListItemV2>> queryAppGoodsMainGoods(
        @RequestParam(value = "from", defaultValue = "0", required = false) int from,
        @RequestParam(value = "limit", defaultValue = "10", required = false) int limit,
        @RequestParam(value = "goodsMain") @Parameter(description = "二级分类pkey") Integer goodsMain,
        @RequestParam(value = "goodsSortType", required = false, defaultValue = "SALED") @Parameter(description = "排序分类") GoodsSortType goodsSortType,
        @RequestParam(value = "sortDesc", required = false, defaultValue = "false") @Parameter(description = "true:降序, false:升序") Boolean sortDesc,
        @RequestParam(value = "limitGoodsMain", defaultValue = "false") @Parameter(description = "是否限制二级分类") Boolean limitGoodsMain,
        @RequestParam(value = "deliveryType", required = false, defaultValue = "0") @Parameter(description = "配送方式(全部0,快递配送1,骑手配送2),不传默认是0") int deliveryType);
    
    @Operation(summary = "二级分类是否关联运营端商品", tags = AppTags.mobileGoodsV4)
    @PostMapping(value = "/goodsMain/correlation")
    public Result<Boolean> correlationGoodsMain(
        @RequestParam(value = "goodsMain") @Parameter(description = "二级分类pkey") Integer goodsMain);
    
    
//    @Operation(summary = "二级分类商品列表", tags = AppTags.mobileGoodsV4)
    @PostMapping(value = "/goodsMain/query/test")
    public Result<GroupResult<String, GoodsListItemV2>> queryAppGoodsMainGoodsTest(
        @RequestParam(value = "from", defaultValue = "0", required = false) int from,
        @RequestParam(value = "limit", defaultValue = "10", required = false) int limit,
        @RequestParam(value = "goodsMain") @Parameter(description = "二级分类pkey") Integer goodsMain,
        @RequestParam(value = "goodsSortType", required = false, defaultValue = "SALED") @Parameter(description = "排序分类") GoodsSortType goodsSortType,
        @RequestParam(value = "sortDesc", required = false, defaultValue = "false") @Parameter(description = "true:降序, false:升序") Boolean sortDesc,
        @RequestParam(value = "limitGoodsMain", defaultValue = "false") @Parameter(description = "是否限制二级分类") Boolean limitGoodsMain);
    
    @Operation(summary = "分类页下的商户列表-新的滚动方式", tags = AppTags.mobileGoodsV4)
    @PostMapping(value = "/gtype/vendornew/query")
    public Result<GroupResult<String, AppVendorGtypeInfo>> queryAppGtypeVendor(
        @RequestParam(value = "from", defaultValue = "0", required = false) int from,
        @RequestParam(value = "limit", defaultValue = "10", required = false) int limit,
        @RequestParam(value = "gtype", required = false) @Parameter(description = "分类id") Integer gtype,
        @RequestParam(value = "name", required = false) String name);
    
    @Operation(summary = "商户页面下二级分类商品列表", tags = AppTags.mobileGoodsV4)
    @PostMapping(value = "/vendor/goodsMain/query")
    public Result<GroupResult<String, GoodsListItem>> queryAppGoodsMainVendorGoods(
        @RequestParam(value = "from", defaultValue = "0", required = false) int from,
        @RequestParam(value = "limit", defaultValue = "10", required = false) int limit,
        @RequestParam(value = "vendor", required = true) @Parameter(description = "商户主键") Integer vendor,
        @RequestParam(value = "name", required = false) @Parameter(description = "商品名称") String name,
        @RequestParam(value = "goodsMain", required = false) @Parameter(description = "二级分类pkey") Integer goodsMain,
        @RequestParam(value = "priceSort", required = false) @Parameter(description = "价格排序") Boolean priceSort,
        @RequestParam(value = "xsNumSort", required = false) @Parameter(description = "销量排序") Boolean xsNumSort);
    
}
