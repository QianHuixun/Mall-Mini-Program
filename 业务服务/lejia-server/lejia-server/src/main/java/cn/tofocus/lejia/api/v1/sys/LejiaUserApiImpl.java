package cn.tofocus.lejia.api.v1.sys;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.account.api.v2.user.UserApi;
import cn.tofocus.account.dto.user.SysUserInfo;
import cn.tofocus.core.Result;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.core.log.LogApi;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.sys.UserDTO;
import cn.tofocus.lejia.domain.UserManager;

@RequestMapping("/v1/sys/user")
@RestController
public class LejiaUserApiImpl implements LejiaUserApi
{
    
//    @Autowired
//    private AdminApi adminApi;
    
    @Autowired
    private UserApi userApi;
    
    @Autowired
    private UserManager userManager;
    
    @Override
    public Result<UserDTO> getUser(Integer pkey)
    {
        UserDTO info = userManager.getUserInfo(pkey);
        return new Result<>(info);
    }
    
    @Override
    public Result<PageResult<UserDTO>> listUser(int page, int pagesize)
    {
        return new Result<>(userManager.listUser(page, pagesize));
    }
    
    @Override
    @LogApi(operation = "修改用户", format = "修改用户,名称:{user.nickname}")
    public Result<UserDTO> updUserInfo(UserDTO user)
    {
        return new Result<>(userManager.updUser(user));
    }
    
    @Override
    @LogApi(operation = "删除用户", format = "删除用户")
    public Result<Boolean> delUser(Integer pkey)
    {
        return new Result<>(userManager.delUser(pkey));
    }
    
    @Override
    @LogApi(operation = "新增用户", format = "新增用户,名称:{nickname},电话:{mobile}", resultFormat = "")
    public Result<SysUserInfo> insUser(String nickname, String mobile, String roleKey)
    {
        SysUserInfo result = userManager.createOrBindSysUser(nickname, mobile, roleKey);
        return new Result<>(result);
    }
    
    @Override
    @LogApi(operation = "修改用户密码")
    public Result<Object> modifyPassword(String oldpassword, String newpassword)
    {
        Result<Object> password = userApi.modifyPassword(oldpassword, newpassword);
        return password;
    }
    
}
