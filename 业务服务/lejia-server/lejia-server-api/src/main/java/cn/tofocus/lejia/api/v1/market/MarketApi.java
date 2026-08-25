package cn.tofocus.lejia.api.v1.market;

import java.util.List;

import cn.tofocus.lejia.bean.dto.market.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import javax.validation.Valid;

@FeignClient(value = "lejia-server", contextId = "lejia-server-market", path = "/v1/sys/market", fallbackFactory = LejiaCustMarketFallback.class, configuration = FeignConfig.class)
public interface MarketApi
{
    /*****************
     * 市场
     ****************/
    
    @Operation(summary = "新增市场", tags = ApiTags.custMarket)
    @PostMapping("/ins")
    public Result<SysFarmerInfo> insMarket(@RequestBody SysFarmerInfo entity);
    
    @Operation(summary = "获取市场", tags = ApiTags.custMarket)
    @PostMapping("/get")
    public Result<SysFarmerInfo> getMarket(
        @RequestParam(value = "pkey", required = false) @Parameter(description = "市场主键") String pkey);
    
    @Operation(summary = "获取市场列表", tags = ApiTags.custMarket)
    @PostMapping(value = "/query")
    public Result<PageResult<SysFarmerOnList>> queryMarket(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", defaultValue = "10") @Parameter(description = "每页大小") int pagesize,
        @RequestParam(value = "marketName", required = false) @Parameter(description = "市场名") String marketName);
    
    @Operation(summary = "修改市场", tags = ApiTags.custMarket)
    @PostMapping(value = "/upd")
    public Result<SysFarmerInfo> updMarket(@RequestBody SysFarmerInfo entity);
    
    @Operation(summary = "删除市场", tags = ApiTags.custMarket)
    @PostMapping(value = "/del")
    public Result<Boolean> delMarket(@RequestParam(name = "pkey") String pkey);
    
    @Operation(summary = "市场启用", tags = ApiTags.custMarket)
    @PostMapping(value = "/enable/start")
    public Result<Boolean> startMarket(@RequestParam(name = "pkey") String pkey);
    
    @Operation(summary = "市场停用", tags = ApiTags.custMarket)
    @PostMapping(value = "/enable/stop")
    public Result<Boolean> stopMarket(@RequestParam(name = "pkey") String pkey);

    @Operation(summary = "骑手派单配置", tags = ApiTags.custMarket)
    @PostMapping(value = "/courier/dispatch/upd")
    public Result<Boolean> updCourierDispatch(@RequestBody List<MktMarketCourierOnList> infos);
    
    @Operation(summary = "获取骑手已派单配置", tags = ApiTags.custMarket)
    @PostMapping(value = "/courier/dispatch/list")
    public Result<List<MktMarketCourierOnList>> listCourierDispatch();
    
    @Operation(summary = "自动派单设置", tags = ApiTags.custMarket)
    @PostMapping(value = "/dispatch/upd")
    public Result<Boolean> updDispatch(
        @RequestParam(value = "automaticCourier", required = false) @Parameter(description = "系统自动派单, true:自动, false:人工") Boolean automaticCourier,
        @RequestParam(value = "automaticPurchase", required = false) @Parameter(description = "系统自动采购, true:自动, false:人工") Boolean automaticPurchase);
    
    @Operation(summary = "获取市场下拉", tags = ApiTags.custMarket)
    @PostMapping(value = "/drop")
    public Result<List<DropStringDown>> listDropName();
    
    @Operation(summary = "获取市场下拉-商品供应库用", tags = ApiTags.custMarket)
    @PostMapping(value = "/drop/supply")
    public Result<List<DropStringDown>> listDropSupplyName();

    @Deprecated
    @Operation(summary = "修改小票打印机设备编码", tags = ApiTags.custMarket)
    @PostMapping(value = "/upd/printCode")
    public Result<Boolean> updPrintCode(@RequestParam(value = "code") String code);

    @Deprecated
    @Operation(summary = "获取小票打印机设备编码", tags = ApiTags.custMarket)
    @PostMapping(value = "/get/printCode")
    public Result<String> getPrintCode();

    @Operation(summary = "获取技术配置", tags = ApiTags.custMarket)
    @PostMapping(value = "/config/tech/get")
    public Result<MarketTechConfig> getTechConfig();

    @Operation(summary = "修改技术配置", tags = ApiTags.custMarket)
    @PostMapping(value = "/config/tech/upd")
    public Result<Boolean> updTechConfig(@RequestBody @Valid MarketTechConfig config);
}
