package cn.tofocus.lejia.api.v1.market;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.market.MktKryOrderOnList;
import cn.tofocus.lejia.bean.dto.market.MktKryVendorOnList;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@FeignClient(value = "lejia-server", contextId = "lejia-server-kry", path = "/v1/market/kry", 
fallbackFactory = MktKryApiFallback.class, configuration = FeignConfig.class)
public interface MktKryApi 
{
	@Operation(summary = "新增客如云商户", tags = ApiTags.custKryVendor)
	@PostMapping("/vendor/ins")
	public Result<MktKryVendorOnList> insKryVendor(
			@RequestParam(value = "uuid") @Parameter(description = "客如云id") Long uuid,
			@RequestParam(value = "name") @Parameter(description = "商户名称") String name,
			@RequestParam(value = "mobile") @Parameter(description = "手机号码") String mobile,
			@RequestParam(value = "manager") @Parameter(description = "负责人") String manager,
			@RequestParam(value = "token") @Parameter(description = "token") String token
			);
	
	@Operation(summary = "获取客如云商户列表", tags = ApiTags.custKryVendor)
    @PostMapping(value = "/vendor/query")
    public Result<PageResult<MktKryVendorOnList>> queryKryVendor(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号", hidden = true) int page,
        @RequestParam(value = "pagesize", defaultValue = "10000") @Parameter(description = "每页大小", hidden = true) int pagesize,
        @RequestParam(value = "name", required = false) @Parameter(description = "商户名称") String name);
	
	
	@Operation(summary = "修改客如云商户", tags = ApiTags.custKryVendor)
    @PostMapping(value = "/vendor/upd")
    public Result<MktKryVendorOnList> updKryVendor(
    		@RequestParam(value = "pkey") @Parameter(description = "主键", required = true) Integer pkey,
    		@RequestParam(value = "uuid", required = false) @Parameter(description = "客如云id") Long uuid,
			@RequestParam(value = "name", required = false) @Parameter(description = "商户名称") String name,
			@RequestParam(value = "mobile", required = false) @Parameter(description = "手机号码") String mobile,
			@RequestParam(value = "manager", required = false) @Parameter(description = "负责人") String manager,
			@RequestParam(value = "token", required = false) @Parameter(description = "token") String token
    		);
	
	@Operation(summary = "删除客如云商户", tags = ApiTags.custKryVendor)
    @PostMapping(value = "/vendor/del")
    public Result<Boolean> delKryVendor(@RequestParam(name = "pkey") Integer pkey);
	
	@Operation(summary = "客如云启用商户", tags = ApiTags.custKryVendor)
    @PostMapping(value = "/vendor/enable/start")
    public Result<Boolean> startKryVendor(@RequestParam(name = "pkey") Integer pkey);
	
	@Operation(summary = "客如云停用商户", tags = ApiTags.custKryVendor)
    @PostMapping(value = "/vendor/enable/stop")
    public Result<Boolean> stopKryVendor(@RequestParam(name = "pkey") Integer pkey);
	
	@Operation(summary = "获取客如云订单列表", tags = ApiTags.custKryVendor)
    @PostMapping(value = "/order/query")
    public Result<PageResult<MktKryOrderOnList>> queryKryOrder(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号", hidden = true) int page,
        @RequestParam(value = "pagesize", defaultValue = "10000") @Parameter(description = "每页大小", hidden = true) int pagesize,
        @RequestParam(value = "name", required = false) @Parameter(description = "名称") String name,
        @RequestParam(value = "startDate", required = false) @Parameter(description = "开始时间") String startDate, 
        @RequestParam(value = "endDate", required = false) @Parameter(description = "结束时间") String endDate);
}
