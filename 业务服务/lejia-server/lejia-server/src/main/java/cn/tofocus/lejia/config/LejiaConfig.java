package cn.tofocus.lejia.config;

import org.springframework.stereotype.Component;

import cn.tofocus.db.redis.RedisMap;

@Component
public class LejiaConfig extends RedisMap<String>
{

    @Override
    protected String cacheName()
    {
        return "zyyscConfig";
    }
    
}
