package cn.tofocus.lejia.dao.vendor;

import java.util.List;

import org.springframework.stereotype.Component;

import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorBoutique;

@Component
public class MktVendorBoutiqueDao extends JpaSpecificationDelegate<Integer, MktVendorBoutique>
{
    public List<MktVendorBoutique> listVendorAndFarmer(Integer vendor, String farmer)
    {
        return this.select().eq("vendor", vendor).eq("farmer", farmer).sort("sort", false).exec();
    }
    
    public MktVendorBoutique byVendorAndFarmer(Integer vendor, String farmer)
    {
        return this.selectOne().eq("vendor", vendor).eq("farmer", farmer).exec();
    }
    
    public MktVendorBoutique byVendorAndFarmerNotPkey(Integer vendor, String farmer, Integer pkey)
    {
        return this.selectOne().eq("vendor", vendor).eq("farmer", farmer).notEq("pkey", pkey).exec();
    }
    
}