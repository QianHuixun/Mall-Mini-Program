package cn.tofocus.account.api.v2.application;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.account.api.ApiTags;
import cn.tofocus.account.bean.application.ApplicationInfo;
import cn.tofocus.account.bean.application.CloudDomainInfo;
import cn.tofocus.core.Result;
import cn.tofocus.core.exception.DefaultFallbackFactory;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.core.page.PageResult;
import io.swagger.v3.oas.annotations.Operation;

@FeignClient(value = "account", contextId = "app", path = "/v2", fallbackFactory = DefaultFallbackFactory.class, configuration = FeignConfig.class)
public interface ApplicationApi
{
    @PostMapping(value = "/domain/query")
    @Operation(summary = "查询域列表", tags = ApiTags.app)
    Result<PageResult<CloudDomainInfo>> queryDomain(
        @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
        @RequestParam(value = "pagesize", required = false, defaultValue = "10") Integer pagesize,
        @RequestParam(value = "name", required = false) String name);
    
    @PostMapping(value = "/domain/add")
    @Operation(summary = "新增域", tags = ApiTags.app)
    Result<Object> addDomain(@RequestBody CloudDomainInfo info);
    
    @PostMapping(value = "/domain/del")
    @Operation(summary = "删除域", tags = ApiTags.app)
    Result<Object> delDomain(@RequestParam(value = "pkey") String pkey);
    
    @PostMapping(value = "/app/query")
    @Operation(summary = "查询应用列表", tags = ApiTags.app)
    Result<PageResult<ApplicationInfo>> queryApp(
        @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
        @RequestParam(value = "pagesize", required = false, defaultValue = "10") Integer pagesize,
        @RequestParam(value = "name", required = false) String name,
        @RequestParam(value = "domain", required = false) String domain);
    
    @PostMapping(value = "/app/add")
    @Operation(summary = "新增应用", tags = ApiTags.app)
    Result<Object> addApp(@RequestBody ApplicationInfo app);
    
    @PostMapping(value = "/app/del")
    @Operation(summary = "删除应用", tags = ApiTags.app)
    Result<Object> delApp(@RequestParam(value = "pkey") String pkey);
}
