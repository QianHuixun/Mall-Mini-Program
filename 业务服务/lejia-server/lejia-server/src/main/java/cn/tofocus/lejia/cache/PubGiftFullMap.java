package cn.tofocus.lejia.cache;

import java.time.Duration;

import org.springframework.stereotype.Component;

import cn.tofocus.db.redis.RedisMap;
import cn.tofocus.lejia.bean.dto.PubGiftFullDto;

@Component
public class PubGiftFullMap extends RedisMap<PubGiftFullDto>
{

    @Override
    protected String cacheName()
    {
        return "zyysc:market:member:pub:gift:full:linshi";
    }
    
    @Override
    protected Duration defaultTimeout()
    {
        return Duration.ofDays(100);
    }
}
