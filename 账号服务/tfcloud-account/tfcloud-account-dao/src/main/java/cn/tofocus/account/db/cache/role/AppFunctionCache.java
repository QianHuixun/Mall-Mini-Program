package cn.tofocus.account.db.cache.role;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.TypeReference;

import cn.tofocus.account.bean.role.FuncInfo;
import cn.tofocus.account.db.dao.role.AppFunctionDao;
import cn.tofocus.account.db.entity.role.AppFunctionEntity.F;
import cn.tofocus.account.db.entity.role.AppFunctionEntity;
import cn.tofocus.common.Constant;
import cn.tofocus.common.cachemap.DataGroupWriter;
import cn.tofocus.common.cachemap.redis.RedisCacheMap;
import cn.tofocus.common.cachemap.write.WriteCacheItem;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.DataQuery;
import cn.tofocus.domain.cache.FuncQueryInterface;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AppFunctionCache extends RedisCacheMap<String, AppFunctionEntity> implements FuncQueryInterface
{
    @Autowired
    private AppFunctionDao dao;
    
    @Override
    protected String domain()
    {
        return "tfcloud";
    }
    
    @Override
    protected String cacheName()
    {
        return "func";
    }
    
    @Override
    protected DataQuery<String, AppFunctionEntity> dataQuery()
    {
        return dao;
    }
    
    @Override
    protected DataGroupWriter<String, AppFunctionEntity> dataGroupWriter()
    {
        return dao;
    }
    
    public AppFunctionEntity getAsSystem(String key)
    {
        return this.get(key);
    }
    
    public boolean isExistFunc(String excludeDomainid, Set<String> pkey)
    {
        List<AppFunctionEntity> list = this.get(pkey);
        for (AppFunctionEntity r : list)
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
    protected TypeReference<WriteCacheItem<String, AppFunctionEntity>> writeQueueType()
    {
        return new TypeReference<WriteCacheItem<String, AppFunctionEntity>>()
        {
        };
    }
    
    public PageResult<FuncInfo> queryFunc(Integer page, Integer pagesize, String domain)
    {
        if (Constant.NULLID.equals(domain))
            return dao.selectPage()
                .page(page)
                .pagesize(pagesize)
                .isNull(F.domainid)
                .sort(F.group)
                .sort(F.funcGroup)
                .sort(F.sort)
                .execDto(FuncInfo.class);
        else
            return dao.selectPage()
                .page(page)
                .pagesize(pagesize)
                .strict(true)
                .eq(F.domainid, domain)
                .sort(F.group)
                .sort(F.funcGroup)
                .sort(F.sort)
                .execDto(FuncInfo.class);
    }
    
    public List<FuncInfo> listFunc(String domain, String group)
    {
        if (domain == null)
            return Collections.emptyList();
        if (Constant.NULLID.equals(domain))
            return dao.select().isNull(F.domainid).sort(F.group).sort(F.funcGroup).sort(F.sort).execDto(FuncInfo.class);
        else
            return dao.select()
                .eq(F.domainid, domain)
                .eq(F.group, group)
                .sort(F.group)
                .sort(F.funcGroup)
                .sort(F.sort)
                .execDto(FuncInfo.class);
    }
    
    public long countByFuncGroup(String domain, String funcGroup)
    {
        if (Constant.NULLID.equals(domain))
            return dao.aggregation().isNull(F.domainid).eq(F.funcGroup, funcGroup).execCount();
        else
            return dao.aggregation().eq(F.domainid, domain).eq(F.funcGroup, funcGroup).execCount();
    }
    
}
