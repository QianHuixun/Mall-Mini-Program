package cn.tofocus.lejia.api.v1.vendor;

import java.util.List;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.market.DropDTO;
import cn.tofocus.lejia.bean.dto.vendor.VendorBoutiqueInfo;
import cn.tofocus.lejia.bean.dto.vendor.VendorBoutiqueOnPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

public interface VendorBoutiqueApi
{
    @Operation(summary = "获取精选商户列表", tags = ApiTags.vendorBoutique)
    @PostMapping(value = "/query")
    public Result<PageResult<VendorBoutiqueOnPage>> queryVendorBoutique(
        @RequestParam(value = "page", required = false, defaultValue = "0") @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", required = false, defaultValue = "10") @Parameter(description = "每页大小") int pagesize,
        @RequestParam(value = "vendorName", required = false) @Parameter(description = "商户名称") String vendorName,
        @RequestParam(value = "displayName", required = false) @Parameter(description = "商户展示名称") String displayName,
        @RequestParam(value = "enabled", required = false) @Parameter(description = "启用状态") Boolean enabled);
    
    @Operation(summary = "新增精选商户", tags = ApiTags.vendorBoutique)
    @PostMapping(value = "/add")
    public Result<Integer> addVendorBoutique(@RequestBody @Valid VendorBoutiqueInfo info);
    
    @Operation(summary = "编辑精选商户", tags = ApiTags.vendorBoutique)
    @PostMapping(value = "/upd")
    public Result<Boolean> updVendorBoutique(@RequestBody @Valid VendorBoutiqueInfo info);
    
    @Operation(summary = "启动/关闭精选商户", tags = ApiTags.vendorBoutique)
    @PostMapping(value = "/enabled")
    public Result<Boolean> enabledVendorBoutique(@RequestParam(value = "pkey") @Parameter(description = "精选商户主键") Integer pkey,
        @RequestParam(value = "enabled") @Parameter(description = "开启关闭") Boolean enabled);
    
    @Operation(summary = "删除精选商户", tags = ApiTags.vendorBoutique)
    @PostMapping(value = "/del")
    public Result<Boolean> deVendorBoutique(@RequestParam(value = "pkey") @Parameter(description = "精选商户主键") Integer pkey);
    
    @Operation(summary = "展示商品下拉列表", tags = ApiTags.vendorBoutique)
    @PostMapping(value = "/list/drop")
    public Result<List<DropDTO>> listGoodsDrop(@RequestParam(value = "vendor")Integer vendor);
}
