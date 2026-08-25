package cn.tofocus.lejia.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.common.cachemap.redis.write.Balance;
import cn.tofocus.common.cachemap.redis.write.BalanceWriteCache;
import cn.tofocus.lejia.bean.entity.goods.MktSpaceKc;
import cn.tofocus.lejia.dao.goods.MktSpaceKcDao;

@Component
public class SpaceKcCache extends BalanceWriteCache
{
    @Autowired
    private MktSpaceKcDao dao;
    
    @Override
    protected String domain()
    {
        return "zyysc";
    }
    
    @Override
    protected String cacheName()
    {
        return "goods:space:kc";
    }
    
    @Override
    protected void saveBalance(List<Balance> list)
    {
        List<Integer> keys = new ArrayList<>();
        List<MktSpaceKc> kcList = new ArrayList<>();
        for (Balance b : list)
        {
            keys.add(Integer.valueOf(b.getPkey()));
        }
        if(keys.isEmpty())
            return;
        Map<Integer, MktSpaceKc> mapKc = dao.mapKc(keys);
        for (Balance b : list)
        {
            Integer key = Integer.valueOf(b.getPkey());
            if(mapKc.containsKey(key))
            {
                MktSpaceKc kc = mapKc.get(key);
                kc.setKcNum(kc.getKcNum() + b.getBalance().intValue());
                kcList.add(kc);
            }
        }
        dao.putAll(kcList);
    }
    
    @Override
    protected void delBalance(List<String> keys)
    {
        if (keys != null && !keys.isEmpty())
        {
            List<Integer> kcKeys = new ArrayList<>();
            keys.forEach(e -> {
                kcKeys.add(Integer.valueOf(e));
            });
            dao.removeAllById(kcKeys);
        }
    }
    
}
