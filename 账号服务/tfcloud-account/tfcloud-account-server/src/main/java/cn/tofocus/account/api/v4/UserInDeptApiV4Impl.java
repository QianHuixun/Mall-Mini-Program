package cn.tofocus.account.api.v4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.account.bean.AppMenu;
import cn.tofocus.account.bean.application.AppKV;
import cn.tofocus.account.bean.org.DeptKV;
import cn.tofocus.account.db.cache.application.AppReadCache;
import cn.tofocus.account.db.cache.org.DeptReadCache;
import cn.tofocus.account.db.cache.role.AppFunctionCache;
import cn.tofocus.account.db.cache.role.AppRoleCache;
import cn.tofocus.account.db.dao.user.RoleInstanceDao;
import cn.tofocus.account.db.entity.role.AppFunctionEntity;
import cn.tofocus.account.db.entity.role.AppRoleEntity;
import cn.tofocus.account.db.entity.user.RoleInstance;
import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.core.Result;
import cn.tofocus.core.data.StrKeyName;
import cn.tofocus.core.exception.SysErrCode;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.security.AccessList;
import cn.tofocus.core.security.AccessScopeType;
import cn.tofocus.core.security.aop.CheckAppPermission;
import cn.tofocus.core.user.SysFunctionEnum;
import cn.tofocus.domain.manager.MenuManager;
import cn.tofocus.domain.manager.UserPermissionManager;
import cn.tofocus.domain.user.role.RoleInfo;

@RequestMapping("/v4/userInDept")
@RestController
public class UserInDeptApiV4Impl implements UserInDeptApiV4
{
    private static final String PermFunction = SysFunctionEnum.ManagerUser;
    
    @Autowired
    private MenuManager menuManager;
    
    @Autowired
    private DeptReadCache deptCache;
    
    @Autowired
    private AppReadCache appCache;
    
    @Autowired
    private UserPermissionManager userPermissionManager;
    
    @Autowired
    private AppFunctionCache functionCache;
    
    @Autowired
    private AppRoleCache roleCache;
    
    @Autowired
    private RoleInstanceDao roleInstanceDao;
    
    @Override
    public Result<List<RoleInfo>> listUserRole(Long userkey, String deptid)
    {
        List<RoleInfo> list = new ArrayList<>();
        DeptKV dept = deptCache.get(deptid);
        List<RoleInstance> l = userPermissionManager.getUserRole(userkey);
        Set<String> keys = new HashSet<>();
        for (RoleInstance r : l)
        {
            if ((AccessScopeType.domain == r.getScopeType() && r.getScope().equals(dept.getDomainid()))
                || (AccessScopeType.org == r.getScopeType() && r.getScope().equals(dept.getOrgid()))
                || (AccessScopeType.dept == r.getScopeType() && r.getScope().equals(deptid)))
            {
                if(!keys.contains(r.getValue()))
                {
                    keys.add(r.getValue());
                    AppRoleEntity role = roleCache.get(r.getValue());
                    if (role != null)
                        list.add(BeanUtil.beanFrom(RoleInfo.class, role));
                }
            }
        }
        return new Result<>(list);
    }

    @Override
    public Result<Map<Long, List<RoleInfo>>> mapUserRole(List<Long> userkeys, String deptid)
    {
        DeptKV dept = deptCache.get(deptid);
        Map<Long, List<RoleInfo>> map = new HashMap<>();
        for (Long userkey : userkeys)
        {
            List<RoleInfo> list = map.computeIfAbsent(userkey, e -> new ArrayList<>());
            List<RoleInstance> l = userPermissionManager.getUserRole(userkey);
            Set<String> keys = new HashSet<>();
            for (RoleInstance r : l)
            {
                if ((AccessScopeType.domain == r.getScopeType() && r.getScope().equals(dept.getDomainid()))
                    || (AccessScopeType.org == r.getScopeType() && r.getScope().equals(dept.getOrgid()))
                    || (AccessScopeType.dept == r.getScopeType() && r.getScope().equals(deptid)))
                {
                    if(!keys.contains(r.getValue()))
                    {
                        keys.add(r.getValue());
                        AppRoleEntity role = roleCache.get(r.getValue());
                        if (role != null)
                            list.add(BeanUtil.beanFrom(RoleInfo.class, role));
                    }
                }
            }
        }
        return new Result<>(map);
    }
    
    @Override
    public Result<List<StrKeyName>> listUserAcl(Long userkey, String deptid)
    {
        List<StrKeyName> list = new ArrayList<>();
        DeptKV dept = deptCache.get(deptid);
        Map<String, AccessList> aclmap = userPermissionManager.getCachedUserAcls(userkey);
        for (AccessList acl : aclmap.values())
        {
            if (acl.canAccessDomain(dept.getDomainid()) || acl.canAccessOrg(dept.getOrgid())
                || acl.canAccessDept(deptid))
            {
                AppFunctionEntity func = functionCache.get(acl.getFuncKey());
                if (func != null)
                    list.add(new StrKeyName(func.getPkey(), func.getName()));
            }
        }
        return new Result<>(list);
    }
    
    @Override
    public Result<List<AppMenu>> listUserMenu(Long userkey, String application, String deptid)
    {
        AppKV app = appCache.get(application);
        DeptKV dept = deptCache.get(deptid);
        if (!app.getDomainid().equals(dept.getDomainid()))
            throw TofocusException.of(SysErrCode.REQUIRED_PRARAM_ERROR);
        List<AppMenu> top =
            menuManager.findMyMenuByApp(userkey, dept.getDomainid(), application, dept.getOrgid(), deptid);
        return new Result<>(top);
    }
    
    @Override
    public Result<Set<String>> listDeptByRole(List<Long> userkeys, List<String> roles)
    {
        Set<String> deptScopes = new HashSet<>();
        List<RoleInstance> list = roleInstanceDao.listByUserAndRoleInDept(userkeys, roles);
        list.forEach(k -> deptScopes.add(k.getScope()));
        return new Result<>(deptScopes);
    }
    
    @Override
    public Result<Map<Long, List<String>>> mapDeptByRoleGroupByUser(List<Long> userkeys, String role)
    {
        Map<Long, List<String>> map = new HashMap<>();
        List<RoleInstance> list = roleInstanceDao.listByUserAndRoleInDept(userkeys, Collections.singletonList(role));
        list.forEach(k -> map.computeIfAbsent(Long.valueOf(k.getOwnerid()), m -> new ArrayList<>()).add(k.getScope()));
        return new Result<>(map);
    }
    
    @Override
    public Result<Set<Long>> listUserByRoles(List<String> depts, List<String> roles, boolean includeOrgScope)
    {
        Set<Long> users = new HashSet<>();
        List<RoleInstance> list = roleInstanceDao.listByRoleAndDeptScope(depts, roles);
        list.forEach(k -> users.add(Long.valueOf(k.getOwnerid())));
        if(includeOrgScope)
        {
            List<String> orgs = new ArrayList<>();
            List<DeptKV> l = deptCache.get(depts);
            l.forEach(e -> orgs.add(e.getOrgid()));

            list = roleInstanceDao.listByRoleAndOrgScope(orgs, roles);
            list.forEach(k -> users.add(Long.valueOf(k.getOwnerid())));
        }
        return new Result<>(users);
    }
    
    @Override
    public Result<Map<String, List<Long>>> mapUserByRoleGroupByDept(List<String> depts, List<String> roles)
    {
        Map<String, List<Long>> map = new HashMap<>();
        List<RoleInstance> list = roleInstanceDao.listByRoleAndDeptScope(depts, roles);
        list.forEach(k -> map.computeIfAbsent(k.getScope(), m -> new ArrayList<>()).add(Long.valueOf(k.getOwnerid())));
        return new Result<>(map);
    }
    
    @Override
    @CheckAppPermission(value = PermFunction)
    public Result<Boolean> setUserRole(Long userkey, List<String> roles, String deptid, String roleGroup)
    {
        AppKV app = appCache.currentApp();
        List<RoleInstance> instances = new ArrayList<>();
        if (roles != null && !roles.isEmpty())
        {
            for (String role : roles)
            {
                RoleInstance instance = new RoleInstance(app.getDomainid(), role);
                instance.setOwnerid(userkey.toString());
                instance.setScopeType(AccessScopeType.dept);
                instance.setScope(deptid);
                instances.add(instance);
                userPermissionManager.checkManagerUserRole(instance, app.getDomainid());
            }
        }
        userPermissionManager.removeUserRoleInGroupByScope(userkey, AccessScopeType.dept, deptid, roleGroup, app.getDomainid());
        userPermissionManager.addUserRole(userkey, instances, app.getDomainid());
        return new Result<>(true);
    }
    
    @Override
    @CheckAppPermission(value = PermFunction)
    public Result<Boolean> addUserRole(Long userkey, String role, String deptid)
    {
        AppKV app = appCache.currentApp();
        userPermissionManager.removeUserRole(userkey, role, AccessScopeType.dept, deptid, app.getDomainid());
        RoleInstance instance = new RoleInstance(app.getDomainid(), role);
        instance.setOwnerid(userkey.toString());
        instance.setScopeType(AccessScopeType.dept);
        instance.setScope(deptid);
        userPermissionManager.addUserRole(instance, app.getDomainid());
        return new Result<>(true);
    }
    
    @Override
    @CheckAppPermission(value = PermFunction)
    public Result<Boolean> delUserRole(Long userkey, String role, String deptid)
    {
        AppKV app = appCache.currentApp();
        userPermissionManager.removeUserRole(userkey, role, AccessScopeType.dept, deptid, app.getDomainid());
        return new Result<>(true);
    }
    
    @Override
    public Result<Boolean> clearUser(Long userkey, String deptid)
    {
        AppKV app = appCache.currentApp();
        userPermissionManager.removeUserRoleByScope(userkey, AccessScopeType.dept, deptid, app.getDomainid());
        return new Result<>(true);
    }
    
}
