package cn.tofocus.account.db.cache.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.TypeReference;

import cn.tofocus.account.db.dao.user.RoleInstanceDao;
import cn.tofocus.account.db.entity.user.RoleInstance;
import cn.tofocus.common.cachemap.DataGroupWriter;
import cn.tofocus.common.cachemap.redis.RedisCacheMap;
import cn.tofocus.common.cachemap.write.WriteCacheItem;
import cn.tofocus.db.DataQuery;

@Component
public class UserRoleCache extends RedisCacheMap<String, RoleInstance>
{
    @Autowired
    private RoleInstanceDao dao;
    
    @Override
    protected String domain()
    {
        return "tfcloud";
    }
    
    @Override
    protected String cacheName()
    {
        return "userrole";
    }
    
    @Override
    protected DataQuery<String, RoleInstance> dataQuery()
    {
        return dao;
    }
    
    @Override
    protected DataGroupWriter<String, RoleInstance> dataGroupWriter()
    {
        return dao;
    }
    
    @Override
    protected TypeReference<WriteCacheItem<String, RoleInstance>> writeQueueType()
    {
        return new TypeReference<WriteCacheItem<String, RoleInstance>>()
        {
        };
    }
    
}
