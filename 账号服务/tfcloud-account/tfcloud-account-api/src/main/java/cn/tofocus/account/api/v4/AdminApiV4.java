package cn.tofocus.account.api.v4;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.Size;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.account.api.ApiTags;
import cn.tofocus.account.bean.application.MenuConfig;
import cn.tofocus.account.bean.application.MenuInfo;
import cn.tofocus.account.dto.user.SysUserInfo;
import cn.tofocus.core.Result;
import cn.tofocus.core.data.KeyValue;
import cn.tofocus.core.exception.DefaultFallbackFactory;
import cn.tofocus.core.feign.FeignConfig;
import io.swagger.v3.oas.annotations.Operation;

@FeignClient(value = "account", contextId = "adminV4", path = "/v4/admin", fallbackFactory = DefaultFallbackFactory.class, configuration = FeignConfig.class)
public interface AdminApiV4
{
    
    /**************************
     * 
     *    用户管理
     * 
     **************************/
    
    /**
     * 管理员直接增加用户
     * 访问控制：需要SysFunctionEnum.addUser权限
     * @param name
     * @param actived
     * @param userid
     * @param mobile
     * @return
     */
    @PostMapping(value = "/user/addByMobile")
    Result<SysUserInfo> addUserByMobile(@RequestParam(name = "name") @Size(max = 100) String name,
        @RequestParam(name = "actived") boolean actived,
        @RequestParam(name = "mobile", required = false) @Size(max = 20) String mobile);
    
    /**
     * 管理员直接增加用户
     * 访问控制：需要SysFunctionEnum.addUser权限
     * @param name
     * @param actived
     * @param mobile
     * @return
     */
    @PostMapping(value = "/user/addByUserId")
    Result<SysUserInfo> addUserByUserId(@RequestParam(name = "name") @Size(max = 100) String name,
        @RequestParam(name = "actived") boolean actived, @RequestParam(name = "userid") @Size(max = 40) String userid,
        @RequestParam(name = "mobile") @Size(max = 20) String mobile);
    
    /**
     * 管理员删除用户
     * 访问控制：需要SysFunctionEnum.delUser权限
     * @param userkey
     */
    @PostMapping(value = "/user/del")
    Result<Boolean> delUser(@RequestParam("userkey") Long userkey);
    
    /**
     * 管理员修改用户信息
     * <p/>
     * 访问控制：需要SysFunctionEnum.modUser权限
     * @param userkey
     * @param userid
     * @param name
     * @param mobile
     * @param actived
     * @return
     */
    @PostMapping(value = "/user/modifyinfo")
    Result<Boolean> modifyuserinfo(@RequestParam("userkey") Long userkey,
        @RequestParam(name = "userid") @Size(max = 40) String userid,
        @RequestParam(name = "name") @Size(max = 100) String name,
        @RequestParam(name = "mobile", required = false) @Size(max = 20) String mobile);
    
    /**
     * 激活用户
     * <p/>
     * 访问控制：需要SysFunctionEnum.modUser权限
     * @param userkey
     * @param actived
     * @return
     */
    @PostMapping(value = "/user/enable")
    Result<Boolean> enableUser(@RequestParam("userkey") Long userkey, @RequestParam(name = "actived") boolean actived);

    /**
     * 修改密码
     * <p/>
     * 访问控制：需要SysFunctionEnum.modUser权限
     * @param userkey
     * @param actived
     * @return
     */
    @PostMapping(value = "/user/resetPassword")
    Result<Boolean> resetPassword(@RequestParam("userkey") Long userkey, @RequestParam(name = "pwd") String pwd);
    
    /**************************
     * 
     *     公司管理
     * 
     **************************/
    
    /**
     * 新增修改公司
     * <p/>
     * <功能详细描述>
     * @param bank
     * @return
     */
    @Operation(summary = "新增或修改机构")
    @PostMapping(value = "/org/save")
    Result<Boolean> saveOrginazation(@RequestParam(name = "orgid") @Size(max = 40) String orgid,
        @RequestParam(name = "name") @Size(max = 100) String name);
    
    /**
     * 删除公司
     * <p/>
     * @param orgid
     * @return
     */
    @PostMapping(value = "/org/del")
    Result<Boolean> delOrginazation(@RequestParam(name = "orgid") @Size(max = 40) String orgid);
    
    /**************************
     * 
     *     部门管理
     * 
     **************************/
    /**
     * 新增修改部门
     * <p/>
     * <功能详细描述>
     * @param dept
     * @return
     */
    @PostMapping(value = "/dept/save")
    Result<Boolean> saveDepartment(@RequestParam(name = "deptid") @Size(max = 40) String deptid,
        @RequestParam(name = "orgid") @Size(max = 40) String orgid,
        @RequestParam(name = "name") @Size(max = 100) String name);
    
    /**
     * 删除部门
     * <p/>
     * <功能详细描述>
     * @param deptid
     * @return
     */
    @PostMapping(value = "/dept/del")
    Result<Boolean> delDepartment(@RequestParam(name = "deptid") @Size(max = 40) String deptid);

    /**
     * 删除部门以及其关联数据
     * <p/>
     * @param deptid
     * @return
     */
    @PostMapping(value = "/dept/delAll")
    Result<Boolean> delDepartmentAll(@RequestParam(name = "deptid") @Size(max = 40) String deptid);
    
    
    @PostMapping(value = "/listMenuByModel")
    @Operation(summary = "查询菜单", tags = ApiTags.menu)
    Result<List<MenuInfo>> listMenuByModel(@RequestParam(value = "application") String application,
        @RequestParam(value = "model") String model);
    
    @Operation(summary = "设置市场模块配置", tags = {ApiTags.model})
    @PostMapping(value = "/setModelConfigByDept")
    Result<Boolean> setModelConfigByDept(@RequestBody KeyValue<String, Map<String, Boolean>> value);
    
    @Operation(summary = "设置公司模块配置", tags = {ApiTags.model})
    @PostMapping(value = "/setModelConfigByOrg")
    Result<Boolean> setModelConfigByOrg(@RequestBody KeyValue<String, Map<String, Boolean>> value);
    
    @Operation(summary = "设置市场菜单配置", tags = {ApiTags.menu})
    @PostMapping(value = "/setMenuConfigByDept")
    Result<Boolean> setMenuConfigByDept(@RequestBody MenuConfig config);
    
    @Operation(summary = "设置公司菜单配置", tags = {ApiTags.menu})
    @PostMapping(value = "/setMenuConfigByOrg")
    Result<Boolean> setMenuConfigByOrg(@RequestBody MenuConfig config);
}
