package cn.tofocus.lejia.api.v2;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.app.market.MktAppOrderDTO;
import cn.tofocus.lejia.bean.dto.app.market.MktAppOrderV2Info;
import cn.tofocus.lejia.bean.dto.market.MktOrderOnList;
import cn.tofocus.lejia.bean.enums.OrderStatus;
import cn.tofocus.lejia.bean.enums.OrderType;
import cn.tofocus.lejia.bean.enums.PurchaseStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

public interface OrderV2Api
{
    @Operation(summary = "获取订单信息列表", tags = ApiTags.custOrderV2)
    @PostMapping(value = "/query")
    public Result<PageResult<MktOrderOnList>> queryOrder(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", defaultValue = "10") @Parameter(description = "每页大小") int pagesize,
        @RequestParam(value = "startDate", required = false) @Parameter(description = "订单时间-开始") String startDate,
        @RequestParam(value = "endDate", required = false) @Parameter(description = "订单时间-结束") String endDate,
        @RequestParam(value = "status", required = false) @Parameter(description = "状态") OrderStatus status,
        @RequestParam(value = "code", required = false) @Parameter(description = "订单号") String code,
        @RequestParam(value = "mobile", required = false) @Parameter(description = "用户手机") String mobile,
        @RequestParam(value = "orderType", required = false) @Parameter(description = "订单类型") OrderType orderType,
        @RequestParam(value = "purchaseStatus", required = false) @Parameter(description = "采购状态") PurchaseStatus purchaseStatus,
        @RequestParam(value = "vrifyCode", required = false) @Parameter(description = "自提码") String vrifyCode,
        @RequestParam(value = "priceAbnormal", required = false) @Parameter(description = "价格异常") Boolean priceAbnormal,
        @RequestParam(value = "priceAbnormalFinsh", required = false) @Parameter(description = "价格异常(已确认)") Boolean priceAbnormalFinsh);
    
    @Operation(summary = "获取订单信息统计金额和笔数", tags = ApiTags.custOrderV2)
    @PostMapping(value = "/query/sum")
    public Result<Map<String, Object>> queryOrderSum(
        @RequestParam(value = "startDate", required = false) @Parameter(description = "订单时间-开始") String startDate,
        @RequestParam(value = "endDate", required = false) @Parameter(description = "订单时间-结束") String endDate,
        @RequestParam(value = "status", required = false) @Parameter(description = "状态") OrderStatus status,
        @RequestParam(value = "code", required = false) @Parameter(description = "订单号") String code,
        @RequestParam(value = "mobile", required = false) @Parameter(description = "用户手机") String mobile,
        @RequestParam(value = "orderType", required = false) @Parameter(description = "订单类型") OrderType orderType,
        @RequestParam(value = "purchaseStatus", required = false) @Parameter(description = "采购状态") PurchaseStatus purchaseStatus,
        @RequestParam(value = "vrifyCode", required = false) @Parameter(description = "自提码") String vrifyCode,
        @RequestParam(value = "priceAbnormal", required = false) @Parameter(description = "价格异常") Boolean priceAbnormal,
        @RequestParam(value = "priceAbnormalFinsh", required = false) @Parameter(description = "价格异常(已确认)") Boolean priceAbnormalFinsh);
    
    @Operation(summary = "读取订单信息", tags = ApiTags.custOrder)
    @PostMapping(value = "/loadOrder")
    public Result<MktAppOrderV2Info> loadOrder(@RequestParam(value = "pkey") @Parameter(description = "订单PKEY") Integer pkey);
    
    @Operation(summary = "获取订单信息统计金额和笔数", tags = ApiTags.custOrderV2)
    @PostMapping(value = "/test")
    public Result<String> test(String pkey);
}
