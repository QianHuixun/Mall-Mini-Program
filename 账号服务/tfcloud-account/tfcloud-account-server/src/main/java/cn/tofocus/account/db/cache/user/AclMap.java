package cn.tofocus.account.db.cache.user;

import java.time.Duration;
import java.util.HashMap;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.TypeReference;

import cn.tofocus.core.security.AccessList;
import cn.tofocus.db.redis.RedisMap;

@Component
public class AclMap extends RedisMap<HashMap<String, AccessList>>
{

    @Override
    protected String cacheName()
    {
        return "acc:acl";
    }

    @Override
    protected TypeReference<HashMap<String, AccessList>> valueType()
    {
        return new TypeReference<HashMap<String, AccessList>>(){};
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
