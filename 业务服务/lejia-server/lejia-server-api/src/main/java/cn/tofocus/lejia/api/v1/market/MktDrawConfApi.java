package cn.tofocus.lejia.api.v1.market;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.market.MktDrawConfOnList;
import io.swagger.v3.oas.annotations.Operation;

@FeignClient(value = "lejia-server", contextId = "lejia-server-drawconf", path = "/v1/market/drawconf", 
fallbackFactory = MktDrawConfFallback.class, configuration = FeignConfig.class)
public interface MktDrawConfApi 
{
	
	
	@Operation(summary = "获取规则设置", tags = ApiTags.custDrawWin)
    @PostMapping(value = "/get")
    public Result<MktDrawConfOnList> getDrawConf();
	
	@Operation(summary = "修改规则设置", tags = ApiTags.custDrawWin)
    @PostMapping(value = "/upd")
    public Result<MktDrawConfOnList> updDrawConf(@RequestParam("pkey") Integer pkey, @RequestParam("point")  Integer point);
	
	
}
