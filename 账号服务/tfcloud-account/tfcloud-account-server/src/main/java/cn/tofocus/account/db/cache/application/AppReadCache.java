package cn.tofocus.account.db.cache.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.account.bean.AccountConstant;
import cn.tofocus.account.bean.application.AppKV;
import cn.tofocus.account.db.cache.BaseStringReadCache;
import cn.tofocus.account.db.dao.application.ApplicationDao;
import cn.tofocus.account.db.entity.application.ApplicationEntity;
import cn.tofocus.core.exception.SysErrCode;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.security.SecurityContextUtil;
import cn.tofocus.db.DataRead;
import cn.tofocus.domain.cache.AppQueryInterface;

@Component
public class AppReadCache extends BaseStringReadCache<AppKV, ApplicationEntity> implements AppQueryInterface
{
    @Autowired
    private ApplicationDao dao;
    
    @Override
    protected String cacheName()
    {
        return AccountConstant.AppNameAccess;
    }
    
    @Override
    public int getMinsize()
    {
        return 100;
    }
    
    @Override
    public int getMaxsize()
    {
        return 300;
    }
    
    @Override
    protected Class<AppKV> getKVClass()
    {
        return AppKV.class;
    }
    
    @Override
    public String getDomainId(String appid)
    {
        AppKV kv = this.get(appid);
        if (kv != null)
            return kv.getDomainid();
        else
            return null;
    }
    
    @Override
    protected DataRead<String, ApplicationEntity> getDbAccess()
    {
        return dao;
    }
    
    @Override
    protected AppKV convert(ApplicationEntity entity)
    {
        if (entity == null)
            return null;
        else
            return new AppKV(entity.getPkey(), entity.getName(), entity.getDomainid(), entity.getNeedCaptcha());
    }

    public AppKV currentApp()
    {
        String appid = SecurityContextUtil.getAuthenticationContext().getClientId();
        if (appid == null)
            throw TofocusException.of(SysErrCode.Auth.CLIENT_ID_IS_NULL);
        AppKV app = this.get(appid);
        if (app == null)
            throw TofocusException.of(SysErrCode.Auth.UNKNOW_APPID);
        return app;
    }
}
