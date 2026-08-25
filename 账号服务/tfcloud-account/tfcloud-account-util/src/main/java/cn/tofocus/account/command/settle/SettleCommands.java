package cn.tofocus.account.command.settle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import cn.tofocus.account.command.BaseCommands;
import cn.tofocus.account.command.settle.SettleConstant.C;
import cn.tofocus.account.command.settle.SettleConstant.F;
import cn.tofocus.account.command.settle.SettleConstant.R;
import cn.tofocus.account.command.settle.SettleConstant.SysF;
import cn.tofocus.account.db.cache.role.AppFunctionCache;
import cn.tofocus.account.db.cache.role.AppRoleCache;
import cn.tofocus.account.db.dao.application.AppLoginCheckDao;
import cn.tofocus.account.db.dao.application.MenuDao;
import cn.tofocus.account.db.dao.role.RoleAccessDao;
import cn.tofocus.account.db.dao.role.RoleMenuDao;
import cn.tofocus.account.db.entity.application.AppLoginCheckEntity;
import cn.tofocus.account.db.entity.application.MenuEntity;
import cn.tofocus.account.db.entity.role.AppFunctionEntity;
import cn.tofocus.account.db.entity.role.AppRoleEntity;
import cn.tofocus.account.db.entity.role.RoleAccessInstance;
import cn.tofocus.account.db.entity.role.RoleMenuEntity;
import cn.tofocus.common.util.CollectionUtil;

@ShellComponent
@ShellCommandGroup("清分权限迁移")
public class SettleCommands extends BaseCommands
{
    @Autowired
    private AppRoleCache appRoleCache;
    
    @Autowired
    private AppFunctionCache appFunctionCache;
    
    @Autowired
    private RoleAccessDao roleAccessDao;
    
    @Autowired
    private RoleMenuDao roleMenuDao;
    
    @Autowired
    private AppLoginCheckDao appLoginCheckDao;
    
    @Autowired
    private MenuDao menuDao;
    
    @ShellMethod("清分权限迁移")
    public String updateSettle()
    {
        //重置系统角色
        List<AppRoleEntity> roleList = appRoleCache.select().eq(AppRoleEntity.F.domainid, SettleConstant.DOMAIN).exec();
        
        roleList.forEach(r -> {
            r.setGroup(R.GROUP_NAME);
            appRoleCache.put(r);
        });
        //权限迁移
        appFunctionCache
            .removeAll(appFunctionCache.select().eq(AppRoleEntity.F.domainid, SettleConstant.DOMAIN).exec());
        addFunc(F.WEB_LOGIN, "登陆管理后台", C.WEB);
        
        //登录权限验证
        addLoginCheck(C.WEB, F.WEB_LOGIN);
        
        //初始化系统角色的权限
        roleAccessDao.removeAll(roleAccessDao.select().eq(RoleAccessInstance.F.domainid, SettleConstant.DOMAIN).exec());
        addRoleAcl(R.ADMIN,
            Arrays.asList(F.WEB_LOGIN, SysF.DOMAIN_ADMIN, SysF.MANAGER_USER, SysF.MANAGER_ROLE, SysF.MANAGER_ORG));
        addRoleAcl(R.COMPANY_MANAGER,
            Arrays.asList(F.WEB_LOGIN, SysF.DOMAIN_ADMIN, SysF.MANAGER_USER, SysF.MANAGER_ROLE, SysF.MANAGER_ORG));
        addRoleAcl(R.MARKET_MANAGER,
            Arrays.asList(F.WEB_LOGIN, SysF.DOMAIN_ADMIN, SysF.MANAGER_USER, SysF.MANAGER_ROLE, SysF.MANAGER_ORG));
        
        StrList adminMenu = findByMenu("settle_company_ins",
            "settle_company_del",
            "settle_market_ins",
            "settle_market_del",
            "settle_market_enable",
            "settle_report_count");
        StrList orgMenu = findByMenu("settle_company_ins",
            "settle_company_del",
            "settle_leave_ins",
            "settle_leave_del",
            "settle_leave_upd",
            "settle_leave_approve",
            "settle_leave_unapprove",
            "settle_trade_modify_ins",
            "settle_trade_modify_del",
            "settle_trade_modify_upd",
            "settle_trade_modify_approve",
            "settle_trade_modify_unapprove",
            "settle_settle_commission_approve",
            "settle_settle_commission_unapprove",
            "settle_report_count");
        StrList deptMenu = findByMenu("settle_company_ins",
            "settle_company_del",
            "settle_company_upd",
            "settle_company_query",
            "settle_market_ins",
            "settle_market_del",
            "settle_market_upd",
            "settle_market_enable",
            "settle_service_rate_ins",
            "settle_service_rate_del",
            "settle_service_rate_upd",
            "settle_report_count");
        
        //初始化系统角色的菜单
        addRoleMenu(R.ADMIN, adminMenu);
        addRoleMenu(R.COMPANY_MANAGER, orgMenu);
        addRoleMenu(R.MARKET_MANAGER, deptMenu);
        
        return "ok";
    }
    
    private void addFunc(String func, String name, String group)
    {
        AppFunctionEntity entity = new AppFunctionEntity();
        entity.setPkey(func);
        entity.setName(name);
        entity.setGroup(group);
        entity.setDomainid(SettleConstant.DOMAIN);
        appFunctionCache.put(entity);
    }
    
    private void addLoginCheck(String app, String func)
    {
        AppLoginCheckEntity entity = new AppLoginCheckEntity();
        entity.setPkey(app);
        entity.setDomainid(SettleConstant.DOMAIN);
        entity.setFuncKey(func);
        appLoginCheckDao.put(entity);
    }
    
    private void addRoleAcl(String role, List<String> funcs)
    {
        List<RoleAccessInstance> list = new ArrayList<>();
        int o = 0;
        for (String func : funcs)
        {
            RoleAccessInstance entity = new RoleAccessInstance();
            entity.setPkey(role + "_func_" + o);
            entity.setOwnerid(role);
            entity.setFuncKey(func);
            entity.setDomainid(SettleConstant.DOMAIN);
            entity.setAccept(true);
            list.add(entity);
            o++;
        }
        roleAccessDao.putAll(list);
    }
    
    private StrList findByMenu(String... menuId)
    {
        List<MenuEntity> menulist =
            menuDao.select().eq(MenuEntity.F.domainid, SettleConstant.DOMAIN).notIn(MenuEntity.F.pkey, menuId).exec();
        StrList r = new StrList();
        r.addAll(CollectionUtil.keyList(menulist));
        return r;
    }
    
    private static class StrList extends ArrayList<String>
    {
        /**
         * 注释内容
         */
        private static final long serialVersionUID = 1L;
        
        public static StrList fromArray(String... a)
        {
            StrList l = new StrList();
            l.addAll(Arrays.asList(a));
            return l;
        }
    }
    
    private void addRoleMenu(String role, StrList... menuArray)
    {
        roleMenuDao.removeAll(roleMenuDao.select().eq("ownerid", role).exec());
        List<RoleMenuEntity> list = new ArrayList<>();
        int o = 0;
        for (List<String> menus : menuArray)
        {
            for (String menu : menus)
            {
                RoleMenuEntity entity = new RoleMenuEntity();
                entity.setPkey(role + "_menu_" + o);
                entity.setOwnerid(role);
                entity.setMenu(menu);
                entity.setDomainid(SettleConstant.DOMAIN);
                entity.setAccept(true);
                list.add(entity);
                o++;
            }
        }
        roleMenuDao.putAll(list);
    }
}
