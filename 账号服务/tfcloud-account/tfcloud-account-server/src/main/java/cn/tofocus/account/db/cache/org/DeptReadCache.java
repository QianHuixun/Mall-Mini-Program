package cn.tofocus.account.db.cache.org;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.account.bean.AccountConstant;
import cn.tofocus.account.bean.org.DeptKV;
import cn.tofocus.account.db.cache.BaseStringReadCache;
import cn.tofocus.account.db.dao.org.DepartmentDao;
import cn.tofocus.account.db.entity.org.DepartmentEntity;
import cn.tofocus.db.DataRead;
import cn.tofocus.domain.cache.DeptQueryInterface;

@Component
public class DeptReadCache extends BaseStringReadCache<DeptKV, DepartmentEntity> implements DeptQueryInterface
{
    
    @Autowired
    private DepartmentDao dao;
    
    @Override
    protected String cacheName()
    {
        return AccountConstant.DeptNameAccess;
    }
    
    @Override
    public int getMinsize()
    {
        return 500;
    }
    
    @Override
    public int getMaxsize()
    {
        return 2000;
    }
    
    @Override
    protected Class<DeptKV> getKVClass()
    {
        return DeptKV.class;
    }
    
    @Override
    protected DataRead<String, DepartmentEntity> getDbAccess()
    {
        return dao;
    }
    
    @Override
    protected DeptKV convert(DepartmentEntity entity)
    {
        if (entity == null)
            return null;
        else
        {
            DeptKV kv = new DeptKV();
            kv.setPkey(entity.getPkey());
            kv.setName(entity.getName());
            kv.setOrgid(entity.getOrgid());
            kv.setDomainid(entity.getDomainid());
            return kv;
        }
    }
    
}
