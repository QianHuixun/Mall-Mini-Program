package cn.tofocus.lejia.api.v1.sys;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import cn.tofocus.core.Result;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.lejia.api.v1.ApiTags;
import io.swagger.v3.oas.annotations.Operation;

@FeignClient(value = "lejia-server", contextId = "lejia-server-index-data", path = "/v1/sys/index/data/center", 
fallbackFactory = LejiaIndexDataCenterFallback.class, configuration = FeignConfig.class)
public interface LejiaIndexDataCenterApi 
{

	@Operation(summary = "第一行  昨天和今天的数据", tags = ApiTags.custIndexDataCenter)
    @PostMapping(value = "/yesterday/compared")
	public Result<Map<String,Object>> yesterdayTodayCompared();

	@Operation(summary = "第二行 销售柱形图数据", tags = ApiTags.custIndexDataCenter)
    @PostMapping(value = "/sales/status")
	public Result<List<Map<String,Object>>> salesStatus();
	
	@Operation(summary = "第三行 市场销售情况", tags = ApiTags.custIndexDataCenter)
    @PostMapping(value = "/farmer/sales")
	public Result<List<Map<String,Object>>> farmerSales();
	
	@Operation(summary = "第三行 专区销售概况", tags = ApiTags.custIndexDataCenter)
    @PostMapping(value = "/mType/status")
	public Result<List<Map<String,Object>>> mTypeSales();
	
	@Operation(summary = "第四行 商品前十", tags = ApiTags.custIndexDataCenter)
    @PostMapping(value = "/goods/sales")
	public Result<List<Map<String,Object>>> getGoodsSales();
	
	@Operation(summary = "第四行库存预警", tags = ApiTags.custIndexDataCenter)
    @PostMapping(value = "/kc/warning")
	public Result<List<Map<String,Object>>> kcWarning();
	
	@Operation(summary = "获取待发货订单", tags = ApiTags.custIndexDataCenter)
    @PostMapping(value = "/get/deliveredOrder")
    public Result<Integer> getDeliveredOrder();
	
	@Operation(summary = "获取退款订单", tags = ApiTags.custIndexDataCenter)
	@PostMapping(value = "/get/refundOrder")
	public Result<Integer> getRefundOrder();
	
	@PostMapping(value = "/manual/run/task")
	public Result<Boolean> manualRunTask(Integer ascription);
}
