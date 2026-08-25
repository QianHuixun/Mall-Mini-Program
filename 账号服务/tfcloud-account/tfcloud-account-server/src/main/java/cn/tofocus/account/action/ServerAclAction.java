package cn.tofocus.account.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.exception.SysErrCode;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.security.SecurityContextUtil;
import cn.tofocus.domain.manager.UserPermissionManager;

@RestController
public class ServerAclAction
{
    @Autowired
    private UserPermissionManager userPermissionManager;
    
    @PostMapping(value = "/server/resetAcl")
    public Result<Boolean> resetAllAcl(@RequestParam(name = "userkey", required = false) Long userkey)
    {
        String appid = SecurityContextUtil.getAuthenticationContext().getClientId();
        if (!("account".equals(appid) || "tfManager".equals(appid))
            || SecurityContextUtil.getAuthenticationContext().getUserkey() != -1)
        {
            throw TofocusException.of(SysErrCode.ACCESS_DENIED);
        }
        if (userkey == null)
            userPermissionManager.resetAllAcl();
        else
            userPermissionManager.resetUserAcl(userkey);
        return new Result<>(true);
    }
    
    @PostMapping(value = "/server/resetRoleAcl")
    public Result<Boolean> resetRoleAcl(@RequestParam(name = "roleid", required = false) String roleid)
    {
        String appid = SecurityContextUtil.getAuthenticationContext().getClientId();
        if (!("account".equals(appid) || "tfManager".equals(appid))
            || SecurityContextUtil.getAuthenticationContext().getUserkey() != -1)
        {
            throw TofocusException.of(SysErrCode.ACCESS_DENIED);
        }
        userPermissionManager.resetRoleAcl(roleid);
        return new Result<>(true);
    }
}
