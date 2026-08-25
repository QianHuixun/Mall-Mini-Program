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

import cn.tofocus.account.db.entity.org.DeptMenuEntity;
import cn.tofocus.account.db.entity.org.DeptMenuEntity.F;
import cn.tofocus.db.SlowQueryLog;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;

@Component
public class DeptMenuDao extends JpaSpecificationDelegate<String, DeptMenuEntity>
{
    public Map<String, Set<String>> findDisableMenus(String appid, String deptid)
    {
        Map<String, Set<String>> map = new HashMap<>();
        List<DeptMenuEntity> list =
            this.select().strict(true).eq(F.menuAppid, appid).eq(F.deptid, deptid).eq(F.enable, false).exec();
        for (DeptMenuEntity e : list)
        {
            Set<String> set = map.computeIfAbsent(e.getMenuModel(), k -> new HashSet<>());
            set.add(e.getMenu());
        }
        return map;
    }
    
    public List<DeptMenuEntity> listByDomain(String domain)
    {
        return this.select().strict(true).eq(F.domainid, domain).exec();
    }
    
    public void delByMenu(String menu)
    {
        this.select().strict(true).eq(F.menu, menu).del();
    }
    
    public Set<String> findDisableMenusByModel(String deptid, String model)
    {
        Set<String> set = new HashSet<>();
        List<DeptMenuEntity> list =
            this.select().strict(true).eq(F.menuModel, model).eq(F.deptid, deptid).eq(F.enable, false).exec();
        for (DeptMenuEntity e : list)
        {
            set.add(e.getMenu());
        }
        return set;
    }
    
    @SlowQueryLog(timeout = 30000)
    @Transactional(rollbackFor = Exception.class)
    public void updateConfig(String domainid, String orgid, String deptid, String model, String application,
        Collection<String> unselectedMenu)
    {
        List<DeptMenuEntity> dels =
            this.select().strict(true).eq(F.deptid, deptid).eq(F.menuModel, model).eq(F.menuAppid, application).exec();
        List<DeptMenuEntity> list = new ArrayList<>();
        for (String menu : unselectedMenu)
        {
            DeptMenuEntity entity = new DeptMenuEntity();
            entity.setDomainid(domainid);
            entity.setOrgid(orgid);
            entity.setDeptid(deptid);
            entity.setMenuAppid(application);
            entity.setMenuModel(model);
            entity.setMenu(menu);
            entity.setEnable(false);
            entity.setPkey(DeptMenuEntity.genenateKey(deptid, menu));
            list.add(entity);
        }
        this.removeAndPutAll(dels, list);
    }

    public long delByDept(String deptid)
    {
        return this.select().strict(true).eq(F.deptid, deptid).del();
    }
}
