package cn.tofocus.account.db.cache.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.account.bean.AccountConstant;
import cn.tofocus.account.db.cache.BaseStringReadCache;
import cn.tofocus.account.db.dao.domain.CloudDomainDao;
import cn.tofocus.account.db.entity.domain.CloudDomainEntity;
import cn.tofocus.core.data.StrKeyName;
import cn.tofocus.db.DataRead;
import cn.tofocus.domain.cache.DomainQueryInterface;

@Component
public class DomainReadCache extends BaseStringReadCache<StrKeyName, CloudDomainEntity> implements DomainQueryInterface
{
    @Autowired
    private CloudDomainDao dao;

    @Override
    protected String cacheName()
    {
        return AccountConstant.DomainNameAccess;
    }
    
    @Override
    public int getMinsize()
    {
        return 5;
    }
    
    @Override
    public int getMaxsize()
    {
        return 20;
    }
    
    @Override
    protected Class<StrKeyName> getKVClass()
    {
        return StrKeyName.class;
    }

    @Override
    protected DataRead<String, CloudDomainEntity> getDbAccess()
    {
        return dao;
    }

    @Override
    protected StrKeyName convert(CloudDomainEntity entity)
    {
        if (entity == null)
            return null;
        else
            return new StrKeyName(entity.getPkey(), entity.getName());
    }
    
    
}
