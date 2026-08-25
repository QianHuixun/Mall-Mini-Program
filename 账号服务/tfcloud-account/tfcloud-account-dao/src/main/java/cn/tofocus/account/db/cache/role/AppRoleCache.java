package cn.tofocus.account.db.cache.role;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.TypeReference;

import cn.tofocus.account.db.dao.role.AppRoleDao;
import cn.tofocus.account.db.entity.role.AppRoleEntity;
import cn.tofocus.common.cachemap.DataGroupWriter;
import cn.tofocus.common.cachemap.redis.RedisCacheMap;
import cn.tofocus.common.cachemap.write.WriteCacheItem;
import cn.tofocus.db.DataQuery;
import cn.tofocus.domain.cache.RoleQueryInterface;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AppRoleCache extends RedisCacheMap<String, AppRoleEntity> implements RoleQueryInterface
{
    @Autowired
    private AppRoleDao dao;
    
    @Override
    protected String domain()
    {
        return "tfcloud";
    }
    
    @Override
    protected String cacheName()
    {
        return "role";
    }
    
    @Override
    protected DataGroupWriter<String, AppRoleEntity> dataGroupWriter()
    {
        return dao;
    }
    
    @Override
    protected DataQuery<String, AppRoleEntity> dataQuery()
    {
        return dao;
    }
    
    public AppRoleEntity getAsSystem(String pkey)
    {
        return this.get(pkey);
    }
    
    public boolean isExistRole(String excludeDomainid, Collection<String> pkey)
    {
        List<AppRoleEntity> list = this.get(pkey);
        for (AppRoleEntity r : list)
        {
            if (excludeDomainid.equals(r.getDomainid()))
                continue;
            else
            {
                log.warn("{} 已存在", r.getPkey());
                return true;
            }
        }
        return false;
    }
    
    @Override
    protected TypeReference<WriteCacheItem<String, AppRoleEntity>> writeQueueType()
    {
        return new TypeReference<WriteCacheItem<String, AppRoleEntity>>()
        {
        };
    }
}
