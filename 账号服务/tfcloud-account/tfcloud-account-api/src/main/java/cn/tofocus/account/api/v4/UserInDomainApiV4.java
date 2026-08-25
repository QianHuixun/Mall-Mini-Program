package cn.tofocus.account.api.v4;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.account.api.ApiTags;
import cn.tofocus.core.Result;
import cn.tofocus.core.exception.DefaultFallbackFactory;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.domain.user.def.RoleInstanceDTO;
import cn.tofocus.domain.user.role.RoleInfo;
import io.swagger.v3.oas.annotations.Operation;

@FeignClient(value = "account", contextId = "userInDomainApiV4", path = "/v4/userInDomain", fallbackFactory = DefaultFallbackFactory.class, configuration = FeignConfig.class)
public interface UserInDomainApiV4
{
    @Operation(summary = "列出用户被赋予的角色", tags = ApiTags.user)
    @PostMapping(value = "/listRoleInstance")
    Result<List<RoleInstanceDTO>> listUserRoleInstance(@RequestParam("userkey") Long userkey,
        @RequestParam("role") String role);
    
    @Operation(summary = "用户是否有权限", tags = ApiTags.user)
    @PostMapping(value = "/isUserHasRight")
    Result<Boolean> isUserHasRight(@RequestParam("userkey") Long userkey, @RequestParam("function") String function);
    
    /**
     * 删除用户的所有角色
     * @param userkey
     * @param roles
     * @return
     */
    @Operation(summary = "删除用户的所有角色", tags = ApiTags.user)
    @PostMapping(value = "/clearUserRoleInstance")
    Result<Boolean> clearUserRoleInstance(@RequestParam("userkey") Long userkey);
    
    /**
     * 担任角色的用户
     * @param roles
     * @return
     */
    @Operation(summary = "角色的用户", tags = ApiTags.user)
    @PostMapping(value = "/listUserByRoles")
    Result<Set<Long>> listUserByRoles(@RequestParam("roles") List<String> roles);
    
    /**
     * 用户的角色
     * @param userkey
     * @return
     */
    @Operation(summary = "用户的角色", tags = ApiTags.user)
    @PostMapping(value = "/listRole")
    Result<List<RoleInfo>> listUserRole(@RequestParam("userkey") Long userkey);
    
    /**
     * 用户的角色
     * @param userkey
     * @return
     */
    @Operation(summary = "用户的角色", tags = ApiTags.user)
    @PostMapping(value = "/mapUserRole")
    Result<Map<Long, List<RoleInfo>>> mapUserRole(@RequestParam("userkeys") List<Long> userkeys);
    
    /**
     * 增加用户的无授权范围角色
     * @param userkey
     * @param roles
     * @return
     */
    @Operation(summary = "增加用户的无授权范围角色", tags = ApiTags.user)
    @PostMapping(value = "/addRole")
    Result<Boolean> addUserRole(@RequestParam("userkey") Long userkey, @RequestParam("role") String role);
    
    /**
     * 删除用户的无授权范围角色
     * @param userkey
     * @param roles
     * @return
     */
    @Operation(summary = "删除用户的无授权范围角色", tags = ApiTags.user)
    @PostMapping(value = "/delRole")
    Result<Boolean> delUserRole(@RequestParam("userkey") Long userkey, @RequestParam("role") String role);
    
    /**
     * 增加用户的域级范围角色
     * @param userkey
     * @param roles
     * @return
     */
    @Operation(summary = "增加用户的域级范围角色", tags = ApiTags.user)
    @PostMapping(value = "/addRoleInDomain")
    Result<Boolean> addUserRoleInDomain(@RequestParam("userkey") Long userkey, @RequestParam("role") String role);
    
    /**
     * 删除用户的域级范围角色
     * @param userkey
     * @param roles
     * @return
     */
    @Operation(summary = "删除用户的域级范围角色", tags = ApiTags.user)
    @PostMapping(value = "/delRoleInDomain")
    Result<Boolean> delUserRoleInDomain(@RequestParam("userkey") Long userkey, @RequestParam("role") String role);
    
    /**
     * 设置用户的域级角色
     * @param userkey
     * @param role
     * @param deptid
     * @return
     */
    @Operation(summary = "设置用户的域级角色", tags = ApiTags.user)
    @PostMapping(value = "/setRoleInDomain")
    Result<Boolean> setUserRoleInDomain(@RequestParam("userkey") Long userkey,
        @RequestParam(value = "roles", required = false) List<String> roles,
        @RequestParam(value = "roleGroup") String roleGroup);
    
    /**
     * 用户的域级角色
     * @param userkey
     * @param deptid
     * @return
     */
    @Operation(summary = "用户的域级角色", tags = ApiTags.user)
    @PostMapping(value = "/listRoleInDomain")
    Result<List<RoleInfo>> listUserRoleInDomain(@RequestParam("userkey") Long userkey);
}
