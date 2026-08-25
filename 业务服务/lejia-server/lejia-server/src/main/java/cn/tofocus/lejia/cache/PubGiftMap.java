package cn.tofocus.lejia.cache;

import java.time.Duration;

import org.springframework.stereotype.Component;

import cn.tofocus.db.redis.RedisMap;

@Component
public class PubGiftMap extends RedisMap<Integer>
{

    @Override
    protected String cacheName()
    {
        return "zyysc:market:member:pub:gift:linshi";
    }
    
    @Override
    protected Duration defaultTimeout()
    {
        return Duration.ofDays(100);
    }
}
