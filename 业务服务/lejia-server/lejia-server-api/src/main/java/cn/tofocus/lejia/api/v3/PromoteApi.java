package cn.tofocus.lejia.api.v3;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.v3.PromoteOnPage;
import cn.tofocus.lejia.bean.dto.v3.PromoteUpdDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

public interface PromoteApi
{
    @Operation(summary = "新增推广", tags = ApiTags.LEJIA_V3_PROMOTE)
    @PostMapping(value = "/ins")
    public Result<Integer> ins(@RequestBody PromoteUpdDto dto);
    
    @Operation(summary = "编辑推广", tags = ApiTags.LEJIA_V3_PROMOTE)
    @PostMapping(value = "/upd")
    public Result<Boolean> upd(@RequestBody PromoteUpdDto dto);
    
    @Operation(summary = "删除推广", tags = ApiTags.LEJIA_V3_PROMOTE)
    @PostMapping(value = "/del")
    public Result<Boolean> del(@RequestParam(value = "pkey")Integer pkey);
    
    @Operation(summary = "开启状态", tags = ApiTags.LEJIA_V3_PROMOTE)
    @PostMapping(value = "/enabled/start")
    public Result<Boolean> enabledStart(@RequestParam(value = "pkey")Integer pkey);
    
    @Operation(summary = "关闭状态", tags = ApiTags.LEJIA_V3_PROMOTE)
    @PostMapping(value = "/enabled/stop")
    public Result<Boolean> enabledStop(@RequestParam(value = "pkey")Integer pkey);
    
    @Operation(summary = "获取推广列表", tags = ApiTags.LEJIA_V3_PROMOTE)
    @PostMapping(value = "/query")
    public Result<PageResult<PromoteOnPage>> query(@RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", defaultValue = "10") @Parameter(description = "每页大小") int pagesize,
        @RequestParam(value = "title", required = false) @Parameter(description = "标题")String title,
        @RequestParam(value = "content", required = false) @Parameter(description = "内容")String content);
    
}
