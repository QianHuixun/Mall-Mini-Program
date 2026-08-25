package cn.tofocus.lejia.api.v1.market.goods;

import java.util.List;

import cn.tofocus.lejia.bean.dto.goods.GoodsAdvertOnInfo;
import cn.tofocus.lejia.bean.dto.goods.GoodsRecommendInfo;
import cn.tofocus.lejia.bean.dto.goods.GoodsRecommendOnPage;
import cn.tofocus.lejia.bean.enums.GoodsRecommendZone;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.market.DropDTO;
import cn.tofocus.lejia.bean.dto.market.MktGoodsDetailsDTO;
import cn.tofocus.lejia.bean.dto.market.MktGoodsOnList;
import cn.tofocus.lejia.bean.dto.market.MktGoodsUpdDTO;
import cn.tofocus.lejia.bean.enums.MType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import javax.validation.Valid;

@FeignClient(value = "lejia-server", contextId = "lejia-server-goods-manager", path = "/v1/market/goods/manager", 
fallbackFactory = GoodsFallback.class, configuration = FeignConfig.class)
public interface GoodsApi 
{
	@Operation(summary = "新增商品", tags = ApiTags.custGoods)
	@PostMapping("/ins")
	public Result<Integer> insGoods(@RequestBody MktGoodsDetailsDTO entity);
    
	@Operation(summary = "修改商品", tags = ApiTags.custGoods)
	@PostMapping("/upd")
	public Result<Integer> updGoods(@RequestBody MktGoodsUpdDTO entity);
	
	@Operation(summary = "检测商品价格和采购价", tags = ApiTags.custGoods)
	@PostMapping("/checkPrice")
	public Result<String> checkPricePurchase(@RequestParam(value = "goodsPkey") Integer goodsPkey);
	
	@Operation(summary = "获取商品列表", tags = ApiTags.custGoods)
    @PostMapping(value = "/query")
    public Result<PageResult<MktGoodsDetailsDTO>> queryGoods(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号", hidden = true) Integer page,
        @RequestParam(value = "pagesize", defaultValue = "100000") @Parameter(description = "每页大小") Integer pagesize,
        @RequestParam(value = "mType",required = false) @Parameter(description = "商品属性") MType mType,
        @RequestParam(value = "title", required = false) @Parameter(description = "标题") String title,
        @RequestParam(value = "gtype", required = false) @Parameter(description = "分类id") Integer gtype,
        @RequestParam(value = "enabled", required = false) @Parameter(description = "是否上架") Boolean enabled,
        @RequestParam(value = "status", defaultValue = "0") @Parameter(description = "发售状态") Integer status,
        @RequestParam(value = "farmer", required = false) @Parameter(description = "市场pkey") String farmer);
	
	@Operation(summary = "广告点击效果商品列表", tags = ApiTags.custGoods)
    @PostMapping(value = "/img/query")
    public Result<PageResult<GoodsAdvertOnInfo>> queryAdvertGoods(
        @RequestParam(value = "page", defaultValue = "0", required = false) @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", defaultValue = "10", required = false) @Parameter(description = "每页大小") int pagesize,
        @RequestParam(value = "farmer", required = false) @Parameter(description = "市场pkey") String farmer,
        @RequestParam(value = "title", required = false) @Parameter(description = "标题") String title);
	
	@Operation(summary = "获取商品下拉列表", tags = ApiTags.custGoods)
    @PostMapping(value = "/list")
    public Result<List<MktGoodsOnList>> listGoods();
	
	@Operation(summary = "商品下拉列表名称搜索", tags = ApiTags.custGoods)
    @PostMapping(value = "/list/title")
    public Result<List<DropDTO>> listGoods(@RequestParam(value = "title") String title);
	
	@Operation(summary = "获取商品详情", tags = ApiTags.custGoods)
    @PostMapping(value = "/get")
    public Result<MktGoodsDetailsDTO> getGoods(@RequestParam(value = "pkey")Integer pkey);
	
	@Operation(summary = "删除商品", tags = ApiTags.custGoods)
    @PostMapping(value = "/del")
    public Result<Boolean> delGoods(@RequestParam(name = "pkey") Integer pkey);
	
	@Operation(summary = "删除商品", tags = ApiTags.custGoods)
	@PostMapping(value = "/del/list")
	public Result<Boolean> delListGoods(@RequestParam(name = "pkeys") List<Integer> pkeys);
	
	@Operation(summary = "商品启用", tags = ApiTags.custGoods)
    @PostMapping(value = "/enable/start")
    public Result<Boolean> startGoods(@RequestParam(name = "pkey") Integer pkey);
	
	@Operation(summary = "商品停用", tags = ApiTags.custGoods)
    @PostMapping(value = "/enable/stop")
    public Result<Boolean> stopGoods(@RequestParam(name = "pkey") Integer pkey);	
	
	@Operation(summary = "编辑富文本框", tags = ApiTags.custGoods)
    @PostMapping(value = "/richTemp/upd")
    public Result<Boolean> updRichTemp(@RequestParam(name = "content", required = false)String content);
	
	@Operation(summary = "获取富文本框", tags = ApiTags.custGoods)
    @PostMapping(value = "/richTemp/get")
    public Result<String> getRichTemp();

    @Operation(summary = "开启/关闭商品的猜我喜欢", tags = ApiTags.custGoods)
    @PostMapping("/enableGuessLike")
    public Result<Boolean> enableGuessLike(
        @RequestParam("pkey") @Parameter(description = "主键", required = true) Integer pkey);
    
    @Operation(summary = "启停商品轮播推荐", tags = ApiTags.custGoods)
    @PostMapping("/zone/recommend/enable")
    public Result<Boolean> enableZoneRecommend(@RequestParam("pkey") @Parameter(description = "主键") Integer pkey,
        @RequestParam("enabled") @Parameter(description = "启停") Boolean enabled);
    
    @Operation(summary = "获取商品专区显示名称", tags = ApiTags.custGoods)
    @PostMapping("/zone/displayName/get")
    public Result<String> getZoneDisplayName(@RequestParam("mType") @Parameter(description = "商品属性") MType mType);
    
    @Operation(summary = "设置商品专区显示名称", tags = ApiTags.custGoods)
    @PostMapping("/zone/displayName/set")
    public Result<Boolean> setZoneDisplayName(@RequestParam("mType") @Parameter(description = "商品属性") MType mType,
        @RequestParam("displayName") @Parameter(description = "显示名称") String displayName);

    @Operation(summary = "查询推荐商品", tags = ApiTags.custGoods)
    @PostMapping("/recommend/query")
    public Result<PageResult<GoodsRecommendOnPage>> queryRecommendGoods(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") Integer page,
        @RequestParam(value = "pagesize", defaultValue = "10") @Parameter(description = "每页大小") Integer pagesize,
        @RequestParam(value = "sourceGoods", required = false) @Parameter(description = "来源商品主键（传值则查该商品关联推荐商品，为空则查运营端配置推荐商品）") Integer sourceGoods,
        @RequestParam(value = "goodsFarmer", required = false) @Parameter(description = "商品所属市场") String goodsFarmer,
        @RequestParam(value = "mType", required = false) @Parameter(description = "商品属性") MType mType,
        @RequestParam(value = "vendor", required = false) @Parameter(description = "商户") String vendor,
        @RequestParam(value = "title", required = false) @Parameter(description = "标题") String title,
        @RequestParam(value = "zone", required = false) @Parameter(description = "推荐区域") GoodsRecommendZone zone);
    
    @Operation(summary = "获取推荐商品", tags = ApiTags.custGoods)
    @PostMapping("/recommend/get")
    public Result<GoodsRecommendInfo> getRecommendGoods(
        @RequestParam(value = "pkey") @Parameter(description = "主键") Integer pkey);
    
    @Operation(summary = "新增推荐商品", tags = ApiTags.custGoods)
    @PostMapping("/recommend/add")
    public Result<Boolean> addRecommendGoods(@RequestBody @Valid GoodsRecommendInfo info);
    
    @Operation(summary = "编辑推荐商品", tags = ApiTags.custGoods)
    @PostMapping("/recommend/upd")
    public Result<Boolean> updRecommendGoods(@RequestBody @Valid GoodsRecommendInfo info);
    
    @Operation(summary = "删除推荐商品", tags = ApiTags.custGoods)
    @PostMapping("/recommend/del")
    public Result<Boolean> delRecommendGoods(@RequestParam(value = "pkey") @Parameter(description = "主键") Integer pkey);
}
