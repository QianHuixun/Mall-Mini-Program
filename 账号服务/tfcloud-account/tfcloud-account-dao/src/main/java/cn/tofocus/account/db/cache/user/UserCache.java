package cn.tofocus.account.db.cache.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.TypeReference;

import cn.tofocus.account.db.dao.user.UserDao;
import cn.tofocus.account.db.entity.user.UserEntity;
import cn.tofocus.common.cachemap.DataGroupWriter;
import cn.tofocus.common.cachemap.redis.RedisCacheMap;
import cn.tofocus.common.cachemap.write.WriteCacheItem;
import cn.tofocus.core.exception.SysErrCode;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.db.DataQuery;
import cn.tofocus.domain.cache.UserQueryInterface;

@Component
public class UserCache extends RedisCacheMap<Long, UserEntity> implements UserQueryInterface
{
    @Override
    protected String domain()
    {
        return "tfcloud";
    }
    
    @Override
    protected String cacheName()
    {
        return "user";
    }
    
    @Autowired
    private UserDao dao;
    
    /****************************
     * 
     *       远端数据操作
     *       
     ***************************/
    
    @Override
    protected DataGroupWriter<Long, UserEntity> dataGroupWriter()
    {
        return dao;
    }
    
    @Override
    protected DataQuery<Long, UserEntity> dataQuery()
    {
        return dao;
    }
    
    @Override
    public UserEntity add(UserEntity value)
    {
        dao.generateID(value);
        if (value.getUserid() == null)
            value.setUserid("tf_" + value.getPkey());
        if (value.getNickname() == null)
            value.setNickname("tf_" + value.getPkey());
        return super.add(value);
    }
    
    @Override
    protected TypeReference<WriteCacheItem<Long, UserEntity>> writeQueueType()
    {
        return new TypeReference<WriteCacheItem<Long, UserEntity>>()
        {
        };
    }
    
    public UserEntity getForUpdate(String domain, Long userkey)
    {
        UserEntity user = this.get(userkey);
        if (user != null && !domain.equals(user.getRegistFromDomain()))
        {
            throw TofocusException.of(SysErrCode.ACCESS_DENIED, "用户属于[" + user.getRegistFromDomain() + "]域，不能修改");
        }
        return user;
    }
    
}
