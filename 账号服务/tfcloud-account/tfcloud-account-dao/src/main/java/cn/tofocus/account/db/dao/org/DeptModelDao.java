package cn.tofocus.account.db.dao.org;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cn.tofocus.account.db.entity.org.DeptModelEntity;
import cn.tofocus.account.db.entity.org.DeptModelEntity.F;
import cn.tofocus.db.SlowQueryLog;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;

@Component
public class DeptModelDao extends JpaSpecificationDelegate<String, DeptModelEntity>
{
    
    public Map<String, Boolean> findByDept(String deptid)
    {
        Map<String, Boolean> map = new HashMap<>();
        List<DeptModelEntity> models = this.select().strict(true).eq(F.deptid, deptid).exec();
        for (DeptModelEntity e : models)
        {
            map.put(e.getModelId(), e.isEnable());
        }
        return map;
    }
    
    public List<DeptModelEntity> listByDomain(String domain)
    {
        return this.select().strict(true).eq(F.domainid, domain).exec();
    }
    
    public void delByModel(String model)
    {
        this.select().strict(true).eq(F.modelId, model).del();
    }
    
    public int countByModel(String model)
    {
        return (int)this.aggregation().eq(F.modelId, model).execCount();
    }
    
    @SlowQueryLog(timeout = 30000)
    @Transactional(rollbackFor = Exception.class)
    public void updateConfig(String domainid, String orgid, String deptid, Map<String, Boolean> modelConfigs)
    {
        List<DeptModelEntity> dels = this.select().strict(true).eq(F.deptid, deptid).exec();
        List<DeptModelEntity> list = new ArrayList<>();
        for (Entry<String, Boolean> entry : modelConfigs.entrySet())
        {
            DeptModelEntity entity = new DeptModelEntity();
            entity.setDomainid(domainid);
            entity.setEnable(entry.getValue());
            entity.setModelId(entry.getKey());
            entity.setOrgid(orgid);
            entity.setDeptid(deptid);
            entity.setPkey(DeptModelEntity.genenateKey(deptid, entry.getKey()));
            list.add(entity);
        }
        this.removeAndPutAll(dels, list);
    }
    
    public long delByDept(String deptid)
    {
        return this.select().strict(true).eq(F.deptid, deptid).del();
    }
}
