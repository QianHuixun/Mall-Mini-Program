package cn.tofocus.lejia.api.v1.sys;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.sys.SysCompanyOnList;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@FeignClient(value = "lejia-server", contextId = "lejia-server", path = "/v1/sys/company", fallbackFactory = LejiaSysFallback.class, configuration = FeignConfig.class)
public interface LejiaSysApi
{
	/*****************
     * 公司
     ****************/
	
	@Operation(summary = "新增公司", tags = ApiTags.custCompany)
	@PostMapping("/ins")
	public Result<SysCompanyOnList> insCompany( @RequestBody SysCompanyOnList entity);

	@Operation(summary = "获取公司", tags = ApiTags.custCompany)
	@PostMapping("/get")
	public Result<SysCompanyOnList> getCompany(@RequestParam(value = "pkey") @Parameter(description = "公司主键") String pkey);
	
	@Operation(summary = "获取公司列表", tags = ApiTags.custCompany)
    @PostMapping(value = "/query")
    public Result<PageResult<SysCompanyOnList>> queryCompany(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", defaultValue = "10") @Parameter(description = "每页大小") int pagesize,
        @RequestParam(value = "companyName", required = false) @Parameter(description = "公司名") String companyName);
	
	@Operation(summary = "修改公司", tags = ApiTags.custCompany)
    @PostMapping(value = "/upd")
    public Result<SysCompanyOnList> updCompany(
    		@RequestParam(name = "pkey") String pkey, 
    		@RequestParam(name = "name", required = false) String name, 
    		@RequestParam(name = "addr", required = false) String addr);
	
	@Operation(summary = "删除公司", tags = ApiTags.custCompany)
    @PostMapping(value = "/del")
    public Result<Boolean> delCompany(@RequestParam(name = "pkey") String pkey);
	
	@Operation(summary = "公司启用", tags = ApiTags.custCompany)
    @PostMapping(value = "/enable/start")
    public Result<Boolean> startCompany(@RequestParam(name = "pkey") String pkey);
	
	@Operation(summary = "公司停用", tags = ApiTags.custCompany)
    @PostMapping(value = "/enable/stop")
    public Result<Boolean> stopCompany(@RequestParam(name = "pkey") String pkey);
}
