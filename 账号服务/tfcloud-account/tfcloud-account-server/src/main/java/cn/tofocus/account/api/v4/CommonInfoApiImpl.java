package cn.tofocus.account.api.v4;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.account.bean.application.AppKV;
import cn.tofocus.account.bean.application.DeptModelKv;
import cn.tofocus.account.bean.org.DeptKV;
import cn.tofocus.account.db.cache.application.AppReadCache;
import cn.tofocus.account.db.cache.application.MenuReadCache;
import cn.tofocus.account.db.cache.domain.DomainReadCache;
import cn.tofocus.account.db.cache.org.DeptReadCache;
import cn.tofocus.account.db.cache.org.OrgReadCache;
import cn.tofocus.account.db.cache.role.AppFunctionCache;
import cn.tofocus.account.db.cache.role.AppRoleCache;
import cn.tofocus.account.db.cache.user.UserCache;
import cn.tofocus.core.Result;
import cn.tofocus.core.data.LongKeyName;
import cn.tofocus.core.data.StrKeyName;
import cn.tofocus.db.DataNameBeanRead;
import cn.tofocus.domain.manager.ModelManager;

@RequestMapping("/v4/common")
@RestController
public class CommonInfoApiImpl implements CommonInfoApi
{
    @Autowired
    private DomainReadCache domainCache;
    
    @Autowired
    private AppReadCache appCache;
    
    @Autowired
    private MenuReadCache menuCache;
    
    @Autowired
    private UserCache userCache;
    
    @Autowired
    private OrgReadCache orgCache;
    
    @Autowired
    private DeptReadCache deptCache;
    
    @Autowired
    private AppRoleCache appRoleCache;
    
    @Autowired
    private AppFunctionCache appFunctionCache;
    
    @Autowired
    private ModelManager modelManager;
    
    @Override
    public Result<StrKeyName> getDomain(String key)
    {
        return new Result<>(domainCache.get(key));
    }
    
    @Override
    public Result<List<StrKeyName>> getDomains(List<String> keys)
    {
        return new Result<>(domainCache.get(keys));
    }
    
    @Override
    public Result<AppKV> getApp(String key)
    {
        return new Result<>(appCache.get(key));
    }
    
    @Override
    public Result<List<AppKV>> getApps(List<String> keys)
    {
        return new Result<>(appCache.get(keys));
    }
    
    @Override
    public Result<StrKeyName> getOrg(String key)
    {
        return new Result<>(orgCache.get(key));
    }
    
    @Override
    public Result<List<StrKeyName>> getOrgs(List<String> keys)
    {
        return new Result<>(new ArrayList<>(orgCache.get(keys)));
    }
    
    @Override
    public Result<StrKeyName> getDept(String key)
    {
        return new Result<>(deptCache.get(key));
    }
    
    @Override
    public Result<List<StrKeyName>> getDepts(List<String> keys)
    {
        return new Result<>(new ArrayList<>(deptCache.get(keys)));
    }
    
    @Override
    public Result<StrKeyName> getFunc(String key)
    {
        return new Result<>(getFromCache(appFunctionCache, key));
    }
    
    @Override
    public Result<List<StrKeyName>> getFuncs(List<String> keys)
    {
        return new Result<>(getFromCache(appFunctionCache, keys));
    }
    
    @Override
    public Result<StrKeyName> getRole(String key)
    {
        return new Result<>(getFromCache(appRoleCache, key));
    }
    
    @Override
    public Result<List<StrKeyName>> getRoles(List<String> keys)
    {
        return new Result<>(getFromCache(appRoleCache, keys));
    }
    
    @Override
    public Result<LongKeyName> getUser(Long key)
    {
        String v = userCache.getValueName(key);
        if (v == null)
            return new Result<>();
        else
            return new Result<>(new LongKeyName(key, v));
    }
    
    @Override
    public Result<List<LongKeyName>> getUsers(List<Long> keys)
    {
        List<LongKeyName> result = new ArrayList<>();
        List<String> names = userCache.getValueNames(keys);
        for (int i = 0; i < keys.size(); i++)
        {
            LongKeyName bean = new LongKeyName(keys.get(i), names.get(i));
            result.add(bean);
        }
        return new Result<>(result);
    }
    
    @Override
    public Result<StrKeyName> getMenu(String key)
    {
        return new Result<>(menuCache.get(key));
    }
    
    @Override
    public Result<List<StrKeyName>> getMenus(List<String> keys)
    {
        return new Result<>(menuCache.get(keys));
    }
    
    private StrKeyName getFromCache(DataNameBeanRead<String> cache, String key)
    {
        String v = cache.getValueName(key);
        if (v == null)
            return null;
        else
            return new StrKeyName(key, v);
    }
    
    private List<StrKeyName> getFromCache(DataNameBeanRead<String> cache, List<String> keys)
    {
        List<StrKeyName> result = new ArrayList<>();
        List<String> names = cache.getValueNames(keys);
        for (int i = 0; i < keys.size(); i++)
        {
            StrKeyName bean = new StrKeyName(keys.get(i), names.get(i));
            result.add(bean);
        }
        return result;
    }
    
    @Override
    public Result<DeptModelKv> getDeptModel(String key)
    {
        DeptModelKv kv = new DeptModelKv();
        kv.setPkey(key);
        DeptKV dept = deptCache.get(key);
        if (dept != null)
        {
            kv.setModels(modelManager.activeModels(dept.getDomainid(), dept.getOrgid(), key).keySet());
        }
        return new Result<>(kv);
    }
    
    @Override
    public Result<List<DeptModelKv>> getDeptModels(List<String> keys)
    {
        List<DeptModelKv> l = new ArrayList<>();
        for(String key : keys)
        {
            DeptModelKv kv = new DeptModelKv();
            kv.setPkey(key);
            DeptKV dept = deptCache.get(key);
            if (dept != null)
            {
                kv.setModels(modelManager.activeModels(dept.getDomainid(), dept.getOrgid(), key).keySet());
            }
            l.add(kv);
        }
        return new Result<>(l);
    }
}
