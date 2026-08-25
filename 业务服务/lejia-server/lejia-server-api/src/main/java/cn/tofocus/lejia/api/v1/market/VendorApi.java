package cn.tofocus.lejia.api.v1.market;

import cn.tofocus.core.Result;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.market.*;
import cn.tofocus.lejia.bean.enums.SourceType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "lejia-server", contextId = "lejia-server-vendor", path = "/v1/market/vendor", fallbackFactory = VendorApiFallback.class, configuration = FeignConfig.class)
public interface VendorApi
{
    @Operation(summary = "新增合作商户", tags = ApiTags.custVendor)
    @PostMapping("/ins")
    Result<Integer> insVendor(@RequestBody MktVendorDTO dto);
    
    @Operation(summary = "cust新增或编辑合作商户", tags = ApiTags.custVendor)
    @PostMapping("/cust/put")
    Result<Integer> putVendor(@RequestBody XaszVendorInfo dto);
    
    @Operation(summary = "新增合作商户-积分", tags = ApiTags.custVendor)
    @PostMapping("/ins/point")
    public Result<Integer> insVendorPoint(@RequestBody MktVendorDTO dto);
    
    @Operation(summary = "修改合作商户", tags = ApiTags.custVendor)
    @PostMapping(value = "/upd/point")
    public Result<Boolean> updVendorPoint(@RequestBody MktVendorDTO dto);
    
    @Operation(summary = "获取合作商户", tags = ApiTags.custVendor)
    @PostMapping("/get")
    Result<MktVendorDTO> getVendor(
        @RequestParam(value = "pkey") @Parameter(description = "合作商户主键", required = true) Integer pkey);
    
    @Operation(summary = "获取合作商户列表", tags = ApiTags.custVendor)
    @PostMapping(value = "/query")
    Result<PageResult<MktVendorOnList>> queryVendor(@ModelAttribute MktVendorQueryParamDTO paramDTO);
    
    @Operation(summary = "修改合作商户", tags = ApiTags.custVendor)
    @PostMapping(value = "/upd")
    Result<Boolean> updVendor(@RequestBody MktVendorDTO dto);
    
    @Operation(summary = "删除合作商户", tags = ApiTags.custVendor)
    @PostMapping(value = "/del")
    public Result<Boolean> delVendor(@RequestParam(name = "pkey") Integer pkey);
    
    @Operation(summary = "合作商户启用", tags = ApiTags.custVendor)
    @PostMapping(value = "/enable/start")
    public Result<Boolean> startVendor(@RequestParam(name = "pkey") Integer pkey);
    
    @Operation(summary = "合作商户停用", tags = ApiTags.custVendor)
    @PostMapping(value = "/enable/stop")
    public Result<Boolean> stopVendor(@RequestParam(name = "pkey") Integer pkey);
    
    @Operation(summary = "获取积分明细列表", tags = ApiTags.custVendor)
    @PostMapping("/point/query")
    public Result<PageResult<MktVendorPointLineOnList>> queryVendorPointLine(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号", hidden = false) int page,
        @RequestParam(value = "pagesize", defaultValue = "10000") @Parameter(description = "每页大小", hidden = false) int pagesize,
        @RequestParam(value = "vendor", required = false) @Parameter(description = "商户pkey") Integer vendor,
        @RequestParam(value = "source", required = false) @Parameter(description = "积分来源") SourceType source,
        @RequestParam(value = "mobile", required = false) @Parameter(description = "手机号码") String mobile,
        @RequestParam(value = "name", required = false) @Parameter(description = "名称") String name,
        @RequestParam(value = "startDate", required = false) @Parameter(description = "开始时间") String startDate,
        @RequestParam(value = "endDate", required = false) @Parameter(description = "结束时间") String endDate);
    
    @Operation(summary = "运营端-市场商城-市场列表", tags = ApiTags.custVendor)
    @PostMapping(value = "/marketList")
    Result<List<MarketPkeyNameDTO>> marketList();
    
    @Operation(summary = "经营范围", tags = ApiTags.custVendor)
    @PostMapping(value = "/gtypeList")
    Result<List<MktVendorPkeyNameDTO>> gtypeList(@RequestParam(value = "farmer", required = false)String farmer);
    
    @Operation(summary = "运营端是否开启统一配置", tags = ApiTags.custVendor)
    @PostMapping(value = "/isUnified")
    Result<Boolean> isUnified();
    
    @Operation(summary = "获取商户下拉", tags = ApiTags.custVendor)
    @PostMapping(value = "/drop")
    public Result<List<DropIntegerDown>> listDropName(@RequestParam(value = "farmer", required = false)String farmer,
        @RequestParam(value = "enabled", required = false)Boolean enabled);
  
    @Operation(summary = "获取商户下拉-礼品券新增使用", tags = ApiTags.custVendor)
    @PostMapping(value = "/drop/gift")
    public Result<List<DropIntegerDown>> listDropNameV2(@RequestParam(value = "farmer", required = false)String farmer);
    
    @PostMapping(value = "/get/send/zx/object")
    public Result<?> sendZxObject(@RequestParam(value = "transCode")String transCode,
        @RequestParam(value = "vendorKey", required = false)Integer vendorKey,
        @RequestParam(value = "flag", required = false)Boolean flag);
 
}
