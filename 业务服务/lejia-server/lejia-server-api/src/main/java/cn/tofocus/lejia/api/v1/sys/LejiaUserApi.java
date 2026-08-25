package cn.tofocus.lejia.api.v1.sys;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.account.dto.user.SysUserInfo;
import cn.tofocus.core.Result;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.sys.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@FeignClient(value = "lejia-server", contextId = "lejia-server-user", path = "/v1/sys/user", 
fallbackFactory = LejiaUserFallback.class, configuration = FeignConfig.class)
public interface LejiaUserApi 
{
	@Operation(summary = "新增用户", tags = ApiTags.custUser)
	@PostMapping(value = "/ins")
	public Result<SysUserInfo> insUser(@RequestParam(name = "nickname") String nickname, @RequestParam(name = "mobile") String mobile,
			@RequestParam(name = "roleKey") String roleKey);

	@Operation(summary = "获取用户", tags = ApiTags.custUser)
	@PostMapping(value = "/get")
	public Result<UserDTO> getUser(@RequestParam(name = "pkey") Integer pkey);

	@Operation(summary = "获取用户列表", tags = ApiTags.custUser)
	@PostMapping(value = "/list")
	public Result<PageResult<UserDTO>> listUser(
			@RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
			@RequestParam(value = "pagesize", defaultValue = "10") @Parameter(description = "每页大小") int pagesize);

	@Operation(summary = "修改用户", tags = ApiTags.custUser)
	@PostMapping(value = "/upd")
	public Result<UserDTO> updUserInfo(@RequestBody UserDTO info);

	@Operation(summary = "删除用户", tags = ApiTags.custUser)
	@PostMapping(value = "/del")
	public Result<Boolean> delUser(@RequestParam(name = "pkey") Integer pkey);
	
	@Operation(summary = "修改密码", tags = ApiTags.custUser)
	@PostMapping(value = "/modeify")
	public Result<Object> modifyPassword(@RequestParam("oldpassword") String oldpassword,
	        @RequestParam("newpassword") String newpassword);
	
}
