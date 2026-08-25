package cn.tofocus.lejia.app.v1.market;

import java.math.BigDecimal;
import java.util.List;

import cn.tofocus.lejia.bean.dto.app.AppAscriptionConfigDTO;
import cn.tofocus.lejia.bean.dto.app.market.AppIndexZoneConfig;
import cn.tofocus.lejia.bean.dto.app.market.AppIndexZoneGoodsList;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.app.AppTags;
import cn.tofocus.lejia.bean.dto.app.AppCardDTO;
import cn.tofocus.lejia.bean.dto.app.AppConfigDTO;
import cn.tofocus.lejia.bean.dto.app.goods.AppGtypeDTO;
import cn.tofocus.lejia.bean.dto.app.market.AppCheckFarmerRangInfo;
import cn.tofocus.lejia.bean.dto.app.market.SysFarmerAppOnList;
import cn.tofocus.lejia.bean.dto.app.vendor.AppVendorBoutiquerIndexInfo;
import cn.tofocus.lejia.bean.enums.AccountType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@FeignClient(value = "lejia-server", contextId = "lejia-server-app-market", path = "/v1/app/market/index",
        fallbackFactory = AppIndexFallback.class, configuration = FeignConfig.class)
public interface AppIndexApi {
    @Operation(summary = "获取公共配置", tags = AppTags.mobileIndex)
    @PostMapping("/config/ascription/get")
    public Result<AppAscriptionConfigDTO> getAscriptionConfig();

    @Operation(summary = "获取附近市场", tags = AppTags.mobileIndex)
    @PostMapping("/getNearbyMarket")
    public Result<PageResult<SysFarmerAppOnList>> getNearbyMarket(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") Integer page,
        @RequestParam(value = "pagesize", defaultValue = "10") @Parameter(description = "每页大小") Integer pagesize,
        @RequestParam(value = "longitude", required = false) @Parameter(description = "经度") BigDecimal longitude,
        @RequestParam(value = "latitude", required = false) @Parameter(description = "纬度") BigDecimal latitude,
        @RequestParam(value = "area", required = false) @Parameter(description = "区域") String area,
        @RequestParam(value = "name", required = false) @Parameter(description = "菜场名称") String name,
        @RequestParam(value = "version", required = false) @Parameter(description = "版本号") String version,
        @RequestParam(value = "accountType", required = false) @Parameter(description = "公众账号类型") AccountType accountType);

    @Operation(summary = "当前市场", tags = AppTags.mobileIndex)
    @PostMapping("/currentFarmer")
    public Result<SysFarmerAppOnList> currentFarmer(
        @RequestParam(value = "longitude", required = false) @Parameter(description = "经度") BigDecimal longitude,
        @RequestParam(value = "latitude", required = false) @Parameter(description = "纬度") BigDecimal latitude,
        @RequestParam(value = "version", required = false) @Parameter(description = "版本号") String version,
        @RequestParam(value = "accountType", required = false) @Parameter(description = "公众账号类型") AccountType accountType);
    
    @Operation(summary = "当前市场是否在配送范围", tags = AppTags.mobileIndex)
    @PostMapping("/checkFarmerInRange")
    public Result<AppCheckFarmerRangInfo> checkFarmerInRange(
            @RequestParam(value = "longitude", required = true) @Parameter(description = "经度") BigDecimal longitude,
            @RequestParam(value = "latitude", required = true) @Parameter(description = "纬度") BigDecimal latitude,
            @RequestParam(value = "farmer", required = true) @Parameter(description = "市场主键") String farmer,
            @RequestParam(value = "addrBoolean", required = false) Boolean addrBoolean);
    
    @Operation(summary = "获取首页预约配送时间", tags = AppTags.mobileIndex)
    @PostMapping("/get/reservation/time")
    public Result<String> getPsTime();
    
    @Operation(summary = "获取客服联系方式", tags = AppTags.mobileIndex)
    @PostMapping("/get")
    public Result<AppConfigDTO> getAppConfig();

    @Operation(summary = "首页-卡券列表", tags = AppTags.mobileIndex)
    @PostMapping(value = "/queryCard")
    public Result<List<AppCardDTO>> queryCard();

    @Operation(summary = "弹框卡券列表", tags = AppTags.mobileIndex)
    @PostMapping(value = "/queryNewCard")
    public Result<List<AppCardDTO>> queryNewCard();

    @Operation(summary = "领取卡券", tags = AppTags.mobileIndex)
    @PostMapping("/card/insertList")
    Result<Boolean> insCardList(
        @RequestParam(name = "cardPkeys") @Parameter(description = "卡券pkey列表") List<Integer> cardPkeys);

    @Operation(summary = "一键领取卡券是否领取完毕", tags = AppTags.mobileIndex)
    @PostMapping("/card/isFinish")
    Result<Boolean> isFinish();
    
    @Operation(summary = "小程序首页获取精选商户", tags = AppTags.mobileIndex)
    @PostMapping("/query/vendor/boutique")
    Result<PageResult<AppVendorBoutiquerIndexInfo>> queryVendorBoutique(
        @RequestParam(value = "page", defaultValue = "0", required = false) @Parameter(description = "页号") Integer page,
        @RequestParam(value = "pagesize", defaultValue = "10", required = false) @Parameter(description = "每页大小") Integer pagesize);

    @Operation(summary = "获取首页专区名称", tags = AppTags.mobileIndex)
    @PostMapping("/zone/config/get")
    Result<AppIndexZoneConfig> getZoneConfig();

    @Operation(summary = "获取首页专区商品列表", tags = AppTags.mobileIndex)
    @PostMapping("/zone/goods/list")
    Result<AppIndexZoneGoodsList> listZoneGoods();
    
    @Operation(summary = "获取首页分类前十", tags = AppTags.mobileIndex)
    @PostMapping(value = "/gtype/list")
    public Result<List<AppGtypeDTO>> listGtype();
}
