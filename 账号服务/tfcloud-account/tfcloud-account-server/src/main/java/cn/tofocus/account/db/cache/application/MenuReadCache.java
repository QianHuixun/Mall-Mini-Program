package cn.tofocus.account.db.cache.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.account.bean.AccountConstant;
import cn.tofocus.account.db.cache.BaseStringReadCache;
import cn.tofocus.account.db.dao.application.MenuDao;
import cn.tofocus.account.db.entity.application.MenuEntity;
import cn.tofocus.core.data.StrKeyName;
import cn.tofocus.db.DataRead;

@Component
public class MenuReadCache extends BaseStringReadCache<StrKeyName, MenuEntity>
{
    @Autowired
    private MenuDao dao;
    
    @Override
    protected String cacheName()
    {
        return AccountConstant.MenuNameAccess;
    }
    
    @Override
    public int getMinsize()
    {
        return 200;
    }
    
    @Override
    public int getMaxsize()
    {
        return 500;
    }
    
    @Override
    protected Class<StrKeyName> getKVClass()
    {
        return StrKeyName.class;
    }
    
    @Override
    protected DataRead<String, MenuEntity> getDbAccess()
    {
        return dao;
    }
    
    @Override
    protected StrKeyName convert(MenuEntity entity)
    {
        if (entity == null)
            return null;
        else
            return new StrKeyName(entity.getPkey(), entity.getName());
    }
    
}
