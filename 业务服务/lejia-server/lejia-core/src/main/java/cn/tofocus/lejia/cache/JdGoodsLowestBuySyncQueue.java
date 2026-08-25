package cn.tofocus.lejia.cache;

import cn.tofocus.db.redis.RedisQueue;
import cn.tofocus.lejia.bean.dto.jd.JdGoodsLowestBuy;
import org.springframework.stereotype.Component;

@Component
public class JdGoodsLowestBuySyncQueue extends RedisQueue<JdGoodsLowestBuy> {
    @Override
    protected String cacheName() {
        return "lejia:jd:goods:lowestBy:sync:queue";
    }
}
