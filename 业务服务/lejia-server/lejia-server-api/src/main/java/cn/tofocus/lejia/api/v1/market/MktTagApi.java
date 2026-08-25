package cn.tofocus.lejia.api.v1.market;

import java.util.List;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.market.DropIntegerDown;
import cn.tofocus.lejia.bean.dto.market.MktTagInfo;
import cn.tofocus.lejia.bean.dto.market.MktTagOnPage;
import cn.tofocus.lejia.bean.enums.member.TagType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

public interface MktTagApi
{
    @Operation(summary = "查询标签", tags = ApiTags.TAG_MANAGE)
    @PostMapping("/query")
    Result<PageResult<MktTagOnPage>> query(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", defaultValue = "10000") @Parameter(description = "每页大小") int pagesize,
        @RequestParam(value = "types", required = false) @Parameter(description = "标签类型（多个用英文逗号分隔）") List<TagType> types,
        @RequestParam(value = "name", required = false) @Parameter(description = "名称") String name,
        @RequestParam(value = "description", required = false) @Parameter(description = "描述") String description);
    
    @Operation(summary = "获取标签", tags = ApiTags.TAG_MANAGE)
    @PostMapping("/get")
    Result<MktTagInfo> get(@RequestParam(value = "pkey") @Parameter(description = "主键") Integer pkey);
    
    @Operation(summary = "新增标签", tags = ApiTags.TAG_MANAGE)
    @PostMapping("/ins")
    Result<Boolean> ins(@RequestBody @Valid MktTagInfo info);
    
    @Operation(summary = "编辑标签", tags = ApiTags.TAG_MANAGE)
    @PostMapping("/upd")
    Result<Boolean> upd(@RequestBody @Valid MktTagInfo info);
    
    @Operation(summary = "删除标签", tags = ApiTags.TAG_MANAGE)
    @PostMapping("/del")
    Result<Boolean> del(@RequestParam(value = "pkey") @Parameter(description = "主键") Integer pkey);
    
    @Operation(summary = "标签下拉", tags = ApiTags.TAG_MANAGE)
    @PostMapping("/list/drop")
    Result<List<DropIntegerDown>> listDrop(
        @RequestParam(value = "types", required = false) @Parameter(description = "标签类型（多个用英文逗号分隔）") List<TagType> types);
}
