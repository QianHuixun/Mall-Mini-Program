package cn.tofocus.lejia.cache;

import java.time.Duration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import cn.tofocus.db.redis.RedisMap;
import cn.tofocus.lejia.bean.dto.app.linshi.CardLinshiDto;

@Component
public class CardLinshiMap extends RedisMap<CardLinshiDto>
{
    
    public Map<String, CardLinshiDto> findAll()
    {
        Set<String> keys = template.keys(root + "*");
        Set<String> newSet = new HashSet<>();
        for (String key : keys)
        {
            key = key.replace(root, "");
            newSet.add(key);
        }
        return this.mget(newSet);
    }
    
    @Override
    protected String cacheName()
    {
        return "zyysc:market:member:card:activity:linshi";
    }
    
    @Override
    protected Duration defaultTimeout()
    {
        return Duration.ofDays(100);
    }
    
}
