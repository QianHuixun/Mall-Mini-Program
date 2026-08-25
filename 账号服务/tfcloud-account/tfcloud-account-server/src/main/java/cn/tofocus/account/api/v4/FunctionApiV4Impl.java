package cn.tofocus.account.api.v4;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.account.bean.role.FuncForUpd;
import cn.tofocus.account.bean.role.FuncGroupForUpd;
import cn.tofocus.account.bean.role.FuncGroupInfo;
import cn.tofocus.account.bean.role.FuncInfo;
import cn.tofocus.account.db.cache.domain.DomainReadCache;
import cn.tofocus.account.db.entity.role.AppFunctionEntity;
import cn.tofocus.account.db.entity.role.AppFunctionGroupEntity;
import cn.tofocus.core.Result;
import cn.tofocus.core.data.StrKeyName;
import cn.tofocus.core.exception.SysErrCode;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.core.security.SecurityContextUtil;
import cn.tofocus.core.security.aop.CheckAppPermission;
import cn.tofocus.core.security.aop.CheckAppPermission.ScopeCheckType;
import cn.tofocus.core.user.SysFunctionEnum;
import cn.tofocus.domain.manager.FunctionManager;

@RequestMapping("/v4")
@RestController
public class FunctionApiV4Impl implements FunctionApiV4
{
    @Autowired
    private FunctionManager functionManager;
    
    @Autowired
    private DomainReadCache domainReadCache;
    
    private static final String PermFunction = SysFunctionEnum.ManagerFunction;
    
    @Override
    @CheckAppPermission(value = PermFunction, checkType = ScopeCheckType.current)
    public Result<PageResult<FuncInfo>> queryFunction(Integer page, Integer pagesize, String domain)
    {
        PageResult<FuncInfo> data = functionManager.queryFunc(page, pagesize, domain);
        return new Result<>(data);
    }
    
    @Override
    @CheckAppPermission(value = PermFunction, checkType = ScopeCheckType.current)
    public Result<Boolean> addFunction(@Valid FuncInfo info)
    {
        if (info.getDomainid() == null)
            throw TofocusException.of(SysErrCode.REQUIRED_PRARAM_ERROR, info.getDomainid() + " 域不可为空");
        if (!domainReadCache.isExistKey(info.getDomainid()))
            throw TofocusException.of(SysErrCode.REQUIRED_PRARAM_ERROR, info.getDomainid() + " 域不存在");
        AppFunctionEntity current = functionManager.getFunc(info.getPkey());
        if (current != null)
            throw TofocusException.of(SysErrCode.Cache.KEY_IS_EXIST);
        AppFunctionEntity newEntity = new AppFunctionEntity();
        newEntity.setPkey(info.getPkey());
        newEntity.setDomainid(info.getDomainid());
        newEntity.setName(info.getName());
        newEntity.setDescription(info.getDescription());
        newEntity.setGroup(info.getGroup());
        newEntity.setFuncGroup(info.getFuncGroup());
        newEntity.setSort(info.getSort());
        SecurityContextUtil.checkRight(PermFunction, newEntity);
        functionManager.saveFunc(newEntity);
        return new Result<>(true);
    }
    
    @Override
    @CheckAppPermission(value = PermFunction, checkType = ScopeCheckType.current)
    public Result<Boolean> updFunction(@Valid FuncForUpd info)
    {
        AppFunctionEntity current = functionManager.getFunc(info.getPkey());
        if (current == null)
            throw TofocusException.of(SysErrCode.Cache.KEY_IS_NOT_EXIST);
        if (current.getDomainid() == null)
            throw TofocusException.of(SysErrCode.ACCESS_DENIED, "系统权限不能修改");
        SecurityContextUtil.checkRight(PermFunction, current);
        
        current.setName(info.getName());
        current.setDescription(info.getDescription());
        current.setGroup(info.getGroup());
        current.setFuncGroup(info.getFuncGroup());
        current.setSort(info.getSort());
        
        functionManager.saveFunc(current);
        return new Result<>(true);
    }
    
    @Override
    @CheckAppPermission(value = PermFunction, checkType = ScopeCheckType.current)
    public Result<String> delFunction(String pkey, boolean force)
    {
        AppFunctionEntity current = functionManager.getFunc(pkey);
        if (current != null)
        {
            if (current.getDomainid() == null)
                throw TofocusException.of(SysErrCode.ACCESS_DENIED, "系统权限不能删除");
            SecurityContextUtil.checkRight(PermFunction, current);
            String errMsg = functionManager.delFunc(pkey, force);
            return new Result<>(errMsg);
        }
        else
            return new Result<>();
    }
    
    @Override
    @CheckAppPermission(value = PermFunction, checkType = ScopeCheckType.current)
    public Result<PageResult<FuncGroupInfo>> queryFunctionGroup(Integer page, Integer pagesize, String domain)
    {
        PageResult<FuncGroupInfo> data = functionManager.queryFuncGroup(page, pagesize, domain);
        return new Result<>(data);
    }
    
    @Override
    @CheckAppPermission(value = PermFunction, checkType = ScopeCheckType.current)
    public Result<Boolean> addFunctionGroup(FuncGroupInfo info)
    {
        if (info.getDomainid() == null)
            throw TofocusException.of(SysErrCode.REQUIRED_PRARAM_ERROR, info.getDomainid() + " 域不可为空");
        if (!domainReadCache.isExistKey(info.getDomainid()))
            throw TofocusException.of(SysErrCode.REQUIRED_PRARAM_ERROR, info.getDomainid() + " 域不存在");
        AppFunctionGroupEntity current = functionManager.getFuncGroup(info.getPkey());
        if (current != null)
            throw TofocusException.of(SysErrCode.Cache.KEY_IS_EXIST);
        AppFunctionGroupEntity newEntity = new AppFunctionGroupEntity();
        newEntity.setPkey(info.getPkey());
        newEntity.setDomainid(info.getDomainid());
        newEntity.setName(info.getName());
        newEntity.setGroup(info.getGroup());
        newEntity.setSort(info.getSort());
        SecurityContextUtil.checkRight(PermFunction, newEntity);
        functionManager.saveFuncGroup(newEntity);
        return new Result<>(true);
    }
    
    @Override
    @CheckAppPermission(value = PermFunction, checkType = ScopeCheckType.current)
    public Result<Boolean> updFunctionGroup(FuncGroupForUpd info)
    {
        AppFunctionGroupEntity current = functionManager.getFuncGroup(info.getPkey());
        if (current == null)
            throw TofocusException.of(SysErrCode.Cache.KEY_IS_NOT_EXIST);
        if (current.getDomainid() == null)
            throw TofocusException.of(SysErrCode.ACCESS_DENIED, "系统权限不能修改");
        SecurityContextUtil.checkRight(PermFunction, current);
        
        current.setName(info.getName());
        current.setGroup(info.getGroup());
        current.setSort(info.getSort());
        
        functionManager.saveFuncGroup(current);
        return new Result<>(true);
    }
    
    @Override
    @CheckAppPermission(value = PermFunction, checkType = ScopeCheckType.current)
    public Result<String> delFunctionGroup(String pkey)
    {
        AppFunctionGroupEntity current = functionManager.getFuncGroup(pkey);
        if (current != null)
        {
            if (current.getDomainid() == null)
                throw TofocusException.of(SysErrCode.ACCESS_DENIED, "系统权限组不能删除");
            SecurityContextUtil.checkRight(PermFunction, current);
            String errMsg = functionManager.delFuncGroup(pkey);
            return new Result<>(errMsg);
        }
        else
            return new Result<>();
    }
    
    @Override
    @CheckAppPermission(value = PermFunction, checkType = ScopeCheckType.current)
    public Result<List<StrKeyName>> listFunctionGroup(String domain, String group)
    {
        List<StrKeyName> data = functionManager.listFunctionGroup(domain, group);
        return new Result<>(data);
    }
    
}
