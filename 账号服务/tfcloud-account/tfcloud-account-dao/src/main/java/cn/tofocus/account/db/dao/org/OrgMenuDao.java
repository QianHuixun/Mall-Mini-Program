package cn.tofocus.account.db.dao.org;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cn.tofocus.account.db.entity.org.OrgMenuEntity;
import cn.tofocus.account.db.entity.org.OrgMenuEntity.F;
import cn.tofocus.db.SlowQueryLog;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;

@Component
public class OrgMenuDao extends JpaSpecificationDelegate<String, OrgMenuEntity>
{
    
    public Map<String, Set<String>> findDisableMenus(String appid, String orgid)
    {
        Map<String, Set<String>> map = new HashMap<>();
        List<OrgMenuEntity> list =
            this.select().strict(true).eq(F.menuAppid, appid).eq(F.orgid, orgid).eq(F.enable, false).exec();
        for (OrgMenuEntity e : list)
        {
            Set<String> set = map.computeIfAbsent(e.getMenuModel(), k -> new HashSet<>());
            set.add(e.getMenu());
        }
        return map;
    }
    
    public Map<String, Set<String>> findEnableMenus(String appid, String orgid)
    {
        Map<String, Set<String>> map = new HashMap<>();
        List<OrgMenuEntity> list =
            this.select().strict(true).eq(F.menuAppid, appid).eq(F.orgid, orgid).eq(F.enable, true).exec();
        for (OrgMenuEntity e : list)
        {
            Set<String> set = map.computeIfAbsent(e.getMenuModel(), k -> new HashSet<>());
            set.add(e.getMenu());
        }
        return map;
    }
    
    public List<OrgMenuEntity> listByDomain(String domain)
    {
        return this.select().strict(true).eq(F.domainid, domain).exec();
    }
    
    public void delByMenu(String menu)
    {
        this.select().strict(true).eq(F.menu, menu).del();
    }
    
    public Set<String> findDisableMenusByModel(String orgid, String model)
    {
        Set<String> set = new HashSet<>();
        List<OrgMenuEntity> list =
            this.select().strict(true).eq(F.menuModel, model).eq(F.orgid, orgid).eq(F.enable, false).exec();
        for (OrgMenuEntity e : list)
        {
            set.add(e.getMenu());
        }
        return set;
    }
    
    public Set<String> findEnableMenusByModel(String orgid, String model)
    {
        Set<String> set = new HashSet<>();
        List<OrgMenuEntity> list =
            this.select().strict(true).eq(F.menuModel, model).eq(F.orgid, orgid).eq(F.enable, true).exec();
        for (OrgMenuEntity e : list)
        {
            set.add(e.getMenu());
        }
        return set;
    }
    
    @SlowQueryLog(timeout = 30000)
    @Transactional(rollbackFor = Exception.class)
    public void updateConfig(String domainid, String orgid, String model, String application,
        Collection<String> menus, boolean selected)
    {
        List<OrgMenuEntity> dels =
            this.select().strict(true).eq(F.orgid, orgid).eq(F.menuModel, model).eq(F.menuAppid, application).exec();
        List<OrgMenuEntity> list = new ArrayList<>();
        for (String menu : menus)
        {
            OrgMenuEntity entity = new OrgMenuEntity();
            entity.setDomainid(domainid);
            entity.setOrgid(orgid);
            entity.setMenuAppid(application);
            entity.setMenuModel(model);
            entity.setMenu(menu);
            entity.setEnable(selected);
            entity.setPkey(OrgMenuEntity.genenateKey(orgid, menu));
            list.add(entity);
        }
        this.removeAndPutAll(dels, list);
    }
}
