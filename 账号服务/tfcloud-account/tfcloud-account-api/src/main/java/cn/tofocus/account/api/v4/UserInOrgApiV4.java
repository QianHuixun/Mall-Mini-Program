package cn.tofocus.account.api.v4;

import java.util.Set;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.account.api.ApiTags;
import cn.tofocus.core.Result;
import cn.tofocus.core.exception.DefaultFallbackFactory;
import cn.tofocus.core.feign.FeignConfig;
import io.swagger.v3.oas.annotations.Operation;

@FeignClient(value = "account", contextId = "userInOrgApiV4", path = "/v4/userInOrg", fallbackFactory = DefaultFallbackFactory.class, configuration = FeignConfig.class)
public interface UserInOrgApiV4
{
    /**
     * 增加用户在一个机构里担任的角色
     * @param userkey
     * @param role
     * @param orgid
     * @return
     */
    @Operation(summary = "增加用户在一个机构里担任的角色", tags = ApiTags.user)
    @PostMapping(value = "/addRole")
    Result<Boolean> addUserRole(@RequestParam("userkey") Long userkey, @RequestParam("role") String role,
        @RequestParam(value = "orgid") String orgid);
    
    /**
     * 删除用户在一个机构里担任的角色
     * @param userkey
     * @param role
     * @param orgid
     * @return
     */
    @Operation(summary = "删除用户在一个机构里担任的角色", tags = ApiTags.user)
    @PostMapping(value = "/delRole")
    Result<Boolean> delUserRole(@RequestParam("userkey") Long userkey, @RequestParam("role") String role,
        @RequestParam(value = "orgid") String orgid);
    
    /**
     * 机构及下属部门里担任角色的用户
     * @param depts
     * @param roles
     * @return
     */
    @Operation(summary = "机构及下属部门里担任角色的用户", tags = ApiTags.user)
    @PostMapping(value = "/listUserByRole")
    Result<Set<Long>> listUserByRole(@RequestParam("role") String role, @RequestParam(value = "orgid") String orgid);
}
