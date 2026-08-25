package cn.tofocus.lejia.api.v1.market;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.market.MktMemberPayOnList;
import cn.tofocus.lejia.bean.dto.market.PayDayDTO;
import cn.tofocus.lejia.bean.dto.market.PayLineDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@FeignClient(value = "lejia-server", contextId = "lejia-server-pay-line", path = "/v1/market/pay/line", 
fallbackFactory = MktPayLineApiFallback.class, configuration = FeignConfig.class)
public interface MktPayLineApi 
{
	@Operation(summary = "获取支付流水", tags = ApiTags.custPayLine)
    @PostMapping(value = "/query")
    public Result<PageResult<MktMemberPayOnList>> queryPayLines(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", defaultValue = "20") @Parameter(description = "每页大小") int pagesize,
        @RequestParam(value = "mobile", required = false) @Parameter(description = "手机号码") String mobile,
        @RequestParam(value = "startTime", required = false) @Parameter(description = "开始时间") String startTime,
        @RequestParam(value = "endTime", required = false) @Parameter(description = "结束时间") String endTime);
	
	@Operation(summary = "对账中心-明细", tags = ApiTags.custPayLine)
    @PostMapping(value = "/bill/query")
    public Result<PageResult<PayLineDTO>> queryPayDetail(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", defaultValue = "20") @Parameter(description = "每页大小") int pagesize,
        @RequestParam(value = "mobile", required = false, defaultValue = "true") @Parameter(description = "购物") Boolean buy,
        @RequestParam(value = "recharge", required = false, defaultValue = "true") @Parameter(description = "充值") Boolean recharge,
        @RequestParam(value = "member", required = false, defaultValue = "true") @Parameter(description = "会员办理") Boolean member,
        @RequestParam(value = "withdraw", required = false, defaultValue = "true") @Parameter(description = "提现") Boolean withdraw,
        @RequestParam(value = "startTime", required = true) @Parameter(description = "开始时间") String startTime,
        @RequestParam(value = "endTime", required = true) @Parameter(description = "结束时间") String endTime);
	
	@Operation(summary = "对账中心-明细-总计笔数", tags = ApiTags.custPayLine)
    @PostMapping(value = "/bill/query/count")
    public Result<Map<String,Object>> queryPayDetailNumCount(
        @RequestParam(value = "mobile", required = false, defaultValue = "true") @Parameter(description = "购物") Boolean buy,
        @RequestParam(value = "recharge", required = false, defaultValue = "true") @Parameter(description = "充值") Boolean recharge,
        @RequestParam(value = "member", required = false, defaultValue = "true") @Parameter(description = "会员办理") Boolean member,
        @RequestParam(value = "withdraw", required = false, defaultValue = "true") @Parameter(description = "提现") Boolean withdraw,
        @RequestParam(value = "startTime", required = true) @Parameter(description = "开始时间") String startTime,
        @RequestParam(value = "endTime", required = true) @Parameter(description = "结束时间") String endTime);
	
	@Operation(summary = "对账中心-日汇总", tags = ApiTags.custPayLine)
	@PostMapping(value = "/bill/day/query")
	public Result<List<PayDayDTO>> queryDayPay(
			@RequestParam(value = "startTime", required = true) @Parameter(description = "开始时间") String startTime, 
			@RequestParam(value = "endTime", required = true) @Parameter(description = "结束时间")String endTime, 
			@RequestParam(value = "companyPkey", required = false) @Parameter(description = "公司pkey")String companyPkey, 
			@RequestParam(value = "marketPkey", required = false) @Parameter(description = "市场pkey", required = false)String marketPkey);
	
	@Operation(summary = "对账中心-月汇总", tags = ApiTags.custPayLine)
	@PostMapping(value = "/bill/month/query")
	public Result<List<PayDayDTO>> queryMonthPay(
			@RequestParam(value = "startTime", required = true) @Parameter(description = "开始时间") String startTime, 
			@RequestParam(value = "endTime", required = true) @Parameter(description = "结束时间")String endTime, 
			@RequestParam(value = "companyPkey", required = false) @Parameter(description = "公司pkey")String companyPkey, 
			@RequestParam(value = "marketPkey", required = false) @Parameter(description = "市场pkey", required = false)String marketPkey);
}
