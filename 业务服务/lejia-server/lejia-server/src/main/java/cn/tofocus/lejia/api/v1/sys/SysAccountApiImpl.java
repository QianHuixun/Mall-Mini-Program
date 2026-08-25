package cn.tofocus.lejia.api.v1.sys;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.lejia.bean.dto.sys.SysAccountShieldVersion;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.domain.AccountManager;
import cn.tofocus.lejia.exception.LejiaErrCode;

@RequestMapping("/v1/sys/account")
@RestController
public class SysAccountApiImpl implements SysAccountApi
{
    @Autowired
    private AccountManager accountManager;
    
    @Override
    public Result<SysAccountShieldVersion> getShieldVersion()
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        if (ascription == null) throw TofocusException.of(LejiaErrCode.NOT_RIGHT);
        SysAccountShieldVersion shieldVersion = accountManager.getShieldVersion(ascription);
        return new Result<>(shieldVersion);
    }
    
    @Override
    public Result<Boolean> saveShieldVersion(SysAccountShieldVersion shieldVersion)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        if (ascription == null) throw TofocusException.of(LejiaErrCode.NOT_RIGHT);
        boolean sign = accountManager.saveShieldVersion(ascription, shieldVersion);
        return new Result<>(sign);
    }
}
