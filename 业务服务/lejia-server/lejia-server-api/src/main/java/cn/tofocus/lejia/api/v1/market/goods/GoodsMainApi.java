package cn.tofocus.lejia.api.v1.market.goods;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.PkeyNameDTO;
import cn.tofocus.lejia.bean.dto.gtype.GtypeInfo;
import cn.tofocus.lejia.bean.dto.market.MktGoodsMainOnList;
import cn.tofocus.lejia.bean.dto.market.MktGoodsMainThreeOnList;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@FeignClient(value = "lejia-server", contextId = "lejia-server-goods-main", path = "/v1/market/goods/main", 
fallbackFactory = GoodsMainFallback.class, configuration = FeignConfig.class)
public interface GoodsMainApi 
{
	
	@Operation(summary = "新增商品", tags = ApiTags.custGoodsMain)
	@PostMapping("/ins")
	public Result<Integer> insGoodsMain(@RequestBody MktGoodsMainOnList entity);
	
	@Operation(summary = "获取商品库列表", tags = ApiTags.custGoodsMain)
    @PostMapping(value = "/query")
    public Result<PageResult<MktGoodsMainOnList>> queryGoodsMain(
        @RequestParam(value = "page", defaultValue = "0", required = false) @Parameter(description = "页号") Integer page,
        @RequestParam(value = "pagesize", defaultValue = "10", required = false) @Parameter(description = "每页大小") Integer pagesize,
        @RequestParam(value = "gtype",required = false) @Parameter(description = "分类id") Integer gtype,
        @RequestParam(value = "name",required = false) @Parameter(description = "名称") String name, 
        @RequestParam(value = "enabled",required = false) Boolean enabled);
	
	
	@Operation(summary = "修改商品", tags = ApiTags.custGoodsMain)
    @PostMapping(value = "/upd")
    public Result<MktGoodsMainOnList> updGoodsMain(
    		@RequestParam(name = "pkey") Integer pkey, 
    		@RequestParam(name = "name", required = false) String name, 
    		@RequestParam(name = "sort", required = false) Integer sort,
    		@RequestParam(name = "gtype", required = false) Integer gtype,
    		@RequestParam(name = "remark", required = false) String remark);
	
	@Operation(summary = "删除商品", tags = ApiTags.custGoodsMain)
    @PostMapping(value = "/del")
    public Result<Boolean> delGoodsMain(@RequestParam(name = "pkey") Integer pkey);
	
	@Operation(summary = "商品启用", tags = ApiTags.custGoodsMain)
    @PostMapping(value = "/enable/start")
    public Result<Boolean> startGoodsMain(@RequestParam(name = "pkey") Integer pkey);
	
	@Operation(summary = "商品停用", tags = ApiTags.custGoodsMain)
    @PostMapping(value = "/enable/stop")
    public Result<Boolean> stopGoodsMain(@RequestParam(name = "pkey") Integer pkey);
	
	@Operation(summary = "获取礼品券商品库下拉列表", tags = ApiTags.custGoodsMain)
    @PostMapping(value = "/list/gift")
    public Result<List<PkeyNameDTO>> listGift();
	
    @Operation(summary = "获取优惠券商品库下拉列表", tags = ApiTags.custGoodsMain)
    @PostMapping(value = "/list/coupon")
    public Result<List<PkeyNameDTO>> listCoupon();

    @Operation(summary = "获取平台商品分类下拉接口", tags = ApiTags.custGoodsMain)
    @PostMapping(value = "/sys/list")
    public Result<List<GtypeInfo>> listSys();
    
    
    
//  ----------三级分类------------
    
    @Operation(summary = "新增三级分类", tags = ApiTags.custGoodsMain)
    @PostMapping("/three/ins")
    public Result<Integer> insGoodsMainThree(@RequestBody MktGoodsMainThreeOnList entity);
    
    @Operation(summary = "获取三级分类列表", tags = ApiTags.custGoodsMain)
    @PostMapping(value = "/three/query")
    public Result<PageResult<MktGoodsMainThreeOnList>> queryGoodsMainThree(
        @RequestParam(value = "page", defaultValue = "0", required = false) @Parameter(description = "页号") Integer page,
        @RequestParam(value = "pagesize", defaultValue = "10", required = false) @Parameter(description = "每页大小") Integer pagesize,
        @RequestParam(value = "twoGtype",required = false) @Parameter(description = "二级分类id") Integer twoGtype,
        @RequestParam(value = "name",required = false) @Parameter(description = "名称") String name, 
        @RequestParam(value = "enabled",required = false) Boolean enabled);
    
    
    @Operation(summary = "修改三级分类", tags = ApiTags.custGoodsMain)
    @PostMapping(value = "/three/upd")
    public Result<MktGoodsMainThreeOnList> updGoodsMainThree(
            @RequestParam(name = "pkey") Integer pkey, 
            @RequestParam(name = "name", required = false) String name, 
            @RequestParam(name = "sort", required = false) Integer sort,
            @RequestParam(name = "twoGtype", required = false) Integer twoGtype,
            @RequestParam(name = "remark", required = false) String remark);
    
    @Operation(summary = "删除三级分类", tags = ApiTags.custGoodsMain)
    @PostMapping(value = "/three/del")
    public Result<Boolean> delGoodsMainThree(@RequestParam(name = "pkey") Integer pkey);
    
    @Operation(summary = "三级分类启用", tags = ApiTags.custGoodsMain)
    @PostMapping(value = "/three/enable/start")
    public Result<Boolean> startGoodsMainThree(@RequestParam(name = "pkey") Integer pkey);
    
    @Operation(summary = "三级分类停用", tags = ApiTags.custGoodsMain)
    @PostMapping(value = "/three/enable/stop")
    public Result<Boolean> stopGoodsMainThree(@RequestParam(name = "pkey") Integer pkey);
    
}
