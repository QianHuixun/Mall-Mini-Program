package cn.tofocus.lejia.dao.vendor;

import org.springframework.stereotype.Component;

import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorOrderPackingCharge;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorOrderPackingCharge.F;

@Component
public class MktVendorOrderPackingChargeDao extends JpaSpecificationDelegate<Integer, MktVendorOrderPackingCharge>
{
    public MktVendorOrderPackingCharge byOrderAndVendor(Integer order, Integer vendor)
    {
        return this.selectOne().eq(F.orderPkey, order).eq(F.vendor, vendor).exec();
    }
}
