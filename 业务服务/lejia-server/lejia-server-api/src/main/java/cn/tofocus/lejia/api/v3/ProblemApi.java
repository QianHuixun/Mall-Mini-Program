package cn.tofocus.lejia.api.v3;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.v3.ProblemOnInfo;
import cn.tofocus.lejia.bean.dto.v3.ProblemTypeOnInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

public interface ProblemApi
{
    @Operation(summary = "获取常见问题列表", tags = ApiTags.WEB_PROBLEM)
    @PostMapping(value = "/query")
    public Result<PageResult<ProblemOnInfo>> queryProblem(@RequestParam(value = "page", defaultValue = "0", required = false) @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", defaultValue = "10", required = false) @Parameter(description = "每页大小") int pagesize,
        @RequestParam(value = "types", required = false)List<Integer> types,
        @RequestParam(value = "content", required = false)String content);
    
    @Operation(summary = "新增常见问题", tags = ApiTags.WEB_PROBLEM)
    @PostMapping(value = "/ins")
    public Result<Boolean> insProblem(@RequestBody @Validated ProblemOnInfo dto);
    
    @Operation(summary = "编辑常见问题", tags = ApiTags.WEB_PROBLEM)
    @PostMapping(value = "/upd")
    public Result<Boolean> updProblem(@RequestBody @Validated ProblemOnInfo dto);
    
    @Operation(summary = "启停常见问题", tags = ApiTags.WEB_PROBLEM)
    @PostMapping(value = "/enabled")
    public Result<Boolean> enabled(@RequestParam(value = "pkey")Integer pkey, @RequestParam(value = "enabled")Boolean enabled);
    
    @Operation(summary = "删除常见问题", tags = ApiTags.WEB_PROBLEM)
    @PostMapping(value = "/del")
    public Result<Boolean> delProblem(@RequestParam(value = "pkey")Integer pkey);
    
    @Operation(summary = "获取常见问题分类", tags = ApiTags.WEB_PROBLEM)
    @PostMapping(value = "/type/query")
    public Result<PageResult<ProblemTypeOnInfo>> queryProblemType(@RequestParam(value = "page", defaultValue = "0", required = false) @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", defaultValue = "10", required = false) @Parameter(description = "每页大小") int pagesize);
    
    @Operation(summary = "获取常见问题分类-不带分页", tags = ApiTags.WEB_PROBLEM)
    @PostMapping(value = "/type/list")
    public Result<List<ProblemTypeOnInfo>> listProblemType();
    
    @Operation(summary = "新增常见问题分类", tags = ApiTags.WEB_PROBLEM)
    @PostMapping(value = "/type/ins")
    public Result<Boolean> insProblemType(@RequestBody @Validated ProblemTypeOnInfo dto);
    
    @Operation(summary = "编辑常见问题分类", tags = ApiTags.WEB_PROBLEM)
    @PostMapping(value = "/type/upd")
    public Result<Boolean> updProblemType(@RequestBody @Validated ProblemTypeOnInfo dto);
    
    @Operation(summary = "删除常见问题分类", tags = ApiTags.WEB_PROBLEM)
    @PostMapping(value = "/type/del")
    public Result<Boolean> delProblemType(@RequestParam(value = "pkey")Integer pkey);
    
    
}
