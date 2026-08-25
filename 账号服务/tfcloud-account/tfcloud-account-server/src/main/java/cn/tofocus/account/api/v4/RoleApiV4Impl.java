package cn.tofocus.account.api.v4;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.account.bean.role.RoleAclTree;
import cn.tofocus.account.bean.role.RoleMenuTree;
import cn.tofocus.account.bean.user.app.AppRoleForUpd;
import cn.tofocus.account.bean.user.app.AppRoleInfo;
import cn.tofocus.account.bean.user.app.AppRoleInfoOnPage;
import cn.tofocus.account.db.cache.application.AppReadCache;
import cn.tofocus.account.db.cache.domain.DomainReadCache;
import cn.tofocus.account.db.entity.role.AppRoleEntity;
import cn.tofocus.core.Result;
import cn.tofocus.core.exception.SysErrCode;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.core.security.SecurityContextUtil;
import cn.tofocus.core.security.aop.CheckAppPermission;
import cn.tofocus.core.security.aop.CheckAppPermission.ScopeCheckType;
import cn.tofocus.core.user.SysFunctionEnum;
import cn.tofocus.domain.manager.RoleManager;

@RequestMapping("/v4/role")
@RestController
public class RoleApiV4Impl implements RoleApiV4
{
    private static final String PermFunction = SysFunctionEnum.ManagerRole;
    
    @Autowired
    private RoleManager roleManager;
    
    @Autowired
    private DomainReadCache domainReadCache;

    @Autowired
    private AppReadCache appCache;
    
    @Override
    @CheckAppPermission(value = PermFunction, checkType = ScopeCheckType.current)
    public Result<PageResult<AppRoleInfoOnPage>> queryRole(Integer page, Integer pagesize, String domain,
        boolean onlySysRole)
    {
        PageResult<AppRoleInfoOnPage> data = roleManager.queryRole(page, pagesize, domain, onlySysRole);
        return new Result<>(data);
    }
    
    @Override
    @CheckAppPermission(value = PermFunction)
    public Result<Boolean> addRole(AppRoleInfo info)
    {
        if (!domainReadCache.isExistKey(info.getDomainid()))
            throw TofocusException.of(SysErrCode.REQUIRED_PRARAM_ERROR, info.getDomainid() + " 域不存在");
        AppRoleEntity current = roleManager.getAppRole(info.getPkey());
        if (current != null)
            throw TofocusException.of(SysErrCode.Cache.KEY_IS_EXIST);
        AppRoleEntity newEntity = new AppRoleEntity();
        newEntity.setPkey(info.getPkey());
        newEntity.setDomainid(info.getDomainid());
        newEntity.setName(info.getName());
        newEntity.setDescription(info.getDescription());
        newEntity.setGroup(info.getGroup());
        newEntity.setEnable(info.isEnable());
        newEntity.setOrgid(info.getOrgid());
        newEntity.setDeptid(info.getDeptid());
        SecurityContextUtil.checkRight(PermFunction, newEntity);
        roleManager.saveAppRole(newEntity);
        return new Result<>(true);
    }
    
    @Override
    @CheckAppPermission(value = PermFunction)
    public Result<Boolean> updRole(AppRoleForUpd info)
    {
        AppRoleEntity current = roleManager.getAppRole(info.getPkey());
        if (current == null)
            throw TofocusException.of(SysErrCode.Cache.KEY_IS_NOT_EXIST);
        if (current.getDomainid() == null)
            throw TofocusException.of(SysErrCode.ACCESS_DENIED, "系统角色不能修改");
        SecurityContextUtil.checkRight(PermFunction, current);
        
        current.setName(info.getName());
        current.setDescription(info.getDescription());
        current.setGroup(info.getGroup());
        current.setEnable(info.isEnable());
        
        roleManager.saveAppRole(current);
        return new Result<>(true);
    }

    @Override
    @CheckAppPermission(value = PermFunction)
    public Result<Boolean> enableRole(String pkey, boolean enable)
    {
        AppRoleEntity current = roleManager.getAppRole(pkey);
        if (current == null)
            throw TofocusException.of(SysErrCode.Cache.KEY_IS_NOT_EXIST);
        if (current.getDomainid() == null)
            throw TofocusException.of(SysErrCode.ACCESS_DENIED, "系统角色不能修改");
        SecurityContextUtil.checkRight(PermFunction, current);
        
        current.setEnable(enable);
        
        roleManager.saveAppRole(current);
        return new Result<>(true);
    }
    
    @Override
    @CheckAppPermission(value = PermFunction)
    public Result<String> delRole(String pkey, boolean force)
    {
        AppRoleEntity current = roleManager.getAppRole(pkey);
        if (current != null)
        {
            if (current.getDomainid() == null)
                throw TofocusException.of(SysErrCode.ACCESS_DENIED, "系统角色不能删除");
            SecurityContextUtil.checkRight(PermFunction, current);
            String errMsg = roleManager.delAppRole(pkey, force);
            return new Result<>(errMsg);
        }
        else
            return new Result<>();
    }
    
    @Override
    @CheckAppPermission(value = PermFunction)
    public Result<RoleMenuTree> getRoleMenu(String pkey)
    {
        RoleMenuTree data = roleManager.getRoleMenu(pkey);
        return new Result<>(data);
    }
    
    @Override
    @CheckAppPermission(value = PermFunction)
    public Result<Boolean> setRoleMenu(RoleMenuTree data)
    {
        AppRoleEntity current = roleManager.getAppRole(data.getPkey());
        if (current != null)
        {
            if (current.getDomainid() == null)
                throw TofocusException.of(SysErrCode.ACCESS_DENIED, "系统角色不能修改");
            SecurityContextUtil.checkRight(PermFunction, current);
            roleManager.setRoleMenu(data);
            return new Result<>(true);
        }
        else
            return new Result<>(false);
    }
    
    @Override
    @CheckAppPermission(value = PermFunction)
    public Result<RoleAclTree> getRoleFunc(String pkey, String funcGroup)
    {
        String domain = appCache.currentApp().getDomainid();
        RoleAclTree data = roleManager.getRoleFunc(domain, pkey, funcGroup);
        return new Result<>(data);
    }
    
    @Override
    @CheckAppPermission(value = PermFunction)
    public Result<Boolean> setRoleFunc(RoleAclTree data)
    {
        AppRoleEntity current = roleManager.getAppRole(data.getPkey());
        if (current != null)
        {
            if (current.getDomainid() == null)
                throw TofocusException.of(SysErrCode.ACCESS_DENIED, "系统角色不能修改");
            SecurityContextUtil.checkRight(PermFunction, current);
            roleManager.setRoleFunc(current.getDomainid(), data);
            return new Result<>(true);
        }
        else
            return new Result<>(false);
    }

    @Override
    @CheckAppPermission(value = PermFunction)
    public Result<RoleAclTree> getRoleFuncTree(String pkey, String funcGroup)
    {
        String domain = appCache.currentApp().getDomainid();
        RoleAclTree data = roleManager.getRoleFuncTree(domain, pkey, funcGroup);
        return new Result<>(data);
    }
    
    @Override
    @CheckAppPermission(value = PermFunction)
    public Result<Boolean> setRoleFuncTree(RoleAclTree data)
    {
        AppRoleEntity current = roleManager.getAppRole(data.getPkey());
        if (current != null)
        {
            if (current.getDomainid() == null)
                throw TofocusException.of(SysErrCode.ACCESS_DENIED, "系统角色不能修改");
            SecurityContextUtil.checkRight(PermFunction, current);
            roleManager.setRoleFuncTree(current.getDomainid(), data);
            return new Result<>(true);
        }
        else
            return new Result<>(false);
    }
    
    @Override
    public Result<List<AppRoleInfo>> listRoleInDept(String domain, String group, String deptid)
    {
        List<AppRoleInfo> data = roleManager.listRoleInDept(domain, group, deptid);
        return new Result<>(data);
    }

    
}
