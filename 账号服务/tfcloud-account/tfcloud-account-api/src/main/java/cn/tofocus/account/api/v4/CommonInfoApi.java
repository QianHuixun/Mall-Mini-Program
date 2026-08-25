package cn.tofocus.account.api.v4;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.account.bean.application.AppKV;
import cn.tofocus.account.bean.application.DeptModelKv;
import cn.tofocus.core.Result;
import cn.tofocus.core.data.LongKeyName;
import cn.tofocus.core.data.StrKeyName;
import cn.tofocus.core.exception.DefaultFallbackFactory;
import cn.tofocus.core.feign.FeignConfig;

@FeignClient(value = "account", contextId = "commonInfo", path = "/v4/common", fallbackFactory = DefaultFallbackFactory.class, configuration = FeignConfig.class)
public interface CommonInfoApi
{
    @GetMapping(value = "/getDomain")
    Result<StrKeyName> getDomain(@RequestParam("key") String key);
    
    @GetMapping(value = "/getDomains")
    Result<List<StrKeyName>> getDomains(@RequestParam("keys") List<String> keys);
    
    @GetMapping(value = "/getApp")
    Result<AppKV> getApp(@RequestParam("key") String key);
    
    @GetMapping(value = "/getApps")
    Result<List<AppKV>> getApps(@RequestParam("keys") List<String> keys);
    
    @GetMapping(value = "/getOrg")
    Result<StrKeyName> getOrg(@RequestParam("key") String key);
    
    @GetMapping(value = "/getOrgs")
    Result<List<StrKeyName>> getOrgs(@RequestParam("keys") List<String> keys);
    
    @GetMapping(value = "/getDept")
    Result<StrKeyName> getDept(@RequestParam("key") String key);
    
    @GetMapping(value = "/getDepts")
    Result<List<StrKeyName>> getDepts(@RequestParam("keys") List<String> keys);
    
    @GetMapping(value = "/getFunc")
    Result<StrKeyName> getFunc(@RequestParam("key") String key);
    
    @GetMapping(value = "/getFuncs")
    Result<List<StrKeyName>> getFuncs(@RequestParam("keys") List<String> keys);
    
    @GetMapping(value = "/getRole")
    Result<StrKeyName> getRole(@RequestParam("key") String key);
    
    @GetMapping(value = "/getRoles")
    Result<List<StrKeyName>> getRoles(@RequestParam("keys") List<String> keys);
    
    @GetMapping(value = "/getUser")
    Result<LongKeyName> getUser(@RequestParam("key") Long key);
    
    @GetMapping(value = "/getUsers")
    Result<List<LongKeyName>> getUsers(@RequestParam("keys") List<Long> keys);
    
    @GetMapping(value = "/getMenu")
    Result<StrKeyName> getMenu(@RequestParam("key") String key);
    
    @GetMapping(value = "/getMenus")
    Result<List<StrKeyName>> getMenus(@RequestParam("keys") List<String> keys);

    @GetMapping(value = "/getDeptModel")
    Result<DeptModelKv> getDeptModel(@RequestParam("key") String key);
    
    @GetMapping(value = "/getDeptModels")
    Result<List<DeptModelKv>> getDeptModels(@RequestParam("keys") List<String> keys);
}
