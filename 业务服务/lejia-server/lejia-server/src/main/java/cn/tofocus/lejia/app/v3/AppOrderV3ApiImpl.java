package cn.tofocus.lejia.app.v3;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.common.util.StringUtil;
import cn.tofocus.core.Result;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.lejia.app.AppTags;
import cn.tofocus.lejia.bean.dto.app.jd.JdOrderTotalInfo;
import cn.tofocus.lejia.bean.dto.v2.card.MemberCardOrderInfo;
import cn.tofocus.lejia.bean.dto.v2.card.MemberCardV2OnList;
import cn.tofocus.lejia.bean.dto.v3.GwcOrderTotalV3Info2;
import cn.tofocus.lejia.bean.dto.v3.OrderTotalV3Info;
import cn.tofocus.lejia.bean.dto.v3.OrderV3Info;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.domain.jd.JdAppOrderManager;
import cn.tofocus.lejia.domain.v3.AppOrderV3Manager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@RequestMapping("/v3/app/market/lm/order")
@RestController
public class AppOrderV3ApiImpl
{
    @Autowired
    private AppOrderV3Manager manager;
    
//    @Autowired
//    private JdAppOrderManager jdAppOrderManager;
    
//    @Operation(summary = "提交订单", tags = AppTags.mobileOrderV3)
//    @PostMapping(value = "/commitOrder")
//    public Result<JdOrderTotalInfo> commitOrder(@RequestBody JdOrderTotalInfo info)
//    {
//        return new Result<>(jdOrderManager.commitOrder(info));
//    }
    @Operation(summary = "提交订单", tags = AppTags.mobileOrderV3)
    @PostMapping(value = "/commitOrder")
    public Result<OrderTotalV3Info> commitOrder(@RequestBody OrderTotalV3Info info)
    {
        return new Result<>(manager.commitOrder(info));
    }
    
//    @Operation(summary = "商品直接购买", tags = AppTags.mobileOrderV3)
//    @PostMapping("/buyGoods")
//    public Result<JdOrderTotalInfo> bugGoods(
//        @RequestParam(value = "space") @Parameter(description = "规格主键") Integer space,
//        @RequestParam(value = "num") @Parameter(description = "数量") Integer num,
//        //        @RequestParam(value = "tjr", required = false) @Parameter(description = "推荐人") String tjr,
//        @RequestParam(value = "pickupType", required = false, defaultValue = "false") @Parameter(description = "true:自提;  false:配送") Boolean pickupType,
//        @RequestParam(value = "addressPkey", required = false) @Parameter(description = "地址主键") Integer addressPkey,
//        @RequestParam(value = "dineIn", required = false) @Parameter(description = "true:堂食") Boolean dineIn,
//        @RequestParam(name = "association", required = false) @Parameter(description = "加工主键") Integer association,
//        @RequestParam(value = "longitude", required = false) BigDecimal longitude,
//        @RequestParam(value = "latitude", required = false) BigDecimal latitude)
//    {
//        return new Result<>(jdOrderManager.buyGoods(241191l, num, addressPkey));
//    }
    @Operation(summary = "商品直接购买", tags = AppTags.mobileOrderV3)
    @PostMapping("/buyGoods")
    public Result<GwcOrderTotalV3Info2> bugGoods(
        @RequestParam(value = "space") @Parameter(description = "规格主键") Integer space,
        @RequestParam(value = "num") @Parameter(description = "数量") Integer num,
        //        @RequestParam(value = "tjr", required = false) @Parameter(description = "推荐人") String tjr,
        @RequestParam(value = "pickupType", required = false, defaultValue = "false") @Parameter(description = "true:自提;  false:配送") Boolean pickupType,
        @RequestParam(value = "addressPkey", required = false) @Parameter(description = "地址主键") Integer addressPkey,
        @RequestParam(value = "dineIn", required = false) @Parameter(description = "true:堂食") Boolean dineIn,
        @RequestParam(name = "association", required = false) @Parameter(description = "加工主键") Integer association,
        @RequestParam(value = "longitude", required = false) BigDecimal longitude,
        @RequestParam(value = "latitude", required = false) BigDecimal latitude)
    {
        return new Result<>(manager.buyGoods(space, num, pickupType, addressPkey, dineIn, association, longitude, latitude));
    }
    
    @Operation(summary = "购物车结算", tags = AppTags.mobileOrderV3)
    @PostMapping("/buyGwc")
    public Result<GwcOrderTotalV3Info2> buyGwc(
        @RequestParam(value = "gwcs") @Parameter(description = "购物车ID清单") List<Integer> gwcs,
        @RequestParam(value = "pickupType", required = false, defaultValue = "false") @Parameter(description = "true:自提;  false:配送") Boolean pickupType,
        @RequestParam(value = "addressPkey", required = false) @Parameter(description = "地址主键") Integer addressPkey,
        @RequestParam(value = "dineIn", required = false) @Parameter(description = "true:堂食") Boolean dineIn,
        @RequestParam(value = "longitude", required = false) BigDecimal longitude,
        @RequestParam(value = "latitude", required = false) BigDecimal latitude)
    {
        return new Result<>(manager.buyGwcV3(gwcs, pickupType, addressPkey, dineIn, longitude, latitude));
    }
    
//    @Operation(summary = "重新计算顺丰运费", tags = AppTags.mobileOrderV3)
//    @PostMapping("/recalculatePostage")
//    public Result<BigDecimal> recalculatePostage()
//    {
//        return null;
//    }
    
//    @Operation(summary = "下单页面获取可用优惠券列表", tags = AppTags.mobileOrderV3)
//    @PostMapping(value = "/listCard")
//    public Result<List<MemberCardV2OnList>> listCard(@RequestBody OrderV3Info info)
//    {
//        return new Result<>(manager.listCard(info, MobileSession.memberPkey(), null, true));
//    }
    
    @Operation(summary = "下单页面获取可用优惠券列表", tags = AppTags.mobileOrderV3)
    @PostMapping(value = "/listCard")
    public Result<MemberCardOrderInfo> listCard(@RequestBody OrderV3Info info)
    {
        return new Result<>(manager.listCardV2(info, MobileSession.memberPkey(), info.getFarmer(), info.getSelectCards()));
    }
}
