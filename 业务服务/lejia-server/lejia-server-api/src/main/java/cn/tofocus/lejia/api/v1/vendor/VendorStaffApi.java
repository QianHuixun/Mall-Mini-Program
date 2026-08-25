package cn.tofocus.lejia.api.v1.vendor;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.vendor.VendorStaffOnPage;
import cn.tofocus.lejia.bean.dto.vendor.VendorStaffUpdInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

public interface VendorStaffApi
{
    @Operation(summary = "获取店员列表", tags = ApiTags.vendorStaff)
    @PostMapping(value = "/query")
    public Result<PageResult<VendorStaffOnPage>> queryVendorStaff(
        @RequestParam(value = "page", required = false, defaultValue = "0") @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", required = false, defaultValue = "10") @Parameter(description = "每页大小") int pagesize,
        @RequestParam(value = "vendor", required = false) @Parameter(description = "商户主键") Integer vendor,
        @RequestParam(value = "content", required = false) @Parameter(description = "查询内容") String content);
    
    @Operation(summary = "新增店员", tags = ApiTags.vendorStaff)
    @PostMapping(value = "/add")
    public Result<Integer> addVendorStaff(@RequestBody @Valid VendorStaffUpdInfo info);
    
    @Operation(summary = "编辑店员", tags = ApiTags.vendorStaff)
    @PostMapping(value = "/upd")
    public Result<Boolean> updVendorStaff(@RequestBody @Valid VendorStaffUpdInfo info);
    
    @Operation(summary = "启动/关闭店员", tags = ApiTags.vendorStaff)
    @PostMapping(value = "/enabled")
    public Result<Boolean> enabledVendorStaff(@RequestParam(value = "pkey") @Parameter(description = "店员主键") Integer pkey,
        @RequestParam(value = "enabled") @Parameter(description = "开启关闭") Boolean enabled);
    
    @Operation(summary = "删除店员", tags = ApiTags.vendorStaff)
    @PostMapping(value = "/del")
    public Result<Boolean> deVendorStaff(@RequestParam(value = "pkey") @Parameter(description = "店员主键") Integer pkey);
    
}
