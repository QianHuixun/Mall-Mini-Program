package cn.tofocus.account.db.dao.role;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import cn.tofocus.account.db.entity.role.RoleMenuEntity.F;
import cn.tofocus.account.db.entity.role.RoleMenuEntity;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;

@Component
public class RoleMenuDao extends JpaSpecificationDelegate<String, RoleMenuEntity>
{

    public List<RoleMenuEntity> listByRole(String roleid)
    {
        return this.select().strict(true).eq(F.ownerid, roleid).exec();
    }

    public List<RoleMenuEntity> listByDomain(String domain)
    {
        return this.select().strict(true).eq(F.domainid, domain).exec();
    }

    public Map<String, Long> countRoleByMenu(String menu)
    {
        return this.aggregation().eq(F.menu, menu).execGroupByCountDistinct(F.ownerid, F.ownerid);
    }

    public void delByMenu(String menu)
    {
        this.select().strict(true).eq(F.menu, menu).del();
    }

    public void delByRole(String role)
    {
        this.select().strict(true).eq(F.ownerid, role).del();
    }
}
