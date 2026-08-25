package cn.tofocus.account.api.v4;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.account.api.ApiTags;
import cn.tofocus.account.bean.AppMenu;
import cn.tofocus.core.Result;
import cn.tofocus.core.data.StrKeyName;
import cn.tofocus.core.exception.DefaultFallbackFactory;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.domain.user.role.RoleInfo;
import io.swagger.v3.oas.annotations.Operation;

@FeignClient(value = "account", contextId = "userInDeptApiV4", path = "/v4/userInDept", fallbackFactory = DefaultFallbackFactory.class, configuration = FeignConfig.class)
public interface UserInDeptApiV4
{
    /**
     * 删除用户在一个部门的所有角色
     * @param userkey
     * @param roles
     * @return
     */
    @Operation(summary = "删除用户在一个部门的所有角色", tags = ApiTags.user)
    @PostMapping(value = "/clearUser")
    Result<Boolean> clearUser(@RequestParam("userkey") Long userkey, @RequestParam(value = "deptid") String deptid);
    
    /**
     * 增加用户在一个部门里担任的角色
     * @param userkey
     * @param role
     * @param deptid
     * @return
     */
    @Operation(summary = "增加用户在一个部门里担任的角色", tags = ApiTags.user)
    @PostMapping(value = "/addRole")
    Result<Boolean> addUserRole(@RequestParam("userkey") Long userkey, @RequestParam("role") String role,
        @RequestParam(value = "deptid") String deptid);
    
    /**
     * 删除用户在一个部门里担任的角色
     * @param userkey
     * @param role
     * @param deptid
     * @return
     */
    @Operation(summary = "删除用户在一个部门里担任的角色", tags = ApiTags.user)
    @PostMapping(value = "/delRole")
    Result<Boolean> delUserRole(@RequestParam("userkey") Long userkey, @RequestParam("role") String role,
        @RequestParam(value = "deptid") String deptid);
    
    /**
     * 设置用户在一个部门里的担任的角色
     * @param userkey
     * @param role
     * @param deptid
     * @return
     */
    @Operation(summary = "设置用户在一个部门里的担任的角色", tags = ApiTags.user)
    @PostMapping(value = "/setRole")
    Result<Boolean> setUserRole(@RequestParam("userkey") Long userkey,
        @RequestParam(value = "roles", required = false) List<String> roles,
        @RequestParam(value = "deptid") String deptid, @RequestParam(value = "roleGroup") String roleGroup);
    
    /**
     * 用户在一个部门下的担任的角色
     * @param userkey
     * @param deptid
     * @return
     */
    @Operation(summary = "用户在一个部门下的担任的角色", tags = ApiTags.user)
    @PostMapping(value = "/listRole")
    Result<List<RoleInfo>> listUserRole(@RequestParam("userkey") Long userkey,
        @RequestParam(value = "deptid") String deptid);
    
    /**
     * 用户在一个部门下的担任的角色
     * @param userkey
     * @param deptid
     * @return
     */
    @Operation(summary = "用户在一个部门下的担任的角色", tags = ApiTags.user)
    @PostMapping(value = "/mapUserRole")
    Result<Map<Long, List<RoleInfo>>> mapUserRole(@RequestParam("userkeys") List<Long> userkeys,
        @RequestParam(value = "deptid") String deptid);
    
    /**
     * 用户在一个部门下的权限
     * @param userkey
     * @param deptid
     * @return
     */
    @Operation(summary = "用户在一个部门下的权限", tags = ApiTags.user)
    @PostMapping(value = "/listAcl")
    Result<List<StrKeyName>> listUserAcl(@RequestParam("userkey") Long userkey,
        @RequestParam(value = "deptid") String deptid);
    
    /**
     * 用户在一个部门下的菜单
     * @param userkey
     * @param application
     * @param deptid
     * @return
     */
    @Operation(summary = "用户在一个部门下的菜单", tags = ApiTags.user)
    @PostMapping(value = "/listMenu")
    Result<List<AppMenu>> listUserMenu(@RequestParam("userkey") Long userkey,
        @RequestParam(value = "application") String application, @RequestParam(value = "deptid") String deptid);
    
    /**
     * 用户在哪些部门里担任了角色
     * @param userkey
     * @param role
     * @return
     */
    @Operation(summary = "用户在哪些部门里担任了角色", tags = ApiTags.user)
    @PostMapping(value = "/listDeptByRole")
    Result<Set<String>> listDeptByRole(@RequestParam("userkeys") List<Long> userkeys,
        @RequestParam("roles") List<String> roles);
    
    /**
     * 用户在哪些部门里担任了角色，按用户分组
     * @param userkeys
     * @param role
     * @return
     */
    @Operation(summary = "用户在哪些部门里担任了角色，按用户分组", tags = ApiTags.user)
    @PostMapping(value = "/mapDeptByRoleGroupByUser")
    Result<Map<Long, List<String>>> mapDeptByRoleGroupByUser(@RequestParam("userkeys") List<Long> userkeys,
        @RequestParam("role") String role);
    
    /**
     * 部门里担任角色的用户
     * @param depts
     * @param roles
     * @return
     */
    @Operation(summary = "部门里担任角色的用户", tags = ApiTags.user)
    @PostMapping(value = "/listUserByRoles")
    Result<Set<Long>> listUserByRoles(@RequestParam(value = "depts") List<String> depts,
        @RequestParam("roles") List<String> roles, @RequestParam("includeOrgScope") boolean includeOrgScope);
    
    /**
     * 部门里担任角色的用户，按部门分组
     * @param depts
     * @param role
     * @return
     */
    @Operation(summary = "部门里担任角色的用户，按部门分组", tags = ApiTags.user)
    @PostMapping(value = "/mapUserByRoleGroupByDept")
    Result<Map<String, List<Long>>> mapUserByRoleGroupByDept(@RequestParam("depts") List<String> depts,
        @RequestParam("roles") List<String> roles);
    
}
