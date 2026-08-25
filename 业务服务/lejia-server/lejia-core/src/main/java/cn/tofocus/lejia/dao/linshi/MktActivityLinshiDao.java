package cn.tofocus.lejia.dao.linshi;

import java.util.List;

import org.springframework.stereotype.Component;

import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.linshi.MktActivityLinshi;

@Deprecated
@Component
public class MktActivityLinshiDao extends JpaSpecificationDelegate<Integer, MktActivityLinshi>
{
    public List<MktActivityLinshi> listMktActivityLinshi(List<Integer> inVendorKeys)
    {
        return this.select().in("vendor", inVendorKeys).exec();
    }
    
    public MktActivityLinshi getMktActivityLinshi(Integer vendorKey)
    {
        return this.selectOne().eq("vendor", vendorKey).exec();
    }
}
