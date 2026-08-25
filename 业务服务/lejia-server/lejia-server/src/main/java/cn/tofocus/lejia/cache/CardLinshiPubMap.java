package cn.tofocus.lejia.cache;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.TypeReference;

import cn.tofocus.db.redis.RedisMap;

@Component
public class CardLinshiPubMap extends RedisMap<List<Integer>>
{
    
    @Override
    protected String cacheName()
    {
        return "zyysc:market:member:card:activity:linshi:pub";
    }
    
    @Override
    protected Duration defaultTimeout()
    {
        return Duration.ofDays(100);
    }
    
    @Override
    protected TypeReference<List<Integer>> valueType()
    {
        return new TypeReference<List<Integer>>()
        {
        };
    }
    
}
