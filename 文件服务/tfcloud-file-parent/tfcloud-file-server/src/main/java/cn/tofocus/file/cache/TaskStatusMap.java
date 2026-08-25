package cn.tofocus.file.cache;

import org.springframework.stereotype.Component;

import cn.tofocus.db.redis.RedisMap;
import cn.tofocus.file.Constant;

@Component
public class TaskStatusMap extends RedisMap<String>
{
    
    @Override
    protected String cacheName()
    {
        return Constant.DOMAIN + ":file:task";
    }
    
}
