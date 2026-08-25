package cn.tofocus.lejia.app.v1.market;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.app.AppTags;
import cn.tofocus.lejia.bean.dto.app.market.AppGoodsCollageDTO;
import cn.tofocus.lejia.bean.dto.app.market.MktAppAddrDTO;
import cn.tofocus.lejia.bean.dto.app.market.MktAppCardDTO;
import cn.tofocus.lejia.bean.dto.app.market.MktAppOrderCutDTO;
import cn.tofocus.lejia.bean.dto.app.market.MktAppOrderDTO;
import cn.tofocus.lejia.bean.dto.app.market.MktAppRefundDTO;
import cn.tofocus.lejia.bean.dto.market.DistributionTypeDTO;
import cn.tofocus.lejia.bean.dto.market.DistributionTypeTimeDTO;
import cn.tofocus.lejia.bean.dto.market.MktGiftOnList;
import cn.tofocus.lejia.bean.enums.DistributionType;
import cn.tofocus.lejia.bean.enums.OrderGroupStatus;
import cn.tofocus.lejia.bean.enums.OrderStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@FeignClient(value = "lejia-server", contextId = "lejia-server-app-order", path = "/v1/app/market/lm/order", fallbackFactory = AppOrderApiFallback.class, configuration = FeignConfig.class)
public interface AppOrderApi
{
    
//    @Operation(summary = "商品直接购买", tags = AppTags.mobileOrder)
//    @PostMapping("/buyGoods")
//    public Result<MktAppOrderDTO> bugGoods(@RequestParam(value = "goods") @Parameter(description = "商品ID") Integer goods,
//        @RequestParam(value = "num") @Parameter(description = "数量") Integer num,
//        @RequestParam(value = "tjr", required = false) @Parameter(description = "推荐人") String tjr);
//    
//    @Operation(summary = "购物车结算", tags = AppTags.mobileOrder)
//    @PostMapping("/buyGwc")
//    public Result<MktAppOrderDTO> buyGwc(@RequestParam(value = "gwcs") @Parameter(description = "购物车ID清单") List<String> gwcs);
    
    @Operation(summary = "获取可用地址", tags = AppTags.mobileOrder)
    @PostMapping(value = "/listAddr")
    public Result<List<MktAppAddrDTO>> listAddr(@RequestParam(value = "distributionType", required = false, defaultValue = "IMMEDIATELY") DistributionType distributionType);
    
    @Operation(summary = "获取可用卡券", tags = AppTags.mobileOrder)
    @PostMapping(value = "/listCard")
    public Result<List<MktAppCardDTO>> listCard(@RequestBody MktAppOrderDTO order);
    
//    @Operation(summary = "提交订单", tags = AppTags.mobileOrder)
//    @PostMapping(value = "/commitOrder")
//    public Result<MktAppOrderDTO> commitOrder(@RequestBody MktAppOrderDTO order);
    
    @Operation(summary = "确认到货", tags = AppTags.mobileOrder)
    @PostMapping(value = "/drOrder")
    public Result<Boolean> drOrder(@RequestParam(value = "pkey") @Parameter(description = "订单PKEY") Integer pkey);

    @Operation(summary = "读取订单信息", tags = AppTags.mobileOrder)
    @PostMapping(value = "/loadOrder")
    public Result<MktAppOrderDTO> loadOrder(@RequestParam(value = "pkey") @Parameter(description = "订单PKEY") Integer pkey);
    
    @Operation(summary = "读取订单列表", tags = AppTags.mobileOrder)
    @PostMapping(value = "/listOrder")
    public Result<PageResult<MktAppOrderDTO>> listOrder(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号", hidden = true) int page,
        @RequestParam(value = "pagesize", defaultValue = "1000") @Parameter(description = "每页大小", hidden = true) int pagesize,
        @RequestParam(value = "status", required = false) @Parameter(description = "订单状态") OrderStatus status);
    
    @Operation(summary = "退货申请", tags = AppTags.mobileOrder)
    @PostMapping(value = "/rufund")
    public Result<Boolean> refund(@RequestBody MktAppRefundDTO refundDto);
    
    @Operation(summary = "获取团购订单信息", tags = AppTags.mobileOrder)
    @PostMapping(value = "/collage")
    public Result<AppGoodsCollageDTO> getOrderCollage(@RequestParam(value = "orderPkey") int orderPkey);
    
    @Operation(summary = "获取团购订单信息", tags = AppTags.mobileOrder)
    @PostMapping(value = "/list/collage")
    public Result<PageResult<AppGoodsCollageDTO>> listOrderCollage(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", defaultValue = "10") @Parameter(description = "每页大小") int pagesize,
        @RequestParam(value = "status") @Parameter(description = "是否成团") OrderGroupStatus status);
    
    @Operation(summary = "发起砍价", tags = AppTags.mobileOrder)
    @PostMapping("/initiate/cut")
    public Result<MktAppOrderCutDTO> initiateCut(@RequestParam(value = "goods") @Parameter(description = "商品ID") Integer goods,
        @RequestParam(value = "num") @Parameter(description = "数量") Integer num,
        @RequestParam(value = "addressPkey", required = false) @Parameter(description = "购物车Pkey")Integer addressPkey);
    
    @Operation(summary = "获取砍价订单信息", tags = AppTags.mobileOrder)
    @PostMapping(value = "/query/cut")
    public Result<PageResult<MktAppOrderDTO>> getOrderCutList(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号", hidden = true) int page,
        @RequestParam(value = "pagesize", defaultValue = "1000") @Parameter(description = "每页大小", hidden = true) int pagesize);
    
    @Operation(summary = "读取砍价订单详情", tags = AppTags.mobileOrder)
    @PostMapping(value = "/loadCutOrder")
    public Result<MktAppOrderCutDTO> loadCutOrder(@RequestParam(value = "pkey") @Parameter(description = "订单PKEY") Integer pkey);
    
    @Operation(summary = "帮忙砍价", tags = AppTags.mobileOrder)
    @PostMapping(value = "/cut")
    public Result<BigDecimal> cutOrder(@RequestParam(value = "orderPkey") int orderPkey);
    
    @Operation(summary = "移动端不显示", tags = AppTags.mobileOrder)
    @PostMapping(value = "/isshow")
    public Result<Boolean> isshow(@RequestParam(value = "orderPkey") int orderPkey);
    
    @Operation(summary = "获取礼品券列表", tags = AppTags.mobileOrder)
    @PostMapping(value = "/queryByOrder")
    public Result<List<MktGiftOnList>> queryByOrder(
        @RequestParam(value = "orderPkey") @Parameter(description = "订单号") Integer orderPkey);
    
    @Operation(summary = "配送类型", tags = AppTags.mobileOrder)
    @PostMapping(value = "/get/distributionType")
    public Result<DistributionTypeDTO> getDistributionType(
        @RequestParam(value = "marketPkey") @Parameter(description = "市场主键") String marketPkey,
        @RequestParam(value = "type") @Parameter(description = "配送类型") DistributionType type,
        @RequestParam(value = "addressPkey", required = false) @Parameter(description = "购物车Pkey")Integer addressPkey);
    
    @Operation(summary = "获取配送配置", tags = AppTags.mobileOrder)
    @PostMapping(value = "/get/distributionType/psTime")
    public Result<DistributionTypeTimeDTO> getDistributionTypePsTime(
        @RequestParam(value = "marketPkey") @Parameter(description = "市场主键") String marketPkey,
        @RequestParam(value = "type") @Parameter(description = "配送类型") DistributionType type,
        @RequestParam(value = "addressPkey", required = false) @Parameter(description = "购物车Pkey")Integer addressPkey);
    
    @Operation(summary = "获取积分商城自提时间", tags = AppTags.mobileOrder)
    @PostMapping(value = "/get/distributionType/supplier/psTime")
    public Result<DistributionTypeTimeDTO> getSupplierPsTime(
        @RequestParam(value = "supplier") @Parameter(description = "供应商主键")Integer supplier);
    
}
