package cn.tofocus.account.api.v4;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.account.api.ApiTags;
import cn.tofocus.account.bean.role.FuncForUpd;
import cn.tofocus.account.bean.role.FuncGroupForUpd;
import cn.tofocus.account.bean.role.FuncGroupInfo;
import cn.tofocus.account.bean.role.FuncInfo;
import cn.tofocus.core.Result;
import cn.tofocus.core.data.StrKeyName;
import cn.tofocus.core.exception.DefaultFallbackFactory;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.core.page.PageResult;
import io.swagger.v3.oas.annotations.Operation;

@FeignClient(value = "account", contextId = "functionV4", path = "/v4", fallbackFactory = DefaultFallbackFactory.class, configuration = FeignConfig.class)
public interface FunctionApiV4
{
    @PostMapping(value = "/func/query")
    @Operation(summary = "查询权限", tags = ApiTags.func)
    Result<PageResult<FuncInfo>> queryFunction(
        @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
        @RequestParam(value = "pagesize", required = false, defaultValue = "10") Integer pagesize,
        @RequestParam(value = "domain") String domain);
    
    @PostMapping(value = "/func/add")
    @Operation(summary = "新增权限", tags = ApiTags.func)
    Result<Boolean> addFunction(@RequestBody FuncInfo info);
    
    @PostMapping(value = "/func/upd")
    @Operation(summary = "修改权限", tags = ApiTags.func)
    Result<Boolean> updFunction(@RequestBody FuncForUpd info);
    
    @PostMapping(value = "/func/del")
    @Operation(summary = "删除权限", tags = ApiTags.func)
    Result<String> delFunction(@RequestParam(value = "pkey") String pkey,
        @RequestParam(value = "force", required = false, defaultValue = "false") boolean force);
    
    @PostMapping(value = "/funcGroup/query")
    @Operation(summary = "查询权限组", tags = ApiTags.func)
    Result<PageResult<FuncGroupInfo>> queryFunctionGroup(
        @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
        @RequestParam(value = "pagesize", required = false, defaultValue = "10") Integer pagesize,
        @RequestParam(value = "domain") String domain);
    
    @PostMapping(value = "/funcGroup/add")
    @Operation(summary = "新增权限组", tags = ApiTags.func)
    Result<Boolean> addFunctionGroup(@RequestBody FuncGroupInfo info);
    
    @PostMapping(value = "/funcGroup/upd")
    @Operation(summary = "修改权限组", tags = ApiTags.func)
    Result<Boolean> updFunctionGroup(@RequestBody FuncGroupForUpd info);
    
    @PostMapping(value = "/funcGroup/del")
    @Operation(summary = "删除权限组", tags = ApiTags.func)
    Result<String> delFunctionGroup(@RequestParam(value = "pkey") String pkey);
    
    @PostMapping(value = "/funcGroup/list")
    @Operation(summary = "查询权限组", tags = ApiTags.func)
    Result<List<StrKeyName>> listFunctionGroup(@RequestParam(value = "domain") String domain, @RequestParam(value = "group") String group);
}
