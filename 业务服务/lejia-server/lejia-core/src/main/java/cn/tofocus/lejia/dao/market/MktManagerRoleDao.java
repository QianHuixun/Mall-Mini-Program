package cn.tofocus.lejia.dao.market;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.market.MktManagerRole.F;
import cn.tofocus.lejia.bean.entity.market.MktManagerRole;
import cn.tofocus.lejia.bean.enums.ManagerRole;

@Component
public class MktManagerRoleDao extends JpaSpecificationDelegate<String, MktManagerRole>
{
    public MktManagerRole getRoleCouponManager(Integer key)
    {
        return this.selectOne().eq(F.manager, key).eq(F.role, ManagerRole.COUPON_MANAGER).exec();
    }
    
    public MktManagerRole getRoleOrderManager(Integer key)
    {
        return this.selectOne().eq(F.manager, key).eq(F.role, ManagerRole.ORDER_WRITE_OFF).exec();
    }
    
    public List<MktManagerRole> listRoleCouponManager(List<Integer> keys)
    {
        return this.select().in(F.manager, keys).eq(F.role, ManagerRole.COUPON_MANAGER).exec();
    }
    
    public List<ManagerRole> listRoles(Integer key)
    {
        List<MktManagerRole> list = this.select().eq(F.manager, key).exec();
        List<ManagerRole> res = new ArrayList<>();
        list.forEach(e -> res.add(e.getRole()));
        return res;
    }
    
    public List<MktManagerRole> listRoleManager(Integer key)
    {
        return this.select().eq(F.manager, key).exec();
    }
    
}
