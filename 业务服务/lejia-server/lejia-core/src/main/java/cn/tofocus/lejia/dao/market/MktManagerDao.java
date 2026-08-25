package cn.tofocus.lejia.dao.market;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.CollectionUtil;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.market.MktManager;
import cn.tofocus.lejia.bean.entity.market.MktManager.F;
import cn.tofocus.lejia.bean.entity.market.MktManagerRole;

@Component
public class MktManagerDao extends JpaSpecificationDelegate<Integer, MktManager>
{
    @Autowired
    private MktManagerRoleDao managerRoleDao;
    
    public MktManager getByMobileAndFarmer(String mobile, String farmer, Integer ascription)
    {
        MktManager exec = this.selectOne()
            .eq(F.ascription, ascription)
            .eq(F.farmer, farmer)
            .eq(F.mobile, mobile)
            .eq(F.enabled, true)
            .exec();
        if(exec == null)
            return null;
        MktManagerRole roleCouponManager = managerRoleDao.getRoleCouponManager(exec.getPkey());
        if(roleCouponManager == null)
            return null;
        return exec;
    }
    
    public MktManager getByMobileAndFarmerOrder(String mobile, String farmer, Integer ascription)
    {
        MktManager exec = this.selectOne()
            .eq(F.ascription, ascription)
            .eq(F.farmer, farmer)
            .eq(F.mobile, mobile)
            .eq(F.enabled, true)
            .exec();
        if(exec == null)
            return null;
        MktManagerRole roleCouponManager = managerRoleDao.getRoleOrderManager(exec.getPkey());
        if(roleCouponManager == null)
            return null;
        return exec;
    }
    
    public List<String> listAllowedFarmer(String mobile, Integer ascription)
    {
        List<MktManager> list = this.select()
        .eq(F.ascription, ascription)
        .eq(F.mobile, mobile)
        .eq(F.enabled, true)
        .exec();
        if(list == null || list.isEmpty())
            return new ArrayList<>();
        List<Integer> keyList = CollectionUtil.keyList(list);
        List<MktManagerRole> listRoleCouponManager = managerRoleDao.listRoleCouponManager(keyList);
        if(listRoleCouponManager == null || listRoleCouponManager.isEmpty())
            return new ArrayList<>();
        List<Integer> managerKeys = new ArrayList<>();
        listRoleCouponManager.forEach(e -> managerKeys.add(e.getManager()));
        return this.select()
            .in(F.pkey, managerKeys)
            .execDto(F.farmer, String.class);
    }
    
    public MktManager byMobileAndFarmer(String mobile, String farmer)
    {
        return this.selectOne()
        .eq(F.farmer, farmer)
        .eq(F.mobile, mobile)
        .exec();
    }
}
