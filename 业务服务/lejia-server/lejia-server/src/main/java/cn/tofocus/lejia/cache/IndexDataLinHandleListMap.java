package cn.tofocus.lejia.cache;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.TypeReference;

import cn.tofocus.db.redis.RedisMap;

@Component
public class IndexDataLinHandleListMap extends RedisMap<List<Map<String, Object>>>
{

    @Override
    protected String cacheName()
    {
        return "zyysc:market:index:data:lindao:linshi:list";
    }
    @Override
    protected Duration defaultTimeout()
    {
        return Duration.ofDays(100);
    }
    
    @Override
    protected TypeReference<List<Map<String, Object>>> valueType()
    {
        return new TypeReference<List<Map<String, Object>>>()
        {
        };
    }
}
