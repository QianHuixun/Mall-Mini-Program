package cn.tofocus.lejia.api.v1.market;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.market.MktLogisticsOnList;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@FeignClient(value = "lejia-server", contextId = "lejia-server-logistics", path = "/v1/market/logistics", 
fallbackFactory = LejiaLogisticsFallback.class, configuration = FeignConfig.class)
public interface LejiaLogisticsApi 
{
	
	@Operation(summary = "新增快递公司", tags = ApiTags.custLogistics)
	@PostMapping("/ins")
	public Result<MktLogisticsOnList> insLogistics(@RequestBody MktLogisticsOnList entity);
	
	@Operation(summary = "获取快递公司", tags = ApiTags.custLogistics)
	@PostMapping("/get")
	public Result<MktLogisticsOnList> getLogistics(@RequestParam(value = "pkey") @Parameter(description = "快递公司主键") Integer pkey);
	
	@Operation(summary = "获取快递公司列表", tags = ApiTags.custLogistics)
    @PostMapping(value = "/query")
    public Result<PageResult<MktLogisticsOnList>> queryLogistics(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", defaultValue = "10") @Parameter(description = "每页大小") int pagesize,
        @RequestParam(value = "logisticsName", required = false) @Parameter(description = "快递公司名") String logisticsName,
        @RequestParam(value = "enabled", required = false) @Parameter(description = "是否关闭,传null为全部", hidden = true) Boolean enabled);
	
	@Operation(summary = "修改快递公司", tags = ApiTags.custLogistics)
    @PostMapping(value = "/upd")
    public Result<MktLogisticsOnList> updLogistics(
    		@RequestParam(name = "pkey") Integer pkey, 
    		@RequestParam(name = "name", required = false) String name, 
    		@RequestParam(name = "descp", required = false) String descp);
	
	@Operation(summary = "删除快递公司", tags = ApiTags.custLogistics)
    @PostMapping(value = "/del")
    public Result<Boolean> delLogistics(@RequestParam(name = "pkey") Integer pkey);
	
	@Operation(summary = "快递公司启用", tags = ApiTags.custLogistics)
    @PostMapping(value = "/enable/start")
    public Result<Boolean> startLogistics(@RequestParam(name = "pkey") Integer pkey);
	
	@Operation(summary = "快递公司停用", tags = ApiTags.custLogistics)
    @PostMapping(value = "/enable/stop")
    public Result<Boolean> stopLogistics(@RequestParam(name = "pkey") Integer pkey);
	
}
