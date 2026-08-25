package cn.tofocus.lejia.app.v1.member;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.app.AppTags;
import cn.tofocus.lejia.bean.dto.app.market.MktGwcOnList;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@FeignClient(value = "lejia-server", contextId = "lejia-server-app-member-gwc", path = "/v1/app/market/lm/member/gwc", fallbackFactory = AppMemberGwcFallback.class, configuration = FeignConfig.class)
public interface AppMemberGwcApi {

	@Operation(summary = "添加商品到购物车", tags = AppTags.mobileMemberGwc)
    @PostMapping("/ins")
    public Result<Boolean> insGwc(@RequestParam(name = "goodsPkey") @Parameter(description = "商品主键") int goodsPkey,
        @RequestParam(name = "space") @Parameter(description = "商品规格") int space,
        @RequestParam(name = "goodsNum", required = false, defaultValue = "1") @Parameter(description = "商品数量") int goodsNum, 
        @RequestParam(name = "association", required = false) @Parameter(description = "加工主键") Integer association);

	@Operation(summary = "添加菜谱到购物车", tags = AppTags.mobileMemberGwc)
	@PostMapping("/insCp")
	public Result<Boolean> insCpGwc(@RequestParam(name = "pkey") @Parameter(description = "菜谱主键") int pkey);

	@Operation(summary = "获取购物车列表", tags = AppTags.mobileMemberGwc)
	@PostMapping(value = "/query")
	public Result<MktGwcOnList> queryGwc();

	@Operation(summary = "增加购物车里单个商品的数量", tags = AppTags.mobileMemberGwc)
	@PostMapping(value = "/add/num")
	@Deprecated
	public Result<Boolean> addGwcNum(@RequestParam(name = "pkey") @Parameter(description = "购物车主键") int pkey,
	    @RequestParam(name = "association", required = false) @Parameter(description = "加工主键") Integer association);

	@Operation(summary = "减少购物车里单个商品的数量", tags = AppTags.mobileMemberGwc)
	@PostMapping(value = "/less/num")
	@Deprecated
	public Result<Boolean> lessGwcNum(@RequestParam(name = "pkey") @Parameter(description = "购物车主键") int pkey,
	    @RequestParam(name = "association", required = false) @Parameter(description = "加工主键") Integer association);
	
	@Operation(summary = "增加购物车里单个商品的数量", tags = AppTags.mobileMemberGwc)
	@PostMapping(value = "/add/goods/num")
	public Result<Boolean> addGwcNum(@RequestParam(name = "goodsPkey") @Parameter(description = "商品主键") int goodsPkey,
        @RequestParam(name = "space") @Parameter(description = "商品规格") int space,
        @RequestParam(name = "goodsNum", required = false, defaultValue = "1") @Parameter(description = "商品数量") int goodsNum,
        @RequestParam(name = "association", required = false) @Parameter(description = "加工主键") Integer association);
	
	@Operation(summary = "减少购物车里单个商品的数量", tags = AppTags.mobileMemberGwc)
	@PostMapping(value = "/less/goods/num")
	public Result<Boolean> lessGwcNum(@RequestParam(name = "goodsPkey") @Parameter(description = "商品主键") int goodsPkey,
        @RequestParam(name = "space") @Parameter(description = "商品规格") int space,
        @RequestParam(name = "goodsNum", required = false, defaultValue = "1") @Parameter(description = "商品数量") int goodsNum,
        @RequestParam(name = "association", required = false) @Parameter(description = "加工主键") Integer association);

	@Operation(summary = "删除购物车", tags = AppTags.mobileMemberGwc)
	@PostMapping(value = "/del")
	public Result<Boolean> delGwc(@RequestParam(name = "pkey") @Parameter(description = "购物车主键") int pkey);

	@Operation(summary = "批量删除购物车", tags = AppTags.mobileMemberGwc)
	@PostMapping(value = "/delByPkeys")
	public Result<Boolean> delByPkeys(
		@RequestParam(name = "pkeys") @Parameter(description = "购物车主键列表") List<Integer> pkeys);

	@Operation(summary = "购物车凑单商品", tags = AppTags.mobileMemberGwc)
	@PostMapping(value = "/free")
	public Result<PageResult<Map<String, Object>>> freeDeliveryGoods(@RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") Integer page,
            @RequestParam(value = "pagesize", defaultValue = "10") @Parameter(description = "每页大小") Integer pagesize);
	
	@Operation(summary = "获取购物车商品数量", tags = AppTags.mobileMemberGwc)
	@PostMapping(value = "/get/gwc/goods/num")
	public Result<Integer> getGwcGoodsNum();
	
    @Operation(summary = "获取购物车商品合计价格", tags = AppTags.mobileMemberGwc)
    @PostMapping(value = "/get/gwc/goods/price")
    public Result<BigDecimal> getGwcGoodsPrice();
}
