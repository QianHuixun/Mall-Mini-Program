package cn.tofocus.lejia.dao.vendor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorPackingCharge;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorPackingCharge.F;

@Component
public class MktVendorPackingChargeDao extends JpaSpecificationDelegate<Integer, MktVendorPackingCharge>
{
    public List<MktVendorPackingCharge> listByVendor(Integer vendor)
    {
        return this.select().eq(F.vendor, vendor).sort(F.grade).exec();
    }
    
    public Map<Integer,List<MktVendorPackingCharge>> mapByVendors(Set<Integer> vendors)
    {
        List<MktVendorPackingCharge> list = this.select().in(F.vendor, vendors).exec();
        Map<Integer,List<MktVendorPackingCharge>> res = new HashMap<>();
        list.forEach(e -> {
            if(!res.containsKey(e.getVendor()))
                res.put(e.getVendor(), new ArrayList<MktVendorPackingCharge>());
            res.get(e.getVendor()).add(e);
        });
        return res;
    }
    
    public MktVendorPackingCharge byVendorAndInterval(Integer vendor, Integer interval)
    {
        return this.selectOne().eq(F.vendor, vendor).eq(F.grade, interval).exec();
    }
    
}
