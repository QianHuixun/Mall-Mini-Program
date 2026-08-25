package cn.tofocus.lejia.dao.vendor;

import java.util.List;

import org.springframework.stereotype.Component;

import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.vendor.MktSettlementProcess;

@Component
public class MktSettlementProcessDao extends JpaSpecificationDelegate<Long, MktSettlementProcess>
{
    
    public <T> List<T> findByProcess(Long linePkey, Class<T> clazz)
    {
        return this.select().eq("settlementKey", linePkey).sort("createdTime", false).execDto(clazz);
    }
}