package cn.tofocus.lejia.cache;

import java.time.Duration;

import org.springframework.stereotype.Component;

import cn.tofocus.db.redis.RedisMap;
import cn.tofocus.lejia.bean.dto.GtypeCorresponding;

@Component
public class GoodsMainThreeLinshiMap extends RedisMap<GtypeCorresponding>
{
    
    @Override
    protected String cacheName()
    {
        return "lejia:gtype:goodsMainThree:linshi";
    }
    
    @Override
    protected Duration defaultTimeout()
    {
        return Duration.ofDays(1);
    }
}