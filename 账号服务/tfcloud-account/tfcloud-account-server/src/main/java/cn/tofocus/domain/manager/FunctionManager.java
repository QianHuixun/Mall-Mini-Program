package cn.tofocus.domain.manager;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.account.bean.role.FuncGroupInfo;
import cn.tofocus.account.bean.role.FuncInfo;
import cn.tofocus.account.db.cache.role.AppFunctionCache;
import cn.tofocus.account.db.dao.role.AppFunctionGroupDao;
import cn.tofocus.account.db.entity.role.AppFunctionEntity;
import cn.tofocus.account.db.entity.role.AppFunctionGroupEntity;
import cn.tofocus.core.data.StrKeyName;
import cn.tofocus.core.exception.SysErrCode;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageResult;

@Component
public class FunctionManager
{
    @Autowired
    private AppFunctionCache appFunctionCache;
    
    @Autowired
    private AppFunctionGroupDao appFunctionGroupDao;
    
    @Autowired
    private UserPermissionManager userPermissionManager;
    
    /**************************
     * 
     *    应用权限管理
     * 
     **************************/
    
    public void saveFunc(AppFunctionEntity func)
    {
        appFunctionCache.put(func);
    }
    
    public AppFunctionEntity getFunc(String funcid)
    {
        return appFunctionCache.get(funcid);
    }
    
    public boolean isExistFunc(String excludeDomainid, Set<String> funcKeySet)
    {
        return appFunctionCache.isExistFunc(excludeDomainid, funcKeySet);
    }
    
    public Set<String> allFuncKey()
    {
        return appFunctionCache.keys();
    }
    
    public PageResult<FuncInfo> queryFunc(Integer page, Integer pagesize, String domain)
    {
        return appFunctionCache.queryFunc(page, pagesize, domain);
    }
    
    public List<FuncInfo> listFunc(String domain, String group)
    {
        return appFunctionCache.listFunc(domain, group);
    }

    public String delFunc(String funcid, boolean force)
    {
        if (userPermissionManager.isFuncUsed(funcid))
            throw TofocusException.of(SysErrCode.ACCESS_DENIED, "权限已被使用，不能删除");
        else
            appFunctionCache.removeById(funcid);
        return null;
    }
    
    public PageResult<FuncGroupInfo> queryFuncGroup(Integer page, Integer pagesize, String domain)
    {
        return appFunctionGroupDao.queryFuncGroup(page, pagesize, domain);
    }

    public List<StrKeyName> listFunctionGroup(String domain, String group)
    {
        return appFunctionGroupDao.listFunctionGroup(domain, group);
    }

    public AppFunctionGroupEntity getFuncGroup(String pkey)
    {
        return appFunctionGroupDao.get(pkey);
    }

    public void saveFuncGroup(AppFunctionGroupEntity newEntity)
    {
        appFunctionGroupDao.put(newEntity);
    }

    public String delFuncGroup(String pkey)
    {
        AppFunctionGroupEntity g = appFunctionGroupDao.get(pkey);
        if(g!= null)
        {
            if (appFunctionCache.countByFuncGroup(g.getDomainid(), g.getPkey()) > 0)
                throw TofocusException.of(SysErrCode.ACCESS_DENIED, "权限组已被使用，不能删除");
            else
                appFunctionGroupDao.removeById(pkey);
        }
        return null;
    }
    
}
