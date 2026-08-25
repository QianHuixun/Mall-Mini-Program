package cn.tofocus.lejia.api.v1.market;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.EnumNameDTO;
import cn.tofocus.lejia.bean.dto.PkeyNameDTO;
import cn.tofocus.lejia.bean.dto.app.market.MktVendorGoodsDTO;
import cn.tofocus.lejia.bean.dto.app.market.MktVendorGoodsPriceDTO;
import cn.tofocus.lejia.bean.dto.order.MktVendorOrderDTO;
import cn.tofocus.lejia.bean.dto.order.MktVendorOrderMainDTO;
import cn.tofocus.lejia.bean.dto.order.MktVendorOrderParamDTO;
import cn.tofocus.lejia.bean.dto.order.MktVendorParamDTO;
import cn.tofocus.lejia.bean.dto.order.RevokeMainDTO;
import cn.tofocus.lejia.bean.dto.order.SettlementDetailDTO;
import cn.tofocus.lejia.bean.dto.order.SettlementMainDTO;
import cn.tofocus.lejia.bean.dto.order.VendorOrderInfo;
import cn.tofocus.lejia.bean.dto.order.VendorOrderReport;
import cn.tofocus.lejia.bean.enums.DataEnums;
import cn.tofocus.lejia.bean.enums.SettlementType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@FeignClient(value = "lejia-server", contextId = "lejia-server-vendorOrder", path = "/v1/market/vendorOrder", fallbackFactory = MktVendorOrderApiFallback.class, configuration = FeignConfig.class)
public interface MktVendorOrderApi
{
    
    @Operation(summary = "读取订单采购信息", tags = ApiTags.custVendorOrder)
    @PostMapping(value = "/loadOrder")
    public Result<List<MktVendorOrderDTO>> loadOrder(
        @RequestParam(value = "Pkey") @Parameter(description = "订单PKEY") Integer pkey);
    
    @Operation(summary = "读取采购商户信息", tags = ApiTags.custVendorOrder)
    @PostMapping(value = "/loadVendor")
    public Result<List<MktVendorGoodsDTO>> loadVendor(
        @RequestParam(value = "Pkey") @Parameter(description = "商品pkey") Integer pkey);
    
    @Operation(summary = "读取采购商户信息", tags = ApiTags.custVendorOrder)
    @PostMapping(value = "/loadVendorV2")
    public Result<List<MktVendorGoodsPriceDTO>> loadVendorV2(
        @RequestParam(value = "Pkey") @Parameter(description = "商品pkey") Integer pkey);
    
    @Operation(summary = "采购", tags = ApiTags.custVendorOrder)
    @PostMapping(value = "/checkOrder")
    public Result<Boolean> checkOrder(@RequestBody MktVendorOrderMainDTO info);
    
    @Operation(summary = "重新采购", tags = ApiTags.custVendorOrder)
    @PostMapping(value = "/purchase/again")
    public Result<Boolean> againPurchase(@RequestBody MktVendorOrderDTO info);
    
    @Operation(summary = "采购确认", tags = ApiTags.custVendorOrder)
    @PostMapping(value = "/purchase/confirm")
    public Result<Boolean> confirmPurchase(@RequestParam(value = "pkeys") List<Integer> pkeys);
    
    @Operation(summary = "采购确认(测试)", tags = ApiTags.custVendorOrder)
    @PostMapping(value = "/purchase/confirm/run")
    public Result<Boolean> confirmPurchaseRun();
    
    
    @Operation(summary = "获取采购详情", tags = ApiTags.custVendorOrder)
    @PostMapping(value = "/purchase/list")
    public Result<VendorOrderInfo> clistPurchase(
        @RequestParam(value = "pkey") @Parameter(description = "订单PKEY") Integer pkey);
    
    @Operation(summary = "商户对账分页数据", tags = ApiTags.custVendorOrder)
    @PostMapping(value = "/query")
    Result<MktVendorOrderMainDTO> queryOrder(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", defaultValue = "10") @Parameter(description = "每页大小") int pagesize,
        @RequestParam(value = "vendor", required = false) @Parameter(description = "商户pkey列表") List<Integer> vendor,
        @RequestParam(value = "startDate", required = false) @Parameter(description = "订单时间-开始") String startDate,
        @RequestParam(value = "endDate", required = false) @Parameter(description = "订单时间-结束") String endDate,
        @RequestParam(value = "status", required = false) @Parameter(description = "结算状态") List<SettlementType> status);
    
    @Operation(summary = "市场端-商户列表", tags = ApiTags.custVendorOrder)
    @PostMapping(value = "/vendorList")
    Result<List<PkeyNameDTO>> vendorList();
    
    @Operation(summary = "市场端-结算状态枚举列表", tags = ApiTags.custVendorOrder)
    @PostMapping(value = "/statusList")
    Result<List<EnumNameDTO>> statusList();
    
    @Operation(summary = "市场端-采购方式", tags = ApiTags.custVendorOrder)
    @PostMapping(value = "/settlementMethod")
    Result<EnumNameDTO> settlementMethod(
        @RequestParam(value = "pkey", required = false) @Parameter(description = "市场主键") String pkey);
    
    @Operation(summary = "导出商户对账列表", tags = ApiTags.custVendorOrder)
    @PostMapping(value = "/export")
    void export(@RequestParam(value = "pkeys", required = false) @Parameter(description = "选中的数据主键") List<Integer> pkeys,
        @RequestParam(value = "vendor", required = false) @Parameter(description = "商户pkey列表") List<Integer> vendor,
        @RequestParam(value = "startDate", required = false) @Parameter(description = "订单时间-开始") String startDate,
        @RequestParam(value = "endDate", required = false) @Parameter(description = "订单时间-结束") String endDate,
        @RequestParam(value = "status", required = false) @Parameter(description = "结算状态") List<SettlementType> status,
        HttpServletResponse response);
    
    @Operation(summary = "商户结算分页数据", tags = ApiTags.custVendorOrder)
    @PostMapping(value = "/settlementList")
    Result<SettlementMainDTO> settlementList(@ModelAttribute MktVendorOrderParamDTO param);
    
    @Operation(summary = "选中的商户结算详情", tags = ApiTags.custVendorOrder)
    @PostMapping(value = "/settlementDetail")
    Result<SettlementDetailDTO> settlementDetail(
        @RequestParam(value = "pkeys") @Parameter(description = "选中的数据主键", required = true) List<Integer> pkeys);
    
    @Operation(summary = "商户结算", tags = ApiTags.custVendorOrder)
    @PostMapping(value = "/settlement")
    Result<Boolean> settlement(
        @RequestParam(value = "pkeys") @Parameter(description = "选中的数据主键", required = true) List<Integer> pkeys,
        @RequestParam(value = "settlementRemark", required = false) @Parameter(description = "结算备注") String settlementRemark);
    
    @Operation(summary = "导出商户结算列表", tags = ApiTags.custVendorOrder)
    @PostMapping(value = "/settlement/export")
    void settlementExport(@ModelAttribute MktVendorOrderParamDTO param, HttpServletResponse response);
    
    @Operation(summary = "撤销记录分页数据", tags = ApiTags.custVendorOrder)
    @PostMapping(value = "/revokeList")
    Result<RevokeMainDTO> revokeList(@ModelAttribute MktVendorParamDTO param);
    
    @Operation(summary = "导出撤销记录数据", tags = ApiTags.custVendorOrder)
    @PostMapping(value = "/revoke/export")
    void revokeExport(@ModelAttribute MktVendorParamDTO param, HttpServletResponse response);
    
    @Operation(summary = "商户采购报表", tags = ApiTags.custVendorOrder)
    @PostMapping(value = "/purchase/report")
    public Result<VendorOrderReport> purchaseReport(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", defaultValue = "10") @Parameter(description = "每页大小") int pagesize,
        @RequestParam(value = "dataEnums", required = false, defaultValue = "DAY") @Parameter(description = "时间类型") DataEnums dataEnums,
        @RequestParam(value = "startDate", required = false) @Parameter(description = "开始时间") String startDate,
        @RequestParam(value = "endDate", required = false) @Parameter(description = "结束时间") String endDate,
        @RequestParam(value = "vendorKeys", required = false) @Parameter(description = "商户主键") List<Integer> vendorKeys,
        @RequestParam(value = "status", required = false, defaultValue = "NOT_START") SettlementType status);
    
}
