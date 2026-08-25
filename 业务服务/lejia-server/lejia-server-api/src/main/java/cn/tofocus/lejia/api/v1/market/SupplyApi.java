package cn.tofocus.lejia.api.v1.market;

import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.data.TreeModel;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.market.MktSupplyGoodsInfo;
import cn.tofocus.lejia.bean.dto.market.MktSupplyInfo;
import cn.tofocus.lejia.bean.dto.market.MktSupplyOnList;
import cn.tofocus.lejia.bean.dto.market.MktSupplyParamDTO;
import cn.tofocus.lejia.bean.dto.market.MktSupplySpaceInfo;
import cn.tofocus.lejia.bean.dto.market.MktSupplyVendorInfo;
import cn.tofocus.lejia.bean.dto.market.SupplySendConfDTO;
import cn.tofocus.lejia.bean.enums.MType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@FeignClient(value = "lejia-server", contextId = "lejia-server-supply", path = "/v1/market/supply", fallbackFactory = SupplyApiFallback.class, configuration = FeignConfig.class)
public interface SupplyApi
{
    @Operation(summary = "商品供应库列表查询", tags = ApiTags.marketGoodsSupply)
    @PostMapping(value = "/pageList")
    Result<PageResult<MktSupplyOnList>> pageList(@ModelAttribute MktSupplyParamDTO param);
    
    @Operation(summary = "获取运营端-派单配置", tags = ApiTags.marketGoodsSupply)
    @PostMapping(value = "/getConf")
    Result<SupplySendConfDTO> getConf();
    
    @Operation(summary = "修改运营端-派单配置", tags = ApiTags.marketGoodsSupply)
    @PostMapping(value = "/updConf")
    Result<Boolean> updSendConf(@RequestBody @Valid SupplySendConfDTO upd);
    
    @Operation(summary = "当前市场能否增删改商品供应库", tags = ApiTags.marketGoodsSupply)
    @PostMapping(value = "/isManipulation")
    Result<Boolean> isManipulation();
    
    @Operation(summary = "删除商品供应库采购信息（不支持删除整条商品信息）", tags = ApiTags.marketGoodsSupply)
    @PostMapping(value = "/del")
    Result<Boolean> del(@RequestParam(value = "pkeys") @Parameter(description = "主键列表", required = true) List<Integer> pkeys);
    
    @Operation(summary = "根据商品删除商品供应库信息", tags = ApiTags.marketGoodsSupply)
    @PostMapping(value = "/delByGoods")
    Result<Boolean> delByGoods(
        @RequestParam(value = "goodPkeys") @Parameter(description = "商品主键", required = true) List<Integer> goodPkeys);
    
    @Operation(summary = "导出商品供应库数据", tags = ApiTags.marketGoodsSupply)
    @PostMapping(value = "/export")
    void exportSupply(@ModelAttribute MktSupplyParamDTO param, HttpServletResponse response);
    
    @Operation(summary = "商品供应库——商品树形列表", tags = ApiTags.marketGoodsSupply)
    @PostMapping(value = "/goodsList")
    Result<List<TreeModel<Integer, MktSupplyGoodsInfo>>> goodsList(
        @RequestParam(value = "mType", required = false, defaultValue = "MARKET_GOODS")MType mType,
        @RequestParam(value = "marketPkey", required = false) @Parameter(description = "市场pkey") String marketPkey);
    
    @Operation(summary = "商品供应库——规格列表", tags = ApiTags.marketGoodsSupply)
    @PostMapping(value = "/spaceList")
    Result<List<MktSupplySpaceInfo>> spaceList(
        @RequestParam(value = "goodsPkey") @Parameter(description = "商品pkey", required = true) Integer goodsPkey);
    
    @Operation(summary = "商品供应库——供应商列表", tags = ApiTags.marketGoodsSupply)
    @PostMapping(value = "/vendorList")
    Result<List<MktSupplyVendorInfo>> vendorList(
        @RequestParam(value = "marketPkey", required = false) @Parameter(description = "市场pkey") String marketPkey);
    
    @Operation(summary = "商品供应库——商品明细", tags = ApiTags.marketGoodsSupply)
    @PostMapping(value = "/detail")
    Result<MktSupplyInfo> detail(
        @RequestParam(value = "marketPkey", required = false) @Parameter(description = "市场pkey") String marketPkey,
        @RequestParam(value = "goodsPkey") @Parameter(description = "商品pkey", required = true) Integer goodsPkey);
    
    @Operation(summary = "商品供应库——新增", tags = ApiTags.marketGoodsSupply)
    @PostMapping(value = "/insert")
    Result<Boolean> insert(@RequestBody @Valid MktSupplyInfo mktSupplyInfo);
    
    @Operation(summary = "商品供应库——更新", tags = ApiTags.marketGoodsSupply)
    @PostMapping(value = "/update")
    Result<Boolean> update(@RequestBody @Valid MktSupplyInfo mktSupplyInfo);
    
    @Operation(summary = "商品供应库启用/停用", tags = ApiTags.marketGoodsSupply)
    @PostMapping(value = "/enable/true")
    Result<Boolean> enable(
        @RequestParam(name = "pkey") @Parameter(description = "商品供应库单项数据pkey", required = true) Integer pkey);
    
    @Operation(summary = "商品供应库-运营端是否开启统一配置", tags = ApiTags.marketGoodsSupply)
    @PostMapping(value = "/isGoodSupply")
    Result<Boolean> isGoodSupply();
    
    @Operation(summary = "商品供应库-是否系统自动派单", tags = ApiTags.marketGoodsSupply)
    @PostMapping(value = "/isGoodPurchaseDeploy")
    Result<Boolean> isGoodPurchaseDeploy();
    
}
