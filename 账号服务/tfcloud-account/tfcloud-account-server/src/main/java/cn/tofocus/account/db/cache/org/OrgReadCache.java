package cn.tofocus.account.db.cache.org;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.account.bean.AccountConstant;
import cn.tofocus.account.bean.org.OrgKV;
import cn.tofocus.account.db.cache.BaseStringReadCache;
import cn.tofocus.account.db.dao.org.OrginazationDao;
import cn.tofocus.account.db.entity.org.OrginazationEntity;
import cn.tofocus.db.DataRead;
import cn.tofocus.domain.cache.OrgQueryInterface;

@Component
public class OrgReadCache extends BaseStringReadCache<OrgKV, OrginazationEntity>  implements OrgQueryInterface
{
    @Autowired
    private OrginazationDao dao;

    @Override
    protected String cacheName()
    {
        return AccountConstant.OrgNameAccess;
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
    protected Class<OrgKV> getKVClass()
    {
        return OrgKV.class;
    }

    @Override
    protected DataRead<String, OrginazationEntity> getDbAccess()
    {
        return dao;
    }

    @Override
    protected OrgKV convert(OrginazationEntity entity)
    {
        if (entity == null)
            return null;
        else
        {
            OrgKV kv = new OrgKV();
            kv.setPkey(entity.getPkey());
            kv.setName(entity.getName());
            kv.setDomainid(entity.getDomainid());
            return kv;
        }
    }

    

}
