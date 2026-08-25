package cn.tofocus.lejia.dao.sys;

import java.util.List;

import org.springframework.stereotype.Component;

import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.sys.SysFarmerPickupLocation;
import cn.tofocus.lejia.bean.entity.sys.SysFarmerPickupLocation.F;

@Component
public class SysFarmerPickupLocationDao extends JpaSpecificationDelegate<Integer, SysFarmerPickupLocation>
{
    public List<SysFarmerPickupLocation> findByFarmer(String farmer, Integer ascription)
    {
        return this.select().eq(F.farmer, farmer).eq(F.ascription, ascription).exec();
    }
    
    public <T> List<T> findByFarmer(String farmer, Integer ascription, Class<T> clazz)
    {
        return this.select().eq(F.farmer, farmer).eq(F.ascription, ascription).execDto(clazz);
    }
}
