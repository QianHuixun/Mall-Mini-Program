package cn.tofocus.lejia.dao.market;

import java.util.List;

import org.springframework.stereotype.Component;

import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.market.MktSupplierPickupLocation;
import cn.tofocus.lejia.bean.entity.market.MktSupplierPickupLocation.F;

@Component
public class MktSupplierPickupLocationDao extends JpaSpecificationDelegate<Integer, MktSupplierPickupLocation>
{
    public List<MktSupplierPickupLocation> findBySupplier(Integer supplier, Integer ascription)
    {
        return this.select().eq(F.supplier, supplier).eq(F.ascription, ascription).exec();
    }
    
    public <T> List<T> findBySupplier(Integer supplier, Integer ascription, Class<T> clazz)
    {
        return this.select().eq(F.supplier, supplier).eq(F.ascription, ascription).execDto(clazz);
    }
}
