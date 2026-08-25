package cn.tofocus.account.db.cache.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.TypeReference;

import cn.tofocus.account.db.dao.user.AccessInstanceDao;
import cn.tofocus.account.db.entity.user.AccessInstance;
import cn.tofocus.common.cachemap.DataGroupWriter;
import cn.tofocus.common.cachemap.redis.RedisCacheMap;
import cn.tofocus.common.cachemap.write.WriteCacheItem;
import cn.tofocus.db.DataQuery;

@Component
public class UserAclCache extends RedisCacheMap<String, AccessInstance>
{
    @Autowired
    private AccessInstanceDao dao;
    
    @Override
    protected String domain()
    {
        return "tfcloud";
    }
    
    @Override
    protected String cacheName()
    {
        return "useracl";
    }
    
    @Override
    protected DataQuery<String, AccessInstance> dataQuery()
    {
        return dao;
    }
    
    @Override
    protected DataGroupWriter<String, AccessInstance> dataGroupWriter()
    {
        return dao;
    }
    
    @Override
    protected TypeReference<WriteCacheItem<String, AccessInstance>> writeQueueType()
    {
        return new TypeReference<WriteCacheItem<String, AccessInstance>>()
        {
        };
    }
    
    public boolean isFuncUsed(String funcid)
    {
        return dao.isFuncUsed(funcid);
    }
    
}
