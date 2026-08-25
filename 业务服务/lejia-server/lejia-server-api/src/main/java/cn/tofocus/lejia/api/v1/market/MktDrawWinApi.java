package cn.tofocus.lejia.api.v1.market;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.market.MktDrawWinOnList;
import cn.tofocus.lejia.bean.enums.PrizeStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@FeignClient(value = "lejia-server", contextId = "lejia-server-drawwin", path = "/v1/market/drawwin", 
fallbackFactory = MktDrawWinFallback.class, configuration = FeignConfig.class)
public interface MktDrawWinApi 
{
	
	@Operation(summary = "获取中奖记录", tags = ApiTags.custDrawWin)
    @PostMapping(value = "/query")
    public Result<PageResult<MktDrawWinOnList>> queryDrawWin( 
    		@RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
            @RequestParam(value = "pagesize", defaultValue = "10000") @Parameter(description = "每页大小") int pagesize,
            @RequestParam(value = "status", required = false) PrizeStatus status);
	
	@Operation(summary = "获取中奖记录次数", tags = ApiTags.custDrawWin)
    @PostMapping(value = "/query/num")
    public Result<List<Map<String, Object>>> queryNumDrawWin();
	
	@Operation(summary = "设置奖品已发货", tags = ApiTags.custDrawWin)
    @PostMapping(value = "/upd/status")
    public Result<MktDrawWinOnList> updDrawWin(
    		@RequestParam("pkey") Integer pkey,
    		@RequestParam("logistics")@Parameter(description = "快递公司") String logistics,
    		@RequestParam("express")@Parameter(description = "快递单号") String express);
	
	
}
