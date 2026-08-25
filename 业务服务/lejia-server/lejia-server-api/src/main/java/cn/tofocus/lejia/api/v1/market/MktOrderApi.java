package cn.tofocus.lejia.api.v1.market;

import java.util.Date;
import java.util.List;
import java.util.Map;

import cn.tofocus.core.data.KeyValue;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.app.AppTags;
import cn.tofocus.lejia.bean.dto.app.linshi.CardLinshiDto;
import cn.tofocus.lejia.bean.dto.app.market.MktAppOrderDTO;
import cn.tofocus.lejia.bean.dto.app.market.MktAppOrderVendorDTO;
import cn.tofocus.lejia.bean.dto.market.MktOrderGroupOnList;
import cn.tofocus.lejia.bean.dto.market.MktOrderOnList;
import cn.tofocus.lejia.bean.dto.refund.WebOrderRefundOnInfo;
import cn.tofocus.lejia.bean.dto.refund.WebRefundOrderOnInfo;
import cn.tofocus.lejia.bean.entity.market.MktCourier;
import cn.tofocus.lejia.bean.enums.DistributionType;
import cn.tofocus.lejia.bean.enums.ExpressType;
import cn.tofocus.lejia.bean.enums.OrderGroupStatus;
import cn.tofocus.lejia.bean.enums.OrderOir;
import cn.tofocus.lejia.bean.enums.OrderStatus;
import cn.tofocus.lejia.bean.enums.OrderType;
import cn.tofocus.lejia.bean.enums.PayType;
import cn.tofocus.lejia.bean.enums.PurchaseStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@FeignClient(value = "lejia-server", contextId = "lejia-server-order", path = "/v1/market/order", fallbackFactory = MktOrderApiFallback.class, configuration = FeignConfig.class)
public interface MktOrderApi
{
    
    @Operation(summary = "获取订单信息列表", tags = ApiTags.custOrder)
    @PostMapping(value = "/query")
    public Result<PageResult<MktOrderOnList>> queryOrder(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号", hidden = false) int page,
        @RequestParam(value = "pagesize", defaultValue = "10000") @Parameter(description = "每页大小", hidden = false) int pagesize,
        @RequestParam(value = "orderOir", required = true) @Parameter(description = "订单来源") OrderOir orderOir,
        @RequestParam(value = "startDate", required = false) @Parameter(description = "订单时间-开始") String startDate,
        @RequestParam(value = "endDate", required = false) @Parameter(description = "订单时间-结束") String endDate,
        @RequestParam(value = "status", required = false) @Parameter(description = "状态") OrderStatus status,
        @RequestParam(value = "code", required = false) @Parameter(description = "订单号") String code,
        @RequestParam(value = "mobile", required = false) @Parameter(description = "收货手机") String mobile,
        @RequestParam(value = "memberMobile", required = false) @Parameter(description = "用户手机") String memberMobile,
        @RequestParam(value = "orderType", required = false) @Parameter(description = "订单类型") OrderType orderType,
        @RequestParam(value = "purchaseStatus", required = false) @Parameter(description = "采购状态") PurchaseStatus purchaseStatus,
        @RequestParam(value = "groupPkey", required = false) @Parameter(description = "团购订单主键") Integer groupPkey,
        @RequestParam(value = "vrifyCode", required = false) String vrifyCode,
        @RequestParam(value = "priceAbnormal", required = false, defaultValue = "false") @Parameter(description = "价格异常") Boolean priceAbnormal,
        @RequestParam(value = "priceAbnormalFinsh", required = false, defaultValue = "false") @Parameter(description = "价格异常(已确认)") Boolean priceAbnormalFinsh,
        @RequestParam(value = "expressType", required = false)@Parameter(description = "骑手类型") ExpressType expressType,
        @RequestParam(value = "payTypes", required = false) @Parameter(description = "支付方式") List<PayType> payTypes, 
        @RequestParam(value = "tags", required = false) @Parameter(description = "用户标签") List<Integer> tags,
        @RequestParam(value = "distributionType", required = false) @Parameter(description = "配送类型") DistributionType distributionType);
    
    @Operation(summary = "获取订单信息统计金额和笔数", tags = ApiTags.custOrder)
    @PostMapping(value = "/query/sum")
    public Result<Map<String, Object>> queryOrderSum(
        @RequestParam(value = "orderOir", required = true) @Parameter(description = "订单来源") OrderOir orderOir,
        @RequestParam(value = "startDate", required = false) @Parameter(description = "订单时间-开始") String startDate,
        @RequestParam(value = "endDate", required = false) @Parameter(description = "订单时间-结束") String endDate,
        @RequestParam(value = "status", required = false) @Parameter(description = "状态") OrderStatus status,
        @RequestParam(value = "code", required = false) @Parameter(description = "订单号") String code,
        @RequestParam(value = "mobile", required = false) @Parameter(description = "收货手机") String mobile,
        @RequestParam(value = "memberMobile", required = false) @Parameter(description = "用户手机") String memberMobile,
        @RequestParam(value = "orderType", required = false) @Parameter(description = "订单类型") OrderType orderType,
        @RequestParam(value = "groupPkey", required = false) @Parameter(description = "团购订单主键") Integer groupPkey,
        @RequestParam(value = "vrifyCode", required = false) String vrifyCode,
        @RequestParam(value = "priceAbnormal", required = false, defaultValue = "false") @Parameter(description = "价格异常") Boolean priceAbnormal,
        @RequestParam(value = "priceAbnormalFinsh", required = false, defaultValue = "false") @Parameter(description = "价格异常(已确认)") Boolean priceAbnormalFinsh,
        @RequestParam(value = "payTypes", required = false) @Parameter(description = "支付方式") List<PayType> payTypes, 
        @RequestParam(value = "tags", required = false) @Parameter(description = "用户标签") List<Integer> tags,
        @RequestParam(value = "distributionType", required = false) @Parameter(description = "配送类型") DistributionType distributionType);
    
    @Operation(summary = "发货", tags = ApiTags.custOrder)
    @PostMapping(value = "/send")
    public Result<Boolean> sendOrder(@RequestParam(value = "Pkey") @Parameter(description = "订单PKEY") Integer pkey,
        @RequestParam(value = "logistics", required = false) @Parameter(description = "快递公司") String logistics,
        @RequestParam(value = "code", required = false) @Parameter(description = "快递单号") String code);

    @Operation(summary = "批量自提出货", tags = ApiTags.custOrder)
    @PostMapping(value = "/pickup/waitArrival")
    public Result<Boolean> waitArrival(@RequestParam(value = "pkeys")List<Integer> pkeys);

    @Operation(summary = "批量自提到货", tags = ApiTags.custOrder)
    @PostMapping(value = "/pickup/waitWriteoff")
    public Result<Boolean> waitWriteoff(@RequestParam(value = "pkeys")List<Integer> pkeys);
    
    @PostMapping(value = "/send/test")
    public Result<Boolean> sendOrderTest(@RequestParam(value = "pkey") @Parameter(description = "订单PKEY") Integer pkey,
        @RequestParam(value = "logistics", required = false) @Parameter(description = "快递公司") String logistics,
        @RequestParam(value = "code", required = false) @Parameter(description = "快递单号") String code);
    
    @Operation(summary = "派单", tags = ApiTags.custOrder)
    @PostMapping(value = "/paidan")
    public Result<Boolean> paidan(@RequestParam(value = "Pkey") @Parameter(description = "订单PKEY") Integer pkey,
        @RequestParam(value = "courier", required = false) @Parameter(description = "快递员") Integer courier);
    
    @Operation(summary = "跑腿确认送达", tags = ApiTags.custOrder)
    @PostMapping(value = "/upd/arrived")
    public Result<Boolean> arrivedExpress(@RequestParam(value = "pkey") @Parameter(description = "订单PKEY") Integer pkey);
    
    @Operation(summary = "读取跑脚员列表", tags = ApiTags.custOrder)
    @PostMapping(value = "/queryCourier")
    public Result<List<MktCourier>> queryCourier();
    
    @Operation(summary = "读取订单信息", tags = ApiTags.custOrder)
    @PostMapping(value = "/loadOrder")
    public Result<MktAppOrderDTO> loadOrder(@RequestParam(value = "pkey") @Parameter(description = "订单PKEY") Integer pkey);
    
    @Operation(summary = "读取订单退款信息", tags = ApiTags.custOrder)
    @PostMapping(value = "/refund/loadOrder")
    public Result<List<WebOrderRefundOnInfo>> loadRefundOrder(@RequestParam(value = "pkey")@Parameter(description = "订单PKEY") Integer pkey);
    
    @Operation(summary = "web端直接退款", tags = ApiTags.custOrder)
    @PostMapping(value = "/refund/agree")
    public Result<Boolean> agreeRefund(@RequestBody WebRefundOrderOnInfo info);
    
    @Operation(summary = "web端退款原因下拉选", tags = AppTags.mobileRefundV2)
    @PostMapping("/refund/list/reason/drop")
    public Result<List<String>> listReasonDrop(@RequestParam(value = "status")OrderStatus status);
    
    @Operation(summary = "读取订单信息", tags = ApiTags.custOrder)
    @PostMapping(value = "/vendor/loadOrder")
    public Result<MktAppOrderVendorDTO> loadOrderVenodr(@RequestParam(value = "pkey") @Parameter(description = "订单PKEY") Integer pkey);
    
    @Operation(summary = "刷新第三方配送状态", tags = ApiTags.custOrder)
    @PostMapping(value = "/third/party/status/get")
    public Result<Boolean> getThirdPartyStatus(@RequestParam(value = "pkey") @Parameter(description = "订单PKEY") Integer pkey);
    
    @Operation(summary = "获取团购订单列表", tags = ApiTags.custOrder)
    @PostMapping(value = "/group/query")
    public Result<PageResult<MktOrderGroupOnList>> queryOrderGroup(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号", hidden = false) int page,
        @RequestParam(value = "pagesize", defaultValue = "10000") @Parameter(description = "每页大小", hidden = false) int pagesize,
        @RequestParam(value = "goods", required = false) @Parameter(description = "商品") Integer goods,
        @RequestParam(value = "status", required = false) @Parameter(description = "状态") OrderGroupStatus status);
    
    @Operation(summary = "获得30秒内 是否有新订单", tags = ApiTags.custOrder)
    @PostMapping(value = "/newmess")
    public Result<Boolean> newOrder();
    
    @Operation(summary = "获得15秒内 是否有新订单 语音播报", tags = ApiTags.custOrder)
    @PostMapping(value = "/voice")
    public Result<Integer> voiceOrder();
    
    @Operation(summary = "待处理订单数量", tags = ApiTags.custOrder)
    @PostMapping(value = "/pending")
    public Result<Integer> pendingOrder();
    
    @Operation(summary = "自提核销码变更", tags = ApiTags.custOrder)
    @PostMapping(value = "/pickcode/upd")
    public Result<Boolean> updatePickupCodeStatus(@RequestParam(value = "pkey", required = true) @Parameter(description = "订单PKEY") Integer pkey);
    
    @Operation(summary = "打印订单", tags = ApiTags.custOrder)
    @PostMapping(value = "/printOrder")
    public Result<Boolean> printOrder(@RequestParam(value = "pkey") Integer pkey);
    
    @PostMapping(value = "/insMapHuodongLinshi")
    public Result<Boolean> insMapHuodongLinshi(@RequestBody CardLinshiDto info);
    
    @PostMapping(value = "/insMapHuodongLinshi/reurl")
    public Result<Boolean> insMapHuodongLinshiReurl(@RequestParam(value = "code") String code);
    
    @Operation(summary = "顺丰发货", tags = ApiTags.custOrder)
    @PostMapping(value = "/deliver/sf")
    Result<Boolean> deliverSf(@RequestParam(value = "pkey") @Parameter(description = "订单PKEY") Integer pkey,
        @RequestParam(value = "pickupTime") @Parameter(description = "上门取件时间") Date pickupTime,
        @RequestParam(value = "sendContent") @Parameter(description = "寄托物内容") String sendContent);
    
    @Operation(summary = "取消快递单", tags = ApiTags.custOrder)
    @PostMapping(value = "/deliver/cancel")
    Result<Boolean> cancelDelivery(@RequestParam(value = "pkey") @Parameter(description = "物流单PKEY") Long pkey);
    
    @Operation(summary = "获取订单可选自提点", tags = ApiTags.custOrder)
    @PostMapping(value = "/pickupLocation/list")
    Result<List<KeyValue<Integer, String>>> listPickupLocation(
        @RequestParam(value = "pkey") @Parameter(description = "订单pkey") Integer pkey);
    
    @Operation(summary = "修改自提点", tags = ApiTags.custOrder)
    @PostMapping(value = "/pickupLocation/upd")
    Result<Boolean> updPickupLocation(@RequestParam(value = "pkey") @Parameter(description = "订单pkey") Integer pkey,
        @RequestParam(value = "pickupLocation") @Parameter(description = "自提点pkey") Integer pickupLocation);
}
