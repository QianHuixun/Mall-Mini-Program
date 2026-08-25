package cn.tofocus.lejia.cache;

import java.time.Duration;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.TypeReference;

import cn.tofocus.db.redis.RedisMap;

@Component
public class IndexDataLinHandleMap extends RedisMap<Map<String, Object>>
{
    @Override
    protected String cacheName()
    {
        return "zyysc:market:index:data:lindao:linshi:map";
    }
    
    @Override
    protected Duration defaultTimeout()
    {
        return Duration.ofDays(100);
    }
    
    @Override
    protected TypeReference<Map<String, Object>> valueType()
    {
        return new TypeReference<Map<String, Object>>()
        {
        };
    }
    
}
