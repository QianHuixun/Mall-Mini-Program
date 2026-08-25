package cn.tofocus.account.db.cache.user;

import java.time.Duration;
import java.util.HashMap;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.TypeReference;

import cn.tofocus.db.redis.RedisMap;

@Component
public class UserMenuMap extends RedisMap<HashMap<String, Boolean>>
{

    @Override
    protected String cacheName()
    {
        return "acc:userMenu";
    }

    @Override
    protected TypeReference<HashMap<String, Boolean>> valueType()
    {
        return new TypeReference<HashMap<String, Boolean>>(){};
    }

    @Override
    protected Duration defaultTimeout()
    {
        return Duration.ofDays(7);
    }

    @Override
    public Duration getDefaultTimeout()
    {
        return super.getDefaultTimeout();
    }
    
}
