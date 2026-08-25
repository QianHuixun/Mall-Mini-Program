package cn.tofocus.account.api.v4;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.account.bean.application.AppKV;
import cn.tofocus.account.db.cache.application.AppReadCache;
import cn.tofocus.account.db.dao.org.DepartmentDao;
import cn.tofocus.account.db.dao.user.RoleInstanceDao;
import cn.tofocus.account.db.entity.user.RoleInstance;
import cn.tofocus.core.Result;
import cn.tofocus.core.security.AccessScopeType;
import cn.tofocus.core.security.aop.CheckAppPermission;
import cn.tofocus.core.user.SysFunctionEnum;
import cn.tofocus.domain.manager.UserPermissionManager;

@RequestMapping("/v4/userInOrg")
@RestController
public class UserInOrgApiV4Impl implements UserInOrgApiV4
{
    private static final String PermFunction = SysFunctionEnum.ManagerUser;
    
    @Autowired
    private UserPermissionManager userPermissionManager;

    @Autowired
    private RoleInstanceDao roleInstanceDao;
    
    @Autowired
    private AppReadCache appCache;

    @Autowired
    private DepartmentDao departmentDao;

    @Override
    @CheckAppPermission(value = PermFunction)
    public Result<Boolean> addUserRole(Long userkey, String role, String orgid)
    {
        AppKV app = appCache.currentApp();
        userPermissionManager.removeUserRole(userkey, role, AccessScopeType.org, orgid, app.getDomainid());
        RoleInstance instance = new RoleInstance(app.getDomainid(), role);
        instance.setOwnerid(userkey.toString());
        instance.setScopeType(AccessScopeType.org);
        instance.setScope(orgid);
        userPermissionManager.addUserRole(instance, app.getDomainid());
        return new Result<>(true);
    }

    @Override
    @CheckAppPermission(value = PermFunction)
    public Result<Boolean> delUserRole(Long userkey, String role, String orgid)
    {
        AppKV app = appCache.currentApp();
        userPermissionManager.removeUserRole(userkey, role, AccessScopeType.org, orgid, app.getDomainid());
        return new Result<>(true);
    }

    @Override
    public Result<Set<Long>> listUserByRole(String role, String orgid)
    {
        List<String> roles = Collections.singletonList(role);
        List<String> depts = departmentDao.listByOrg(orgid);
        Set<Long> users = new HashSet<>();
        List<RoleInstance> list = roleInstanceDao.listByRoleAndOrgScope(Collections.singletonList(orgid), roles);
        list.forEach(k -> users.add(Long.valueOf(k.getOwnerid())));

        list = roleInstanceDao.listByRoleAndDeptScope(depts, roles);
        list.forEach(k -> users.add(Long.valueOf(k.getOwnerid())));
        return new Result<>(users);
    }
    
    
}
