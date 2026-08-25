package cn.tofocus.account.db.cache.role;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.TypeReference;

import cn.tofocus.account.db.dao.role.RoleAccessDao;
import cn.tofocus.account.db.entity.role.RoleAccessInstance;
import cn.tofocus.common.cachemap.DataGroupWriter;
import cn.tofocus.common.cachemap.redis.RedisCacheMap;
import cn.tofocus.common.cachemap.write.WriteCacheItem;
import cn.tofocus.db.DataQuery;

@Component
public class RoleAclCache extends RedisCacheMap<String, RoleAccessInstance>
{
    @Autowired
    private RoleAccessDao dao;
    
    @Override
    protected String domain()
    {
        return "tfcloud";
    }
    
    @Override
    protected String cacheName()
    {
        return "roleacl";
    }
    
    @Override
    protected DataQuery<String, RoleAccessInstance> dataQuery()
    {
        return dao;
    }
    
    @Override
    protected DataGroupWriter<String, RoleAccessInstance> dataGroupWriter()
    {
        return dao;
    }
    
    @Override
    protected TypeReference<WriteCacheItem<String, RoleAccessInstance>> writeQueueType()
    {
        return new TypeReference<WriteCacheItem<String, RoleAccessInstance>>()
        {
        };
    }
    
    public boolean isFuncUsed(String funcid)
    {
        return dao.isFuncUsed(funcid);
    }
    
}
