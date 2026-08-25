package cn.tofocus.lejia.dao.zx;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.zx.ThirdPayLineEntity;
import cn.tofocus.lejia.bean.entity.zx.ThirdPayLineEntity.F;

@Component
public class ThirdPayLineDao extends JpaSpecificationDelegate<Integer, ThirdPayLineEntity>
{
    public Map<String, String> tranMap(String day)
    {
        Map<String, String> map = new HashMap<>();
        List<ThirdPayLineEntity> list = this.select().between("createTime", day + " 00:00:00",  day + " 23:59:59")
        .exec();
        list.forEach(e -> map.put(e.getMerOrderId(), e.getSeqId()));
        return map;
    }
    
    public ThirdPayLineEntity byMerOrderId(String merOrderId)
    {
    	return this.selectOne().eq(F.merOrderId, "3EY5" + merOrderId).exec();
    }
}
