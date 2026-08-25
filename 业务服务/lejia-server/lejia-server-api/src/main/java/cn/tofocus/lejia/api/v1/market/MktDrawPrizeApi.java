package cn.tofocus.lejia.api.v1.market;


import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import cn.tofocus.core.Result;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.market.MktDrawPrizeOnList;
import io.swagger.v3.oas.annotations.Operation;

@FeignClient(value = "lejia-server", contextId = "lejia-server-drawprize", path = "/v1/market/drawprize", 
fallbackFactory = MktDrawPrizeFallback.class, configuration = FeignConfig.class)
public interface MktDrawPrizeApi 
{
	
	
	@Operation(summary = "获取礼品配置", tags = ApiTags.custDrawWin)
    @PostMapping(value = "/query")
    public Result<List<MktDrawPrizeOnList>> queryDrawPrize();
	
	@Operation(summary = "修改礼品配置", tags = ApiTags.custDrawWin)
    @PostMapping(value = "/upd")
    public Result<MktDrawPrizeOnList> updDrawPrize(@RequestBody MktDrawPrizeOnList entity);
	
	
}
