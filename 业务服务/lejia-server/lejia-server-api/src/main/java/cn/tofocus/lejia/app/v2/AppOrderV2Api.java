package cn.tofocus.lejia.app.v2;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.lejia.app.AppTags;
import cn.tofocus.lejia.bean.dto.app.supplier.AppSupplierOrderInfo;
import cn.tofocus.lejia.bean.dto.market.DistributionTypeTimeDTO;
import cn.tofocus.lejia.bean.dto.v2.order.OrderDetailsV2Info;
import cn.tofocus.lejia.bean.dto.v2.order.OrderTotalV2Info;
import cn.tofocus.lejia.bean.dto.wanli.WanliCourierOnInfo;
import cn.tofocus.lejia.bean.enums.DistributionType;
import cn.tofocus.lejia.bean.enums.jd.RefundJdType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

public interface AppOrderV2Api
{
    @Operation(summary = "商品直接购买", tags = AppTags.mobileOrderV2)
    @PostMapping("/buyGoods")
    public Result<OrderTotalV2Info> bugGoods(@RequestParam(value = "space") @Parameter(description = "规格主键") Integer space,
        @RequestParam(value = "num") @Parameter(description = "数量") Integer num,
        @RequestParam(value = "tjr", required = false) @Parameter(description = "推荐人") String tjr,
        @RequestParam(value = "pickupType", required = false, defaultValue = "false") @Parameter(description = "true:自提;  false:配送")Boolean pickupType,
        @RequestParam(value = "addressPkey", required = false) @Parameter(description = "地址主键")Integer addressPkey, 
        @RequestParam(value = "dineIn", required = false) @Parameter(description = "true:堂食")Boolean dineIn,
        @RequestParam(name = "association", required = false) @Parameter(description = "加工主键") Integer association,
        @RequestParam(value = "longitude", required = false) BigDecimal longitude,
        @RequestParam(value = "latitude", required = false) BigDecimal latitude);
    
    @Operation(summary = "购物车结算", tags = AppTags.mobileOrderV2)
    @PostMapping("/buyGwc")
    public Result<OrderTotalV2Info> buyGwc(@RequestParam(value = "gwcs") @Parameter(description = "购物车ID清单") List<Integer> gwcs,
        @RequestParam(value = "pickupType", required = false, defaultValue = "false") @Parameter(description = "true:自提;  false:配送")Boolean pickupType,
        @RequestParam(value = "addressPkey", required = false) @Parameter(description = "地址主键")Integer addressPkey,
        @RequestParam(value = "dineIn", required = false) @Parameter(description = "true:堂食")Boolean dineIn,
        @RequestParam(value = "longitude", required = false) BigDecimal longitude,
        @RequestParam(value = "latitude", required = false) BigDecimal latitude);
    
    @Operation(summary = "提交订单", tags = AppTags.mobileOrderV2)
    @PostMapping(value = "/commitOrder")
    public Result<OrderTotalV2Info> commitOrder(@RequestBody OrderTotalV2Info info);
    
    @Operation(summary = "获取未支付订单支付页面", tags = AppTags.mobileOrderV2)
    @PostMapping(value = "/getUnpaidOrder")
    public Result<OrderTotalV2Info> getUnpaidOrder(@RequestParam(value = "pkey") @Parameter(description = "订单主键") Integer pkey,
        @RequestParam(value = "addressPkey", required = false) @Parameter(description = "购物车Pkey")Integer addressPkey,
        @RequestParam(value = "longitude", required = false) BigDecimal longitude,
        @RequestParam(value = "latitude", required = false) BigDecimal latitude);
    
    @Operation(summary = "获取骑手定位信息", tags = AppTags.mobileOrderV2)
    @PostMapping(value = "/get/courier")
    public Result<WanliCourierOnInfo> getCourier(@RequestParam(value = "pkey")Integer pkey);
    
    @Operation(summary = "读取订单信息", tags = AppTags.mobileOrderV2)
    @PostMapping(value = "/loadOrder")
    public Result<OrderDetailsV2Info> loadOrder(@RequestParam(value = "pkey") @Parameter(description = "订单主键") Integer pkey,
        @RequestParam(value = "jdType", required = false) RefundJdType jdType);
    
    @Operation(summary = "获取是否能农贸会员卡支付", tags = AppTags.mobileOrderV2)
    @PostMapping(value = "/check/nm/member/pay")
    public Result<BigDecimal> checkNmMemberPay();

    // 前端用的v1
    @Operation(summary = "获取配送配置", tags = AppTags.mobileOrder)
    @PostMapping(value = "/get/distributionType/psTime")
    public Result<DistributionTypeTimeDTO> getDistributionTypePsTimeV2(
        @RequestParam(value = "marketPkey") @Parameter(description = "市场主键") String marketPkey,
        @RequestParam(value = "type") @Parameter(description = "配送类型") DistributionType type,
        @RequestParam(value = "addressPkey", required = false) @Parameter(description = "购物车Pkey")Integer addressPkey);
   
    //@Operation(summary = "指定购买卡券-临时接口", tags = AppTags.mobileOrder)
    //@PostMapping(value = "/pay/coupon/one")
    //public Result<WxPayData> payCoupon(@RequestParam(value = "card")String card);
//
    //@Operation(summary = "免费领取卡券-临时接口", tags = AppTags.mobileOrder)
    //@PostMapping(value = "/receive/coupon/one")
    //public Result<MktCard> receiveCoupon(@RequestParam(value = "vendor")Integer vendor);
    
    @Operation(summary = "检查活动礼品是否核销-临时接口", tags = AppTags.mobileOrder)
    @PostMapping(value = "/activity/writeOff/check")
    public Result<Boolean> checkWriteOffActivity(@RequestParam(value = "name") String name);
    
    @Operation(summary = "活动礼品核销-临时接口", tags = AppTags.mobileOrder)
    @PostMapping(value = "/activity/writeOff")
    public Result<Boolean> writeOffActivity(@RequestParam(value = "name") String name);
    
    
    @Operation(summary = "获取自提订单信息（扫核销码）", tags = AppTags.mobileOrder)
    @PostMapping("/pickup/verifyCode/scan")
    Result<AppSupplierOrderInfo> getOrderByScanVerifyCode(
        @RequestParam(value = "kcCode") @Parameter(description = "订单号") String kcCode,
        @RequestParam(value = "verifyCode") @Parameter(description = "核销码") String verifyCode);
    
    
    @Operation(summary = "核销自提订单", tags = AppTags.mobileOrder)
    @PostMapping(value = "/pickup/writeOff")
    public Result<Boolean> writeOffPickupOrder(@RequestParam(value = "kcCode") @Parameter(description = "订单号") String kcCode,
        @RequestParam(value = "verifyCode") @Parameter(description = "核销码") String verifyCode);
    
    
    
}
