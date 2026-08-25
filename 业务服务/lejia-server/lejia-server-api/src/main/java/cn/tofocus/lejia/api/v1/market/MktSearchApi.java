package cn.tofocus.lejia.api.v1.market;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.market.MktSearchKeywordInfo;
import cn.tofocus.lejia.bean.enums.SearchKeywordModule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

public interface MktSearchApi
{
    @Operation(summary = "查询搜索词", tags = ApiTags.SEARCH)
    @PostMapping(value = "/keyword/query")
    Result<PageResult<MktSearchKeywordInfo>> query(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号", hidden = true) int page,
        @RequestParam(value = "pagesize", defaultValue = "100") @Parameter(description = "每页大小", hidden = true) int pagesize,
        @RequestParam(value = "module", required = false) @Parameter(description = "模块") SearchKeywordModule module,
        @RequestParam(value = "keyword", required = false) @Parameter(description = "关键词") String keyword);
    
    @Operation(summary = "获取搜索词", tags = ApiTags.SEARCH)
    @PostMapping(value = "/keyword/get")
    Result<MktSearchKeywordInfo> get(@RequestParam(value = "pkey") @Parameter(description = "主键") Integer pkey);
    
    @Operation(summary = "新增搜索词", tags = ApiTags.SEARCH)
    @PostMapping(value = "/keyword/add")
    Result<Boolean> add(@RequestBody @Valid MktSearchKeywordInfo info);
    
    @Operation(summary = "编辑搜索词", tags = ApiTags.SEARCH)
    @PostMapping(value = "/keyword/upd")
    Result<Boolean> upd(@RequestBody @Valid MktSearchKeywordInfo info);
    
    @Operation(summary = "删除搜索词", tags = ApiTags.SEARCH)
    @PostMapping(value = "/keyword/del")
    Result<Boolean> del(@RequestParam(value = "pkey") @Parameter(description = "主键") Integer pkey);
}
