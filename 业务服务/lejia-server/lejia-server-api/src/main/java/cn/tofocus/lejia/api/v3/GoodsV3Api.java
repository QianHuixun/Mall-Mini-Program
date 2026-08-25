package cn.tofocus.lejia.api.v3;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.goods.GoodsCouponOnPage;
import cn.tofocus.lejia.bean.dto.market.MktGoodsDetailsDTO;
import cn.tofocus.lejia.bean.enums.MType;
import cn.tofocus.lejia.bean.enums.v3.SortType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

public interface GoodsV3Api
{
    
    @Operation(summary = "获取商品列表", tags = ApiTags.LEJIA_V3_GOODS)
    @PostMapping(value = "/query")
    public Result<PageResult<MktGoodsDetailsDTO>> queryGoods(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", defaultValue = "10") @Parameter(description = "每页大小") int pagesize,
        @RequestParam(value = "mType") @Parameter(description = "商品属性") MType mType,
        @RequestParam(value = "sortType", required = false) @Parameter(description = "排序") SortType sortType,
        @RequestParam(value = "sort", required = false, defaultValue = "false") @Parameter(description = "true:正序，false:倒序") Boolean sort,
        @RequestParam(value = "title", required = false) @Parameter(description = "标题") String title,
        @RequestParam(value = "gtype", required = false) @Parameter(description = "分类id") Integer gtype,
        @RequestParam(value = "goodsMain", required = false) @Parameter(description = "二级分类id") Integer goodsMain,
        @RequestParam(value = "threeGtype", required = false) @Parameter(description = "三级分类id") Integer threeGtype,
        @RequestParam(value = "enabled", required = false) @Parameter(description = "是否上架") Boolean enabled,
        @RequestParam(value = "status", defaultValue = "0") @Parameter(description = "发售状态") Integer status,
        @RequestParam(value = "vendor", required = false) @Parameter(description = "商户主键") Integer vendor,
        @RequestParam(value = "booth", required = false) @Parameter(description = "摊位号") String booth,
        @RequestParam(value = "supplier", required = false) @Parameter(description = "供应商主键") Integer supplier);

    @Operation(summary = "获取优惠券商品列表", tags = ApiTags.LEJIA_V3_GOODS)
    @PostMapping(value = "/coupon/query")
    public Result<PageResult<GoodsCouponOnPage>> queryGoodsCoupon(
        @RequestParam(value = "page", required = false, defaultValue = "0") @Parameter(description = "页号") Integer page,
        @RequestParam(value = "pagesize", required = false, defaultValue = "10") @Parameter(description = "每页大小") Integer pagesize,
        @RequestParam(value = "title", required = false) @Parameter(description = "标题") String title,
        @RequestParam(value = "sortType", required = false) @Parameter(description = "排序") SortType sortType,
        @RequestParam(value = "sort", required = false, defaultValue = "false") @Parameter(description = "true:正序，false:倒序") Boolean sort,
        @RequestParam(value = "enabled", required = false) @Parameter(description = "是否上架") Boolean enabled);
}
