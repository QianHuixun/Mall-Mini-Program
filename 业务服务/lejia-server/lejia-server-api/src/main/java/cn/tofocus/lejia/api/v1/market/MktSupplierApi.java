package cn.tofocus.lejia.api.v1.market;

import java.util.List;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.market.MktSupplierInfo;
import cn.tofocus.lejia.bean.dto.market.MktSupplierOnPage;
import cn.tofocus.lejia.bean.dto.market.MktSupplierOption;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

public interface MktSupplierApi
{
    @Operation(summary = "查询供应商", tags = ApiTags.SUPPLIER_MANAGE)
    @PostMapping("/query")
    Result<PageResult<MktSupplierOnPage>> query(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", defaultValue = "10000") @Parameter(description = "每页大小") int pagesize,
        @RequestParam(value = "name", required = false) @Parameter(description = "名称") String name,
        @RequestParam(value = "mobile", required = false) @Parameter(description = "手机号") String mobile,
        @RequestParam(value = "enabled", required = false) @Parameter(description = "状态") Boolean enabled);
    
    @Operation(summary = "获取供应商", tags = ApiTags.SUPPLIER_MANAGE)
    @PostMapping("/get")
    Result<MktSupplierInfo> get(@RequestParam(value = "pkey") @Parameter(description = "主键") Integer pkey);
    
    @Operation(summary = "新增供应商", tags = ApiTags.SUPPLIER_MANAGE)
    @PostMapping("/ins")
    Result<Boolean> ins(@RequestBody @Valid MktSupplierInfo info);
    
    @Operation(summary = "编辑供应商", tags = ApiTags.SUPPLIER_MANAGE)
    @PostMapping("/upd")
    Result<Boolean> upd(@RequestBody @Valid MktSupplierInfo info);
    
    @Operation(summary = "删除供应商", tags = ApiTags.SUPPLIER_MANAGE)
    @PostMapping("/del")
    Result<Boolean> del(@RequestParam(value = "pkey") @Parameter(description = "主键") Integer pkey);
    
    @Operation(summary = "启停供应商", tags = ApiTags.SUPPLIER_MANAGE)
    @PostMapping("/enable")
    Result<Boolean> enable(@RequestParam(value = "pkey") @Parameter(description = "主键") Integer pkey,
        @RequestParam(value = "enabled") @Parameter(description = "状态") Boolean enabled);
    
    @Operation(summary = "获取供应商下拉列表", tags = ApiTags.SUPPLIER_MANAGE)
    @PostMapping("/options")
    Result<List<MktSupplierOption>> options(
        @RequestParam(value = "keyword", required = false) @Parameter(description = "关键字") String keyword);
}
