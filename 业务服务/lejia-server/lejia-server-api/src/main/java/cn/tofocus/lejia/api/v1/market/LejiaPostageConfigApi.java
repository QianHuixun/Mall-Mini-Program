package cn.tofocus.lejia.api.v1.market;

import java.util.List;

import javax.validation.Valid;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import cn.tofocus.core.Result;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.market.MktDeliveryTimeConfig;
import cn.tofocus.lejia.bean.dto.market.MktPostageConfigOnList;
import cn.tofocus.lejia.bean.dto.market.PostageExpressConfigDTO;
import io.swagger.v3.oas.annotations.Operation;

@FeignClient(value = "lejia-server", contextId = "lejia-server-postage", path = "/v1/market/postage", 
fallbackFactory = LejiaPostageConfigApiFallback.class, configuration = FeignConfig.class)
public interface LejiaPostageConfigApi 
{
	
//	@Operation(summary = "新增运费配置", tags = ApiTags.custPostageConfig)
//	@PostMapping("/ins")
//	public Result<MktPostageConfigOnList> insPostageConfig(@RequestBody MktPostageConfigOnList entity);
	
	@Operation(summary = "获取运费配置列表", tags = ApiTags.custPostageConfig)
    @PostMapping(value = "/query")
    public Result<List<MktPostageConfigOnList>> queryPostageConfig();

	@Operation(summary = "修改运费配置", tags = ApiTags.custPostageConfig)
    @PostMapping(value = "/upd")
    public Result<List<MktPostageConfigOnList>> updPostageConfig(@RequestBody List<MktPostageConfigOnList> entitys);

    @Operation(summary = "获取预计送达时间配置列表", tags = ApiTags.custPostageConfig)
    @PostMapping(value = "/queryDeliveryTime")
    public Result<List<MktDeliveryTimeConfig>> queryDeliveryTimeConfig();
	
	@Operation(summary = "修改运费配置、夜间配送、跑腿配置", tags = ApiTags.custPostageConfig)
    @PostMapping(value = "/upd/market")
    public Result<Boolean> updPostageConfig(@RequestBody  @Valid PostageExpressConfigDTO entity);
	
}
