package cn.tofocus.lejia.dao.vendor;

import org.springframework.stereotype.Component;

import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorPoint;

@Component
public class MktVendorPointDao extends JpaSpecificationDelegate<Integer, MktVendorPoint> {
    public Integer getPoints(Integer pkey) {
        MktVendorPoint vendorPoint = get(pkey);
        if (vendorPoint != null)
            return vendorPoint.getPoints();
        return 0;
    }
}
