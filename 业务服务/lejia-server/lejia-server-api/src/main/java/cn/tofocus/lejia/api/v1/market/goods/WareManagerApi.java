package cn.tofocus.lejia.api.v1.market.goods;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.exception.DefaultFallbackFactory;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.market.MktWareLineInsDTO;
import cn.tofocus.lejia.bean.dto.market.MktWareLineOnList;
import cn.tofocus.lejia.bean.dto.market.WareAggreDTO;
import cn.tofocus.lejia.bean.enums.WareType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@FeignClient(value = "lejia-server", contextId = "lejia-server-ware", path = "/v1/market/ware", 
fallbackFactory = DefaultFallbackFactory.class, configuration = FeignConfig.class)
public interface WareManagerApi {

	@Operation(summary = "采购入库和库存盘点", tags = ApiTags.custWareManager)
	@PostMapping("/ins")
	public Result<Integer> insWare(@RequestBody MktWareLineInsDTO entity);
	
	@Operation(summary = "获取库存列表", tags = ApiTags.custWareManager)
    @PostMapping(value = "/query")
    public Result<PageResult<MktWareLineOnList>> queryWare(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", defaultValue = "10") @Parameter(description = "每页大小") int pagesize,
        @RequestParam(value = "goodsPkey") @Parameter(description = "商品pkey") int goodsPkey,
        @RequestParam(value = "type", required = false) @Parameter(description = "类型,可不传") WareType type);
	
	@Operation(summary = "获取库存列表统计数据", tags = ApiTags.custWareManager)
    @PostMapping(value = "/sum")
    public Result<List<WareAggreDTO>> queryWareSum(
        @RequestParam(value = "goodsPkey") @Parameter(description = "商品pkey") int goodsPkey);
	
}
