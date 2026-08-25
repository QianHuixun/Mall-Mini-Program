package cn.tofocus.lejia.dao.goods;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.goods.MktSpaceKc;

@Component
public class MktSpaceKcDao extends JpaSpecificationDelegate<Integer, MktSpaceKc>
{
    public Map<Integer,MktSpaceKc> mapKc(List<Integer> keys)
    {
        Map<Integer,MktSpaceKc> res = new HashMap<>();
        if(keys == null || keys.isEmpty())
            return res;
        List<MktSpaceKc> list = this.select().in("pkey", keys.toArray()).exec();
        list.forEach(e -> {
            res.put(e.getPkey(), e);
        });
        return res;
    }
    
    public Map<Integer, MktSpaceKc> mapKc(Integer ascription)
    {
        Map<Integer, MktSpaceKc> res = new HashMap<>();
        List<MktSpaceKc> list = this.select().eq("ascription", ascription).exec();
        list.forEach(e -> res.put(e.getPkey(), e));
        return res;
    }
}