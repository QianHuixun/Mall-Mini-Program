package cn.tofocus.account.api.v4;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.constraints.Size;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.NullHandling;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.account.bean.application.AppKV;
import cn.tofocus.account.bean.application.MenuConfig;
import cn.tofocus.account.bean.application.MenuInfo;
import cn.tofocus.account.bean.org.DeptKV;
import cn.tofocus.account.bean.org.OrgKV;
import cn.tofocus.account.bean.user.app.AppRoleInfo;
import cn.tofocus.account.db.cache.application.AppReadCache;
import cn.tofocus.account.db.cache.org.DeptReadCache;
import cn.tofocus.account.db.cache.org.OrgReadCache;
import cn.tofocus.account.db.cache.role.AppRoleCache;
import cn.tofocus.account.db.cache.user.UserRoleCache;
import cn.tofocus.account.db.dao.application.MenuDao;
import cn.tofocus.account.db.dao.org.DepartmentDao;
import cn.tofocus.account.db.dao.org.DeptMenuDao;
import cn.tofocus.account.db.dao.org.DeptModelDao;
import cn.tofocus.account.db.dao.org.OrgMenuDao;
import cn.tofocus.account.db.dao.org.OrgModelDao;
import cn.tofocus.account.db.dao.role.AppRoleDao;
import cn.tofocus.account.db.dao.user.RoleInstanceDao;
import cn.tofocus.account.db.entity.application.MenuEntity;
import cn.tofocus.account.db.entity.role.AppRoleEntity;
import cn.tofocus.account.db.entity.role.AppRoleEntity.F;
import cn.tofocus.account.db.entity.user.RoleInstance;
import cn.tofocus.account.db.entity.user.UserEntity;
import cn.tofocus.account.dto.user.SysUserInfo;
import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.core.Result;
import cn.tofocus.core.data.KeyValue;
import cn.tofocus.core.exception.SysErrCode;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.security.aop.CheckAppPermission;
import cn.tofocus.core.user.SysFunctionEnum;
import cn.tofocus.domain.manager.ModelManager;
import cn.tofocus.domain.manager.OrginazationManager;
import cn.tofocus.domain.manager.UserManager;
import cn.tofocus.domain.manager.UserPermissionManager;
import lombok.extern.slf4j.Slf4j;

@Validated
@RequestMapping("/v4/admin")
@RestController
@Slf4j
public class AdminApiV4Impl implements AdminApiV4
{
    @Autowired
    private UserManager userManager;
    
    @Autowired
    private OrginazationManager orginazationManager;
    
    @Autowired
    private AppReadCache appCache;
    
    @Autowired
    private MenuDao menuDao;
    
    @Autowired
    private OrgModelDao orgModelDao;
    
    @Autowired
    private DeptModelDao deptModelDao;
    
    @Autowired
    private OrgMenuDao orgMenuDao;
    
    @Autowired
    private DeptMenuDao deptMenuDao;
    
    @Autowired
    private DeptReadCache deptReadCache;
    
    @Autowired
    private OrgReadCache orgReadCache;
    
    @Autowired
    private ModelManager modelManager;
    
    @Autowired
    private UserRoleCache userRoleCache;
    
    @Autowired
    private RoleInstanceDao userRoleDao;
    
    @Autowired
    private AppRoleCache appRoleCache;

    @Autowired
    private UserPermissionManager userPermissionManager;
    
    @Override
    @CheckAppPermission(value = SysFunctionEnum.ManagerUser)
    public Result<SysUserInfo> addUserByMobile(@Size(max = 100) String name, boolean actived,
        @Size(max = 20) String mobile)
    {
        AppKV app = appCache.currentApp();
        UserEntity user = userManager.addUserByMobile(app.getDomainid(), name, actived, mobile);
        return new Result<>(BeanUtil.beanFrom(SysUserInfo.class, user));
    }
    
    @Override
    @CheckAppPermission(value = SysFunctionEnum.ManagerUser)
    public Result<SysUserInfo> addUserByUserId(@Size(max = 100) String name, boolean actived,
        @Size(max = 40) String userid, @Size(max = 20) String mobile)
    {
        AppKV app = appCache.currentApp();
        UserEntity user = userManager.addUserByUserId(app.getDomainid(), name, actived, userid, mobile);
        return new Result<>(BeanUtil.beanFrom(SysUserInfo.class, user));
    }
    
    @Override
    @CheckAppPermission(value = SysFunctionEnum.ManagerUser)
    public Result<Boolean> delUser(Long userkey)
    {
        AppKV app = appCache.currentApp();
        userManager.delUser(app.getDomainid(), userkey);
        return new Result<>(true);
    }
    
    @Override
    @CheckAppPermission(value = SysFunctionEnum.ManagerUser)
    public Result<Boolean> modifyuserinfo(Long userkey, @Size(max = 40) String userid, @Size(max = 100) String name,
        @Size(max = 20) String mobile)
    {
        AppKV app = appCache.currentApp();
        userManager.modifyUserinfo(app.getDomainid(), userkey, userid, name, mobile);
        return new Result<>(true);
    }
    
    @Override
    @CheckAppPermission(value = SysFunctionEnum.ManagerUser)
    public Result<Boolean> enableUser(Long userkey, boolean actived)
    {
        AppKV app = appCache.currentApp();
        userManager.enableUser(app.getDomainid(), userkey, actived);
        return new Result<>(true);
    }

    @Override
    @CheckAppPermission(value = SysFunctionEnum.ManagerUser)
    public Result<Boolean> resetPassword(Long userkey, String pwd)
    {
        AppKV app = appCache.currentApp();
        userManager.resetPassword(app.getDomainid(), userkey, pwd);
        return new Result<>(true);
    }
    
    @Override
    @CheckAppPermission(value = SysFunctionEnum.ManagerOrg)
    public Result<Boolean> saveOrginazation(@Size(max = 40) String orgid, @Size(max = 100) String name)
    {
        AppKV app = appCache.currentApp();
        orginazationManager.saveOrg(app.getDomainid(), orgid, name);
        return new Result<>(true);
    }
    
    @Override
    @CheckAppPermission(value = SysFunctionEnum.ManagerOrg)
    public Result<Boolean> delOrginazation(@Size(max = 40) String orgid)
    {
        AppKV app = appCache.currentApp();
        orginazationManager.delOrg(app.getDomainid(), orgid);
        return new Result<>(true);
    }
    
    @Override
    @CheckAppPermission(value = SysFunctionEnum.ManagerOrg)
    public Result<Boolean> saveDepartment(@Size(max = 40) String deptid, @Size(max = 40) String orgid,
        @Size(max = 100) String name)
    {
        AppKV app = appCache.currentApp();
        orginazationManager.saveDepartment(app.getDomainid(), deptid, orgid, name);
        return new Result<>(true);
    }
    
    @Override
    @CheckAppPermission(value = SysFunctionEnum.ManagerOrg)
    public Result<Boolean> delDepartment(@Size(max = 40) String deptid)
    {
        AppKV app = appCache.currentApp();
        orginazationManager.delDepartment(app.getDomainid(), deptid);
        return new Result<>(true);
    }
    
    @Override
    @CheckAppPermission(value = SysFunctionEnum.DomainAdmin)
    public Result<Boolean> delDepartmentAll(@Size(max = 40) String deptid)
    {
        long c = 0;
        AppKV app = appCache.currentApp();
        //删除部门
        orginazationManager.delDepartment(app.getDomainid(), deptid);
        log.info("删除部门，[{}]", deptid);
        //删除授权给部门的角色
        List<RoleInstance> list = userRoleDao.listAllByDeptScope(deptid);
        userRoleCache.removeAll(list);
        log.info("删除{}个配给市场的角色", list.size());
        //删除部门下的角色
        List<AppRoleEntity> roles = appRoleCache.select().eq(F.domainid, app.getDomainid()).eq(F.deptid, deptid).exec();
        for(AppRoleEntity role : roles)
        {
            userPermissionManager.removeRole(role.getPkey());
        }
        log.info("删除{}个市场的角色", roles.size());
        //删除部门的配置
        c = deptMenuDao.delByDept(deptid);
        log.info("删除{}个市场的菜单配置", c);
        c = deptModelDao.delByDept(deptid);
        log.info("删除{}个市场的模块配置", c);
        userPermissionManager.resetAllAcl();
        log.info("reset ACL 完成");
        return new Result<>(true);
    }
    
    @Override
    @CheckAppPermission(value = SysFunctionEnum.DomainAdmin)
    public Result<List<MenuInfo>> listMenuByModel(String application, String model)
    {
        String domain = appCache.currentApp().getDomainid();
        AppKV app = appCache.get(application);
        if (!domain.equals(app.getDomainid()))
            throw TofocusException.of(SysErrCode.ACCESS_DENIED);
        
        List<MenuEntity> list = menuDao.listMenuAndButtonByModel(application, model);
        return new Result<>(BeanUtil.beanListFrom(MenuInfo.class, list));
    }
    
    @Override
    @CheckAppPermission(value = SysFunctionEnum.DomainAdmin)
    public Result<Boolean> setModelConfigByDept(KeyValue<String, Map<String, Boolean>> value)
    {
        String deptid = value.getKey();
        Map<String, Boolean> modelConfigs = value.getValue();
        String domain = appCache.currentApp().getDomainid();
        DeptKV dept = deptReadCache.get(deptid);
        if (dept == null)
            return new Result<>(false);
        if (!domain.equals(dept.getDomainid()))
            throw TofocusException.of(SysErrCode.ACCESS_DENIED);
        deptModelDao.updateConfig(domain, dept.getOrgid(), deptid, modelConfigs);
        return new Result<>(true);
    }
    
    @Override
    @CheckAppPermission(value = SysFunctionEnum.DomainAdmin)
    public Result<Boolean> setModelConfigByOrg(KeyValue<String, Map<String, Boolean>> value)
    {
        String orgid = value.getKey();
        Map<String, Boolean> modelConfigs = value.getValue();
        String domain = appCache.currentApp().getDomainid();
        OrgKV org = orgReadCache.get(orgid);
        if (org == null)
            return new Result<>(false);
        if (!domain.equals(org.getDomainid()))
            throw TofocusException.of(SysErrCode.ACCESS_DENIED);
        orgModelDao.updateConfig(domain, orgid, modelConfigs);
        return new Result<>(true);
    }
    
    @Override
    @CheckAppPermission(value = SysFunctionEnum.DomainAdmin)
    public Result<Boolean> setMenuConfigByDept(MenuConfig config)
    {
        String deptid = config.getOwner();
        String application = config.getApplication();
        String model = config.getModel();
        List<String> unselectedMenu = config.getMenus();
        String domain = appCache.currentApp().getDomainid();
        AppKV app = appCache.get(application);
        DeptKV dept = deptReadCache.get(deptid);
        if (dept == null)
            return new Result<>(false);
        if (!domain.equals(app.getDomainid()) || !domain.equals(dept.getDomainid()))
            throw TofocusException.of(SysErrCode.ACCESS_DENIED);
        deptMenuDao.updateConfig(domain, dept.getOrgid(), deptid, model, application, unselectedMenu);
        return new Result<>(true);
    }
    
    @Override
    @CheckAppPermission(value = SysFunctionEnum.DomainAdmin)
    public Result<Boolean> setMenuConfigByOrg(MenuConfig config)
    {
        String orgid = config.getOwner();
        String application = config.getApplication();
        String model = config.getModel();
        List<String> menus = config.getMenus();
        String domain = appCache.currentApp().getDomainid();
        AppKV app = appCache.get(application);
        OrgKV org = orgReadCache.get(orgid);
        if (org == null)
            return new Result<>(false);
        if (!domain.equals(app.getDomainid()) || !domain.equals(org.getDomainid()))
            throw TofocusException.of(SysErrCode.ACCESS_DENIED);
        
        boolean defShow = modelManager.getModel(model).isDefShowMenu();
        if (defShow)
            orgMenuDao.updateConfig(domain, orgid, model, application, menus, false);
        else
            orgMenuDao.updateConfig(domain, orgid, model, application, menus, true);
        return new Result<>(true);
    }
}
