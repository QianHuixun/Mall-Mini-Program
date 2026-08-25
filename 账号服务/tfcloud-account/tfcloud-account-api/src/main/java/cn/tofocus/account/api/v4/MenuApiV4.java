package cn.tofocus.account.api.v4;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.account.api.ApiTags;
import cn.tofocus.account.bean.AppMenu;
import cn.tofocus.account.bean.MenuConfig;
import cn.tofocus.account.bean.application.MenuForUpd;
import cn.tofocus.account.bean.application.MenuInfo;
import cn.tofocus.core.Result;
import cn.tofocus.core.data.TreeModel;
import cn.tofocus.core.enums.MenuType;
import cn.tofocus.core.exception.DefaultFallbackFactory;
import cn.tofocus.core.feign.FeignConfig;
import io.swagger.v3.oas.annotations.Operation;

@FeignClient(value = "account", contextId = "menuV4", path = "/v4/menu", fallbackFactory = DefaultFallbackFactory.class, configuration = FeignConfig.class)
public interface MenuApiV4
{
    @PostMapping(value = "/query")
    @Operation(summary = "查询菜单", tags = ApiTags.menu)
    Result<List<TreeModel<String, MenuInfo>>> queryMenu(@RequestParam(value = "application") String application);
    
    @PostMapping(value = "/listParent")
    @Operation(summary = "查询菜单", tags = ApiTags.menu)
    Result<List<TreeModel<String, MenuType>>> listParentMenu(@RequestParam(value = "application") String application,
        @RequestParam(value = "type") MenuType type);
    
    @PostMapping(value = "/add")
    @Operation(summary = "新增菜单", tags = ApiTags.menu)
    Result<Boolean> addMenu(@RequestBody MenuInfo info);
    
    @PostMapping(value = "/upd")
    @Operation(summary = "修改菜单", tags = ApiTags.menu)
    Result<Boolean> updMenu(@RequestBody MenuForUpd info);
    
    @PostMapping(value = "/del")
    @Operation(summary = "删除菜单", tags = ApiTags.menu)
    Result<String> delMenu(@RequestParam(value = "pkey") String pkey,
        @RequestParam(value = "force", required = false, defaultValue = "false") boolean force);
    
    @PostMapping(value = "/setMenuEnable")
    @Operation(summary = "启停菜单", tags = ApiTags.menu)
    Result<Boolean> setMenuEnable(@RequestParam(value = "pkey") String pkey,
        @RequestParam(value = "enable") boolean enable);
    
    @Operation(summary = "获取公司菜单配置", tags = {ApiTags.menu})
    @PostMapping(value = "/listMenuConfigByOrg")
    Result<MenuConfig<String>> listMenuConfigByOrg(@RequestParam(value = "orgid") String orgid,
        @RequestParam(value = "model") String model, @RequestParam(value = "application") String application);
    
    @Operation(summary = "设置公司菜单配置", tags = {ApiTags.menu})
    @PostMapping(value = "/setMenuConfigByOrg")
    Result<Boolean> setMenuConfigByOrg(@RequestBody MenuConfig<String> data);
    
    @Operation(summary = "获取市场菜单配置", tags = {ApiTags.menu})
    @PostMapping(value = "/listMenuConfigByDept")
    Result<MenuConfig<String>> listMenuConfigByDept(@RequestParam(value = "deptid") String deptid,
        @RequestParam(value = "model") String model, @RequestParam(value = "application") String application);
    
    @Operation(summary = "设置市场菜单配置", tags = {ApiTags.menu})
    @PostMapping(value = "/setMenuConfigByDept")
    Result<Boolean> setMenuConfigByDept(@RequestBody MenuConfig<String> data);
    
    @GetMapping(value = "/allAppMenu")
    @Operation(summary = "全部菜单", tags = ApiTags.menu)
    Result<List<AppMenu>> allAppMenu(@RequestParam(value = "application") String application);
}
