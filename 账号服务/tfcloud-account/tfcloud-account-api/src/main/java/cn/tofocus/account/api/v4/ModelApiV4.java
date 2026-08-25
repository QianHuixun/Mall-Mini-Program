package cn.tofocus.account.api.v4;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.account.api.ApiTags;
import cn.tofocus.account.bean.ModelConfig;
import cn.tofocus.account.bean.application.ModelInfo;
import cn.tofocus.account.bean.application.ModelforUpd;
import cn.tofocus.core.Result;
import cn.tofocus.core.data.StrKeyName;
import cn.tofocus.core.exception.DefaultFallbackFactory;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.core.page.PageResult;
import io.swagger.v3.oas.annotations.Operation;

@FeignClient(value = "account", contextId = "modelV4", path = "/v4/model", fallbackFactory = DefaultFallbackFactory.class, configuration = FeignConfig.class)
public interface ModelApiV4
{
    @PostMapping(value = "/query")
    @Operation(summary = "查询模块列表", tags = ApiTags.model)
    Result<PageResult<ModelInfo>> queryModel(
        @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
        @RequestParam(value = "pagesize", required = false, defaultValue = "10") Integer pagesize,
        @RequestParam(value = "domain") String domain);
    
    @PostMapping(value = "/add")
    @Operation(summary = "新增模块", tags = ApiTags.model)
    Result<Boolean> addModel(@RequestBody ModelInfo info);
    
    @PostMapping(value = "/upd")
    @Operation(summary = "修改模块", tags = ApiTags.model)
    Result<Boolean> updModel(@RequestBody ModelforUpd info);
    
    @PostMapping(value = "/del")
    @Operation(summary = "删除模块", tags = ApiTags.model)
    Result<String> delModel(@RequestParam(value = "pkey") String pkey,
        @RequestParam(value = "force", required = false, defaultValue = "false") boolean force);
    
    @PostMapping(value = "/listName")
    @Operation(summary = "模块名称下拉", tags = ApiTags.model)
    Result<List<StrKeyName>> listModelName(@RequestParam(value = "domain") String domain);
    
    @Operation(summary = "获取公司模块配置", tags = {ApiTags.model})
    @PostMapping(value = "/listModelConfigByOrg")
    Result<ModelConfig<String>> listModelConfigByOrg(@RequestParam(value = "orgid") String orgid);
    
    @Operation(summary = "设置公司模块配置", tags = {ApiTags.model})
    @PostMapping(value = "/setModelConfigByOrg")
    Result<Boolean> setModelConfigByOrg(@RequestBody ModelConfig<String> data);
    
    @Operation(summary = "获取市场模块配置", tags = {ApiTags.model})
    @PostMapping(value = "/listModelConfigByDept")
    Result<ModelConfig<String>> listModelConfigByDept(@RequestParam(value = "deptid") String deptid);
    
    @Operation(summary = "设置市场模块配置", tags = {ApiTags.model})
    @PostMapping(value = "/setModelConfigByDept")
    Result<Boolean> setModelConfigByDept(@RequestBody ModelConfig<String> data);
}
