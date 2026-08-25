package cn.tofocus.account.api.v4;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.account.api.ApiTags;
import cn.tofocus.account.bean.role.RoleAclTree;
import cn.tofocus.account.bean.role.RoleMenuTree;
import cn.tofocus.account.bean.user.app.AppRoleForUpd;
import cn.tofocus.account.bean.user.app.AppRoleInfo;
import cn.tofocus.account.bean.user.app.AppRoleInfoOnPage;
import cn.tofocus.core.Result;
import cn.tofocus.core.exception.DefaultFallbackFactory;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.core.page.PageResult;
import io.swagger.v3.oas.annotations.Operation;

@FeignClient(value = "account", contextId = "roleV4", path = "/v4/role", fallbackFactory = DefaultFallbackFactory.class, configuration = FeignConfig.class)
public interface RoleApiV4
{
    @PostMapping(value = "/query")
    @Operation(summary = "查询角色", tags = ApiTags.role)
    Result<PageResult<AppRoleInfoOnPage>> queryRole(
        @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
        @RequestParam(value = "pagesize", required = false, defaultValue = "10") Integer pagesize,
        @RequestParam(value = "domain", required = false) String domain,
        @RequestParam(value = "onlySysRole", required = false, defaultValue = "true") boolean onlySysRole);
    
    @PostMapping(value = "/add")
    @Operation(summary = "新增角色", tags = ApiTags.role)
    Result<Boolean> addRole(@RequestBody AppRoleInfo info);
    
    @PostMapping(value = "/upd")
    @Operation(summary = "修改角色", tags = ApiTags.role)
    Result<Boolean> updRole(@RequestBody AppRoleForUpd info);
    
    @PostMapping(value = "/enable")
    @Operation(summary = "启停角色", tags = ApiTags.role)
    Result<Boolean> enableRole(@RequestParam("pkey") String pkey, @RequestParam("enable") boolean enable);
    
    @PostMapping(value = "/del")
    @Operation(summary = "删除角色", tags = ApiTags.role)
    Result<String> delRole(@RequestParam(value = "pkey") String pkey,
        @RequestParam(value = "force", required = false, defaultValue = "false") boolean force);
    
    @PostMapping(value = "/getRoleMenu")
    @Operation(summary = "获取角色菜单", tags = ApiTags.role)
    Result<RoleMenuTree> getRoleMenu(@RequestParam(value = "pkey") String pkey);
    
    @PostMapping(value = "/setRoleMenu")
    @Operation(summary = "设置角色菜单", tags = ApiTags.role)
    Result<Boolean> setRoleMenu(@RequestBody RoleMenuTree data);
    
    @PostMapping(value = "/getRoleFunc")
    @Operation(summary = "获取角色权限", tags = ApiTags.role)
    Result<RoleAclTree> getRoleFunc(@RequestParam(value = "pkey", required = false) String pkey,
        @RequestParam(value = "group", required = false) String funcGroup);
    
    @PostMapping(value = "/getRoleFuncTree")
    @Operation(summary = "获取角色权限", tags = ApiTags.role)
    Result<RoleAclTree> getRoleFuncTree(@RequestParam(value = "pkey", required = false) String pkey,
        @RequestParam(value = "group", required = false) String funcGroup);
    
    @PostMapping(value = "/setRoleFunc")
    @Operation(summary = "设置角色权限", tags = ApiTags.role)
    Result<Boolean> setRoleFunc(@RequestBody RoleAclTree data);
    
    @PostMapping(value = "/setRoleFuncTree")
    @Operation(summary = "设置角色权限", tags = ApiTags.role)
    Result<Boolean> setRoleFuncTree(@RequestBody RoleAclTree data);
    
    @PostMapping(value = "/listInDept")
    @Operation(summary = "角色列表", tags = ApiTags.role)
    Result<List<AppRoleInfo>> listRoleInDept(@RequestParam(value = "domain") String domain,
        @RequestParam(value = "group", required = false) String group,
        @RequestParam(value = "deptid", required = false) String deptid);
}
