package cn.tofocus.lejia.api.v1.market;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.market.MktCourierOnList;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@FeignClient(value = "lejia-server", contextId = "lejia-server-courier", path = "/v1/market/courier", 
fallbackFactory = LejiaCourierFallback.class, configuration = FeignConfig.class)
public interface LejiaCourierApi 
{
	@Operation(summary = "新增快递员", tags = ApiTags.custCourier)
	@PostMapping("/ins")
	public Result<MktCourierOnList> insCourier(@RequestParam(value = "name") String name,@RequestParam(value = "mobile") String mobile);
	
	@Operation(summary = "获取快递员", tags = ApiTags.custCourier)
	@PostMapping("/get")
	public Result<MktCourierOnList> getCourier(@RequestParam(value = "pkey") @Parameter(description = "快递员主键") Integer pkey);
	
	@Operation(summary = "获取快递员列表", tags = ApiTags.custCourier)
    @PostMapping(value = "/query")
    public Result<PageResult<MktCourierOnList>> queryCourier(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", defaultValue = "10") @Parameter(description = "每页大小") int pagesize,
        @RequestParam(value = "courierName", required = false) @Parameter(description = "快递员名称") String courierName,
        @RequestParam(value = "courierMobile", required = false) @Parameter(description = "快递员电话") String courierMobile,
        @RequestParam(value = "enabled", required = false) @Parameter(description = "是否关闭,传null为全部", hidden = true) Boolean enabled);
	
	@Operation(summary = "修改快递员", tags = ApiTags.custCourier)
    @PostMapping(value = "/upd")
    public Result<MktCourierOnList> updCourier(
    		@RequestParam(name = "pkey") Integer pkey, 
    		@RequestParam(name = "name", required = false) @Parameter(description = "快递员名称") String name, 
    		@RequestParam(name = "mobile", required = false) @Parameter(description = "快递员电话") String mobile,
    		@RequestParam(name = "remark", required = false) @Parameter(description = "备注") String remark);
	
	@Operation(summary = "删除快递员", tags = ApiTags.custCourier)
    @PostMapping(value = "/del")
    public Result<Boolean> delCourier(@RequestParam(name = "pkey") Integer pkey);
	
	@Operation(summary = "快递员启用", tags = ApiTags.custCourier)
    @PostMapping(value = "/enable/start")
    public Result<Boolean> startCourier(@RequestParam(name = "pkey") Integer pkey);
	
	@Operation(summary = "快递员停用", tags = ApiTags.custCourier)
    @PostMapping(value = "/enable/stop")
    public Result<Boolean> stopCourier(@RequestParam(name = "pkey") Integer pkey);
}
