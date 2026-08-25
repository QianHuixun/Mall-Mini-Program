package cn.tofocus.account.api.v4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.account.bean.application.AppKV;
import cn.tofocus.account.db.cache.application.AppReadCache;
import cn.tofocus.account.db.cache.role.AppRoleCache;
import cn.tofocus.account.db.dao.user.RoleInstanceDao;
import cn.tofocus.account.db.entity.role.AppRoleEntity;
import cn.tofocus.account.db.entity.user.RoleInstance;
import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.core.Result;
import cn.tofocus.core.security.AccessList;
import cn.tofocus.core.security.AccessScopeType;
import cn.tofocus.core.security.aop.CheckAppPermission;
import cn.tofocus.core.user.SysFunctionEnum;
import cn.tofocus.domain.manager.UserPermissionManager;
import cn.tofocus.domain.user.def.RoleInstanceDTO;
import cn.tofocus.domain.user.role.RoleInfo;

@RequestMapping("/v4/userInDomain")
@RestController
public class UserInDomainApiV4Impl implements UserInDomainApiV4
{
    private static final String PermFunction = SysFunctionEnum.ManagerUser;
    
    @Autowired
    private RoleInstanceDao roleInstanceDao;
    
    @Autowired
    private UserPermissionManager userPermissionManager;
    
    @Autowired
    private AppReadCache appCache;
    
    @Autowired
    private AppRoleCache roleCache;
    
    @Override
    public Result<List<RoleInstanceDTO>> listUserRoleInstance(Long userkey, String role)
    {
        List<RoleInstanceDTO> list = roleInstanceDao.listByUserAndRole(userkey, role);
        return new Result<>(list);
    }
    
    @Override
    public Result<Boolean> isUserHasRight(Long userkey, String function)
    {
        boolean b = false;
        Map<String, AccessList> aclmap = userPermissionManager.getCachedUserAcls(userkey);
        if (aclmap != null && aclmap.containsKey(function))
        {
            AccessList acl = aclmap.get(function);
            b = acl.canAccess();
        }
        return new Result<>(b);
    }
    
    @Override
    @CheckAppPermission(value = PermFunction)
    public Result<Boolean> clearUserRoleInstance(Long userkey)
    {
        AppKV app = appCache.currentApp();
        userPermissionManager.removeUserRoleByDomain(userkey, app.getDomainid(), app.getDomainid());
        return new Result<>(true);
    }
    
    @Override
    public Result<Set<Long>> listUserByRoles(List<String> roles)
    {
        Set<Long> users = new HashSet<>();
        List<RoleInstance> list = roleInstanceDao.listByRole(roles);
        list.forEach(k -> users.add(Long.valueOf(k.getOwnerid())));
        return new Result<>(users);
    }
    
    @Override
    @CheckAppPermission(value = PermFunction)
    public Result<Boolean> addUserRole(Long userkey, String role)
    {
        AppKV app = appCache.currentApp();
        userPermissionManager.removeUserRole(userkey, role, null, null, app.getDomainid());
        RoleInstance instance = new RoleInstance(app.getDomainid(), role);
        instance.setOwnerid(userkey.toString());
        userPermissionManager.addUserRole(instance, app.getDomainid());
        return new Result<>(true);
    }
    
    @Override
    @CheckAppPermission(value = PermFunction)
    public Result<Boolean> delUserRole(Long userkey, String role)
    {
        AppKV app = appCache.currentApp();
        userPermissionManager.removeUserRole(userkey, role, null, null, app.getDomainid());
        return new Result<>(true);
    }
    
    @Override
    @CheckAppPermission(value = PermFunction)
    public Result<Boolean> addUserRoleInDomain(Long userkey, String role)
    {
        AppKV app = appCache.currentApp();
        userPermissionManager
            .removeUserRole(userkey, role, AccessScopeType.domain, app.getDomainid(), app.getDomainid());
        RoleInstance instance = new RoleInstance(app.getDomainid(), role);
        instance.setOwnerid(userkey.toString());
        instance.setScopeType(AccessScopeType.domain);
        instance.setScope(app.getDomainid());
        userPermissionManager.addUserRole(instance, app.getDomainid());
        return new Result<>(true);
    }
    
    @Override
    @CheckAppPermission(value = PermFunction)
    public Result<Boolean> delUserRoleInDomain(Long userkey, String role)
    {
        AppKV app = appCache.currentApp();
        userPermissionManager
            .removeUserRole(userkey, role, AccessScopeType.domain, app.getDomainid(), app.getDomainid());
        return new Result<>(true);
    }
    
    @Override
    public Result<List<RoleInfo>> listUserRole(Long userkey)
    {
        AppKV app = appCache.currentApp();
        List<RoleInfo> list = new ArrayList<>();
        List<RoleInstance> l = userPermissionManager.getUserRole(userkey);
        Set<String> keys = new HashSet<>();
        for (RoleInstance r : l)
        {
            if (app.getDomainid().equals(r.getDomainid()))
            {
                if (!keys.contains(r.getValue()))
                {
                    AppRoleEntity role = roleCache.get(r.getValue());
                    if (role != null)
                        list.add(BeanUtil.beanFrom(RoleInfo.class, role));
                    keys.add(r.getValue());
                }
            }
        }
        return new Result<>(list);
    }
    
    @Override
    public Result<Map<Long, List<RoleInfo>>> mapUserRole(List<Long> userkeys)
    {
        AppKV app = appCache.currentApp();
        Map<Long, List<RoleInfo>> map = new HashMap<>();
        for (Long userkey : userkeys)
        {
            List<RoleInfo> list = map.computeIfAbsent(userkey, e -> new ArrayList<>());
            List<RoleInstance> l = userPermissionManager.getUserRole(userkey);
            Set<String> keys = new HashSet<>();
            for (RoleInstance r : l)
            {
                if (app.getDomainid().equals(r.getDomainid()))
                {
                    if (!keys.contains(r.getValue()))
                    {
                        AppRoleEntity role = roleCache.get(r.getValue());
                        if (role != null)
                            list.add(BeanUtil.beanFrom(RoleInfo.class, role));
                        keys.add(r.getValue());
                    }
                }
            }
        }
        return new Result<>(map);
    }
    
    @Override
    public Result<Boolean> setUserRoleInDomain(Long userkey, List<String> roles, String roleGroup)
    {
        AppKV app = appCache.currentApp();
        List<RoleInstance> instances = new ArrayList<>();
        if (roles != null && !roles.isEmpty())
        {
            for (String role : roles)
            {
                RoleInstance instance = new RoleInstance(app.getDomainid(), role);
                instance.setOwnerid(userkey.toString());
                instance.setScopeType(AccessScopeType.domain);
                instance.setScope(app.getDomainid());
                instances.add(instance);
                userPermissionManager.checkManagerUserRole(instance, app.getDomainid());
            }
        }
        userPermissionManager.removeUserRoleInGroupByScope(userkey,
            AccessScopeType.domain,
            app.getDomainid(),
            roleGroup,
            app.getDomainid());
        userPermissionManager.addUserRole(userkey, instances, app.getDomainid());
        return new Result<>(true);
    }
    
    @Override
    public Result<List<RoleInfo>> listUserRoleInDomain(Long userkey)
    {
        AppKV app = appCache.currentApp();
        List<RoleInfo> list = new ArrayList<>();
        List<RoleInstance> l = userPermissionManager.getUserRole(userkey);
        Set<String> keys = new HashSet<>();
        for (RoleInstance r : l)
        {
            if (AccessScopeType.domain == r.getScopeType() && r.getScope().equals(app.getDomainid())
                && !keys.contains(r.getValue()))
            {
                keys.add(r.getValue());
                AppRoleEntity role = roleCache.get(r.getValue());
                if (role != null)
                    list.add(BeanUtil.beanFrom(RoleInfo.class, role));
            }
        }
        return new Result<>(list);
    }
    
}
