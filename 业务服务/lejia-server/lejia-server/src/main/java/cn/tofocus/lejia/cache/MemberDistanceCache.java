package cn.tofocus.lejia.cache;

import java.time.Duration;
import java.util.Date;

import org.springframework.stereotype.Component;

import cn.tofocus.db.redis.RedisMap;

@Component
public class MemberDistanceCache extends RedisMap<Date>
{
    @Override
    protected String cacheName()
    {
        return "zyysc:member:app:index:distance";
    }
    
    @Override
    protected Duration defaultTimeout()
    {
        return Duration.ofDays(1);
    }
}
