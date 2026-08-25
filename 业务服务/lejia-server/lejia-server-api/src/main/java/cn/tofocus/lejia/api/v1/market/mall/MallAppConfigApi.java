package cn.tofocus.lejia.api.v1.market.mall;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import cn.tofocus.core.Result;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.sys.AppConfig;
import cn.tofocus.lejia.bean.entity.market.MktAppConfig;
import io.swagger.v3.oas.annotations.Operation;

@FeignClient(value = "lejia-server", contextId = "lejia-server-appconfig", path = "/v1/market/mall/app/config", 
fallbackFactory = MallAppConfigFallback.class, configuration = FeignConfig.class)
public interface MallAppConfigApi 
{
	@Operation(summary = "获取配置", tags = ApiTags.custAppConfig)
	@PostMapping("/get")
	public Result<AppConfig> getAppConfig();
	
	@Operation(summary = "修改配置", tags = ApiTags.custAppConfig) 
    @PostMapping(value = "/upd")
    public Result<Boolean> updAppConfig(@RequestBody AppConfig config);
}
