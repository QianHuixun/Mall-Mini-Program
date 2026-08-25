package cn.tofocus.lejia.app.v1.jd;

import java.util.List;

import cn.tofocus.lejia.bean.dto.app.jd.JdOrderDeliveryInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.lejia.app.AppTags;
import cn.tofocus.lejia.bean.dto.app.jd.JdOrderTotalInfo;
import cn.tofocus.lejia.domain.jd.JdAppOrderManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@RequestMapping("/v1/app/jd/order")
@RestController
public class AppJdOrderApiImpl
{
    @Autowired
    private JdAppOrderManager manager;
   
    @Operation(summary = "商品直接购买", tags = AppTags.mobileJdOrderV2)
    @PostMapping("/buyGoods")
    public Result<JdOrderTotalInfo> bugGoods(@RequestParam(value = "space") @Parameter(description = "规格主键") Long space,
        @RequestParam(value = "num") @Parameter(description = "数量") Integer num,
        @RequestParam(value = "addressPkey", required = false) @Parameter(description = "地址主键")Integer addressPkey)
    {
        return new Result<>(manager.buyGoods(space, num, addressPkey));
    }
    
    @Operation(summary = "购物车结算", tags = AppTags.mobileJdOrderV2)
    @PostMapping("/buyGwc")
    public Result<JdOrderTotalInfo> buyGwc(@RequestParam(value = "gwcs") @Parameter(description = "购物车ID清单") List<Integer> gwcs,
        @RequestParam(value = "addressPkey", required = false) @Parameter(description = "地址主键")Integer addressPkey)
    {
        return new Result<>(manager.buyGwc(gwcs, addressPkey));
    }
    
    @Operation(summary = "提交订单", tags = AppTags.mobileJdOrderV2)
    @PostMapping(value = "/commitOrder")
    public Result<JdOrderTotalInfo> commitOrder(@RequestBody JdOrderTotalInfo info)
    {
        return new Result<>(manager.commitOrder(info));
    }
    
    @Operation(summary = "查询配送信息", tags = AppTags.mobileJdOrderV2)
    @PostMapping(value = "/deliveryInfo")
    public Result<JdOrderDeliveryInfo> queryDeliveryInfo(
        @RequestParam(value = "pkey") @Parameter(description = "订单PKEY") Integer pkey)
    {
        return new Result<>(manager.queryDeliveryInfo(pkey));
    }
    
}
