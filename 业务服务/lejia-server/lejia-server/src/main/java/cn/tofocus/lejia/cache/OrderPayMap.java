package cn.tofocus.lejia.cache;

import java.time.Duration;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.TypeReference;

import cn.tofocus.db.redis.RedisMap;

@Component
public class OrderPayMap extends RedisMap<Boolean>
{
    
    @Override
    protected String cacheName()
    {
        return "lejia:order:pay";
    }
    
    @Override
    protected Duration defaultTimeout()
    {
        return Duration.ofDays(1);
    }
    
    /**
     * 泛型必须重载此方法
     * @return
     */
    @Override
    protected TypeReference<Boolean> valueType()
    {
        return new TypeReference<Boolean>()
        {
        };
    }
}
