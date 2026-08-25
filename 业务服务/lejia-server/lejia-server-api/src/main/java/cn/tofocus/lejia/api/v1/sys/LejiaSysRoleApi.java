package cn.tofocus.lejia.api.v1.sys;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.account.bean.role.RoleMenuTree;
import cn.tofocus.account.bean.user.app.AppRoleInfo;
import cn.tofocus.core.Result;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@FeignClient(value = "lejia-server", contextId = "lejia-server-role", path = "/v1/sys/role", configuration = FeignConfig.class)
public interface LejiaSysRoleApi
{

   /*****************
    * 角色
    ****************/
    
    @Operation(summary = "新增角色", tags = ApiTags.custRole)
    @PostMapping(value = "/ins")
    public Result<Boolean> insRole(@RequestParam("name") @Parameter(description = "角色名称") String name,
        @RequestParam(value = "description", required = false) @Parameter(description = "角色描述") String description);
    
    @Operation(summary = "修改角色", tags = ApiTags.custRole)
    @PostMapping(value = "/upd")
    public Result<Boolean> updRole(@RequestParam(value = "pkey") @Parameter(description = "角色主键") String pkey,
        @RequestParam("name") @Parameter(description = "角色名称") String name,
        @RequestParam(value = "description", required = false) @Parameter(description = "角色描述") String description);
    
    @Operation(summary = "删除角色", tags = ApiTags.custRole)
    @PostMapping(value = "/del")
    public Result<?> delRole(@RequestParam(value = "pkey") @Parameter(description = "角色主键") String pkey);
    
    @Operation(summary = "查询角色", tags = ApiTags.custRole)
    @PostMapping(value = "/query")
    public Result<PageResult<AppRoleInfo>> query(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", defaultValue = "10") @Parameter(description = "每页大小") int pagesize);

    @Operation(summary = "获取角色的权限", tags = ApiTags.custRole)
    @PostMapping(value = "/getFunction")
    public Result<List<String>> getFunction(@RequestParam(value = "pkey") @Parameter(description = "角色主键") String pkey);
    
    @Operation(summary = "获取可选所有的权限", tags = ApiTags.custRole)
    @PostMapping(value = "/getCurrentUserFunction")
    public Result<RoleMenuTree> getCurrentUserFunction();
    
    @Operation(summary = "设置角色的权限", tags = ApiTags.custRole)
    @PostMapping(value = "/setFunction")
    public Result<Boolean> setFunction(@RequestBody RoleMenuTree rmt);
    
    @Operation(summary = "读取可用角色", tags = ApiTags.custRole)
    @PostMapping(value = "/queryForUser")
    public Result<PageResult<AppRoleInfo>> queryForUser();
    
    @Operation(summary = "读取可配置权限", tags = ApiTags.custRole)
    @PostMapping(value = "/getFunctionAll")
    public Result<RoleMenuTree> getFunctionAll();
}
