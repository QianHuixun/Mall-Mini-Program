//package cn.tofocus.lejia.api.v1.market.goods;
//
//import java.math.BigDecimal;
//
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import cn.tofocus.core.Result;
//import cn.tofocus.core.feign.FeignConfig;
//import cn.tofocus.core.page.PageResult;
//import cn.tofocus.lejia.api.v1.ApiTags;
//import cn.tofocus.lejia.bean.dto.market.MktGoodsSpaceOnList;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.Parameter;
//
//@FeignClient(value = "lejia-server", contextId = "lejia-server-goods-space", path = "/v1/market/goods/space", 
//fallbackFactory = GoodsSpaceFallback.class, configuration = FeignConfig.class)
//public interface GoodsSpaceApi 
//{
//	@Operation(summary = "新增商品规格", tags = ApiTags.custGoodsSpace)
//	@PostMapping("/ins")
//	public Result<MktGoodsSpaceOnList> insSpace(@RequestBody MktGoodsSpaceOnList entity);
//	
//	@Operation(summary = "获取商品规格", tags = ApiTags.custGoodsSpace)
//	@PostMapping("/get")
//	public Result<MktGoodsSpaceOnList> getSpace(@RequestParam(value = "pkey") @Parameter(description = "商品规格主键") Integer pkey);
//	
//	@Operation(summary = "获取商品规格列表", tags = ApiTags.custGoodsSpace)
//    @PostMapping(value = "/query")
//    public Result<PageResult<MktGoodsSpaceOnList>> querySpace(
//        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
//        @RequestParam(value = "pagesize", defaultValue = "10") @Parameter(description = "每页大小") int pagesize);
//	
//	@Operation(summary = "修改商品规格", tags = ApiTags.custGoodsSpace)
//    @PostMapping(value = "/upd")
//    public Result<MktGoodsSpaceOnList> updSpace(
//    		@RequestParam(name = "pkey") Integer pkey, 
//    		@RequestParam(name = "space", required = false) @Parameter(description = "规格")String space, 
//    		@RequestParam(name = "weight", required = false) @Parameter(description = "毛重")BigDecimal weight,
//    		@RequestParam(name = "kcNum", required = false) @Parameter(description = "库存数量")Integer kcNum,
//    		@RequestParam(name = "xsNum", required = false) @Parameter(description = "销售数量") Integer xsNum,
//    		@RequestParam(name = "price", required = false) @Parameter(description = "价格")BigDecimal price,
//    		@RequestParam(name = "priceOld", required = false) @Parameter(description = "原价") BigDecimal priceOld,
//    		@RequestParam(name = "point", required = false) @Parameter(description = "积分") Integer point,
//    		@RequestParam(name = "comm", required = false) @Parameter(description = "佣金") BigDecimal comm);
//	
//	@Operation(summary = "删除商品规格", tags = ApiTags.custGoodsSpace)
//    @PostMapping(value = "/del")
//    public Result<Boolean> delSpace(@RequestParam(name = "pkey") Integer pkey);
//}
