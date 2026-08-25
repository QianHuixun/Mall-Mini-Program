package cn.tofocus.account.db.dao.org;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cn.tofocus.account.db.entity.org.OrgModelEntity.F;
import cn.tofocus.account.db.entity.org.OrgModelEntity;
import cn.tofocus.db.SlowQueryLog;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;

@Component
public class OrgModelDao extends JpaSpecificationDelegate<String, OrgModelEntity>
{
    
    public Map<String, Boolean> findByOrg(String orgid)
    {
        Map<String, Boolean> map = new HashMap<>();
        List<OrgModelEntity> models = this.select().strict(true).eq(F.orgid, orgid).exec();
        for (OrgModelEntity e : models)
        {
            map.put(e.getModelId(), e.isEnable());
        }
        return map;
    }
    
    public List<OrgModelEntity> listByDomain(String domain)
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
    
    @SlowQueryLog
    @Transactional(rollbackFor = Exception.class)
    public void updateConfig(String domainid, String orgid, Map<String, Boolean> modelConfigs)
    {
        List<OrgModelEntity> dels = this.select().strict(true).eq(F.orgid, orgid).exec();
        List<OrgModelEntity> list = new ArrayList<>();
        for (Entry<String, Boolean> entry : modelConfigs.entrySet())
        {
            OrgModelEntity entity = new OrgModelEntity();
            entity.setDomainid(domainid);
            entity.setEnable(entry.getValue());
            entity.setModelId(entry.getKey());
            entity.setOrgid(orgid);
            entity.setPkey(OrgModelEntity.genenateKey(orgid, entry.getKey()));
            list.add(entity);
        }
        this.removeAndPutAll(dels, list);
    }
    
}
