package cn.tofocus.lejia.app.v1.vendor;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.app.AppTags;
import cn.tofocus.lejia.bean.dto.app.AppCardCheckDTO;
import cn.tofocus.lejia.bean.dto.app.AppUsePointsRecordOnList;
import cn.tofocus.lejia.bean.dto.app.AppVendorDTO;
import cn.tofocus.lejia.bean.dto.app.vendor.VendorOrderInfoV2;
import cn.tofocus.lejia.bean.dto.market.MktGiftOnPage;
import cn.tofocus.lejia.bean.dto.order.MktVendorOrderMainDTO;
import cn.tofocus.lejia.bean.enums.CardStatus;
import cn.tofocus.lejia.bean.enums.SettlementType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

/**
 * 移动端-商户接口V1
 */
@FeignClient(value = "lejia-server", contextId = "lejia-server-app-vendor", path = "/v1/app/vendor", fallbackFactory = AppVendorApiFallback.class, configuration = FeignConfig.class)
public interface AppVendorApi
{
    
    @Operation(summary = "商户信息", tags = AppTags.mobileVendor)
    @PostMapping("/get")
    public Result<AppVendorDTO> getVendor();
    
    @Operation(summary = "客户积分消费记录", tags = AppTags.mobileVendor)
    @PostMapping("/queryUsePointsRecord")
    public Result<PageResult<AppUsePointsRecordOnList>> queryUsePointsRecord(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", defaultValue = "100") @Parameter(description = "每页大小") int pagesize);
    
    @Operation(summary = "获取采购订单信息列表", tags = AppTags.mobileVendor)
    @PostMapping(value = "/query")
    public Result<MktVendorOrderMainDTO> queryOrder(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号", hidden = false) int page,
        @RequestParam(value = "pagesize", defaultValue = "10000") @Parameter(description = "每页大小", hidden = false) int pagesize,
        @RequestParam(value = "startDate", required = false) @Parameter(description = "订单时间-开始") String startDate,
        @RequestParam(value = "endDate", required = false) @Parameter(description = "订单时间-结束") String endDate);
    
    @Operation(summary = "获取采购订单信息列表-V2", tags = AppTags.mobileVendor)
    @PostMapping(value = "/orderstatus/query")
    public Result<VendorOrderInfoV2> queryOrder(
        @RequestParam(value = "page", defaultValue = "0", required = false) @Parameter(description = "页号", hidden = false) int page,
        @RequestParam(value = "pagesize", defaultValue = "10", required = false) @Parameter(description = "每页大小", hidden = false) int pagesize,
        @RequestParam(value = "status", required = false) SettlementType status,
        @RequestParam(value = "startDate", required = false) String startDate,
        @RequestParam(value = "endDate", required = false) String endDate,
        @RequestParam(value = "flag", required = false, defaultValue = "true") @Parameter(description = "true: 待采购, false:已采购") Boolean flag);
    
    @Operation(summary = "完成采购", tags = AppTags.mobileVendor)
    @PostMapping(value = "/purchase/finish")
    public Result<Boolean> finishPurchase(@RequestParam(value = "pkey") @Parameter(description = "采购订单主键") Integer pkey);
    
    @Operation(summary = "扫码获取卡券名称", tags = AppTags.mobileVendor)
    @PostMapping("/get/card")
    public Result<String> getCardName(@RequestParam(name = "cardNumber") @Parameter(description = "核销码") String cardNumber);
    
    @Operation(summary = "核销卡券", tags = AppTags.mobileVendor)
    @PostMapping("/ins/card")
    public Result<Boolean> insCard(@RequestParam(name = "cardNumber") @Parameter(description = "核销码") String cardNumber);
    
    @Operation(summary = "获取核销卡券记录", tags = AppTags.mobileVendor)
    @PostMapping("/query/card")
    public Result<PageResult<AppCardCheckDTO>> queryCard(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", defaultValue = "10") @Parameter(description = "每页大小") int pagesize);
    
    @Operation(summary = "核销礼品券", tags = AppTags.mobileVendor)
    @PostMapping("/gift/writeOff")
    public Result<Boolean> writeOffGift(@RequestParam(name = "cardNumber") @Parameter(description = "核销码") String cardNumber);
    
    @Operation(summary = "获取核销礼品券记录", tags = AppTags.mobileVendor)
    @PostMapping(value = "/gift/query")
    public Result<PageResult<MktGiftOnPage>> giftList(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", defaultValue = "10") @Parameter(description = "每页大小") int pagesize,
        @RequestParam(value = "startDate", required = false) String startDate,
        @RequestParam(value = "endDate", required = false) String endDate,
        @RequestParam(value = "status", required = false, defaultValue = "UNUSED") CardStatus status);
    
    @Operation(summary = "获取核销礼品券合计", tags = AppTags.mobileVendor)
    @PostMapping(value = "/gift/sumAmtn")
    public Result<BigDecimal> giftSumAmtn(@RequestParam(value = "startDate", required = false) String startDate,
        @RequestParam(value = "endDate", required = false) String endDate);
    
    @Operation(summary = "扫码获取礼品券名称", tags = AppTags.mobileVendor)
    @PostMapping("/gift/loadName")
    public Result<Map<String, String>> getGiftName(
        @RequestParam(name = "cardNumber") @Parameter(description = "核销码") String cardNumber);
}
