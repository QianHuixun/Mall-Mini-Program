package cn.tofocus.lejia.cache;

import org.springframework.stereotype.Component;

import cn.tofocus.db.redis.RedisHash;
import cn.tofocus.lejia.bean.dto.goods.GoodsListItemIndex;

@Component
public class GoodsListCacheIndex extends RedisHash<String, GoodsListItemIndex>
{

    @Override
    protected String cacheName()
    {
        return "zyysc:app:goods:type:listIndex";
    }
    
}
