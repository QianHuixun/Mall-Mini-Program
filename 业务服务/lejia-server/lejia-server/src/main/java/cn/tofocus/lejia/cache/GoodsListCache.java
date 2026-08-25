package cn.tofocus.lejia.cache;

import org.springframework.stereotype.Component;

import cn.tofocus.db.redis.RedisQueue;
import cn.tofocus.lejia.bean.dto.goods.GoodsListItem;

@Component
public class GoodsListCache extends RedisQueue<GoodsListItem>
{
    
    @Override
    protected String cacheName()
    {
        return "zyysc:app:goods:type:list";
    }
    
}
