package cn.tofocus.lejia.api.v1.market;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import cn.tofocus.core.Result;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.market.MktOriVenOnList;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@FeignClient(value = "lejia-server", contextId = "lejia-server-oriven", path = "/v1/market/oriven", 
fallbackFactory = MktOriVenApiFallback.class, configuration = FeignConfig.class)
public interface MktOriVenApi 
{
	@Operation(summary = "新增溯源信息", tags = ApiTags.custOriVen)
	@PostMapping("/ins")
	public Result<MktOriVenOnList> insOriVen(@RequestBody MktOriVenOnList entity);
	
	@Operation(summary = "获取溯源信息", tags = ApiTags.custOriVen)
	@PostMapping("/get")
	public Result<MktOriVenOnList> getOriVen(@RequestParam(value = "pkey") @Parameter(description = "溯源信息主键") Integer pkey);
	
	@Operation(summary = "获取溯源信息列表", tags = ApiTags.custOriVen)
    @PostMapping(value = "/query")
    public Result<PageResult<MktOriVenOnList>> queryOriVen(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号", hidden = true) int page,
        @RequestParam(value = "pagesize", defaultValue = "10000") @Parameter(description = "每页大小", hidden = true) int pagesize,
        @RequestParam(value = "merchant", required = false) @Parameter(description = "溯源商户") String merchant,
        @RequestParam(value = "goods", required = false) @Parameter(description = "溯源商品 ") String goods,
        @RequestParam(value = "vendor", required = false) @Parameter(description = "供应商") String vendor);
	
	@Operation(summary = "修改溯源信息", tags = ApiTags.custOriVen)
    @PostMapping(value = "/upd")
    public Result<MktOriVenOnList> updOriVen(@RequestBody MktOriVenOnList entity);
	
	@Operation(summary = "删除溯源信息", tags = ApiTags.custOriVen)
    @PostMapping(value = "/del")
    public Result<Boolean> delOriVen(@RequestParam(name = "pkey") Integer pkey);
	
	@Operation(summary = "导入溯源信息excel", tags = ApiTags.custOriVen)
	@PostMapping(value = "/importexcel")
	public Result<Boolean> importExcel(MultipartFile myfile);
}
