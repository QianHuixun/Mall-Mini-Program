package cn.tofocus.lejia.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.security.SecurityContextUtil;
import cn.tofocus.lejia.cache.AccessMap;
import cn.tofocus.lejia.config.LejiaConfig;
import cn.tofocus.lejia.domain.ServerInitManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class ServerInit
{
    @Autowired
    private LejiaConfig config;
    
    //    @Autowired
    //    private DomainAdminApi domainAdminApi;
    
    @Autowired
    private SecurityContextUtil securityContextUtil;
    
    @Autowired
    private ServerInitManager manager;
    
    //    @GetMapping(value = "/server/initMenu")
    //    public Result<Boolean> initMenu(@RequestParam("username") String username,
    //        @RequestParam("password") String password)
    //    {
    //        Result<Boolean> r = new Result<>();
    //        securityContextUtil.runAsUser(username, password);
    //        AuthenticationContext context = SecurityContextUtil.getAuthenticationContext();
    //        Map<String, AccessList> map = context.getAuthorities();
    //        NamedBean currentDomain = context.getCurrentDomain();
    //        
    //        AccessList acl = map.get(SysFunctionEnum.domainAdmin.name());
    //        log.info("acl: " + acl);
    //        if (acl != null && acl.canAccessDomain((String)currentDomain.getPkey()))
    //        {
    //            AppMenuList bossMenu = manager.initBossMenus();
    //            AppMenuList marketMenu = manager.initMarketMenus();
    //            
    //            domainAdminApi.iniAppMenu(bossMenu).fetchResult();
    //            domainAdminApi.iniAppMenu(marketMenu).fetchResult();
    //            r.setResult(true);
    //        }
    //        else
    //        {
    //            throw TofocusException.of(SysErrCode.ACCESS_DENIED, "没有域管理权限");
    //        }
    //        securityContextUtil.exitRunAs();
    //        return r;
    //    }
    
    //    // 初始化菜单
    //    @GetMapping(value = "/server/init")
    //    public Result<Boolean> init(@RequestParam("username") String username, @RequestParam("password") String password)
    //    {
    //        Result<Boolean> r = new Result<>();
    //        if (config.containsKey("inited"))
    //        {
    //            throw TofocusException.of(SysErrCode.ACCESS_DENIED, "已经初始化");
    //        }
    //        else
    //        {
    //            securityContextUtil.runAsUser(username, password);
    //            
    //            AuthenticationContext context = SecurityContextUtil.getAuthenticationContext();
    //            Map<String, AccessList> map = context.getAuthorities();
    //            NamedBean currentDomain = context.getCurrentDomain();
    //            
    //            AccessList acl = map.get(SysFunctionEnum.domainAdmin.name());
    //            if (acl != null && acl.canAccessDomain((String)currentDomain.getPkey()))
    //            {
    //                manager.initDomainAuthority();
    //                r.setResult(true);
    //            }
    //            else
    //            {
    //                throw TofocusException.of(SysErrCode.ACCESS_DENIED, "没有域管理权限");
    //            }
    //            
    //            securityContextUtil.exitRunAs();
    //        }
    //        return r;
    //    }
    
    //    // 可以多次初始化 菜单 角色
    //    @GetMapping(value = "/server/reInit")
    //    public Result<Boolean> reInit()
    //    {
    //        Result<Boolean> r = new Result<>();
    //        AuthenticationContext context = SecurityContextUtil.getAuthenticationContext();
    //        Map<String, AccessList> map = context.getAuthorities();
    //        NamedBean currentDomain = context.getCurrentDomain();
    //        
    //        AccessList acl = map.get(SysFunctionEnum.domainAdmin.name());
    //        if (acl != null && acl.canAccessDomain((String)currentDomain.getPkey()))
    //        {
    //            manager.initDomainAuthority();
    //            r.setResult(true);
    //        }
    //        else
    //        {
    //            throw TofocusException.of(SysErrCode.ACCESS_DENIED, "没有域管理权限");
    //        }
    //        return r;
    //    }
    
    // 生成原始的system账号
    @GetMapping(value = "/server/initSystem")
    public Result<Boolean> insSystem(String userId, String mobile, Integer ascription, String name, String pwd)
    {
        return new Result<>(manager.createSystem(userId, mobile, ascription, name, pwd));
    }
    
    // 重新绑定zySystem账户
    @GetMapping(value = "/server/updSystem")
    public Result<Boolean> updateSystem(Long key, String moblie, String userId, Integer ascription)
    {
        return new Result<>(manager.updateSystem(key, moblie, userId, ascription));
    }
    
    // 生成本地的system账号
    @PostMapping(value = "/server/local/initSystem")
    public Result<Boolean> insLocalSystem(@RequestParam("pkey") Integer pkey, @RequestParam("name") String name,
        @RequestParam("ascription") Integer ascription, @RequestParam("mobile") String mobile)
    {
        return new Result<>(manager.createLocalSystem(pkey, name, ascription, mobile));
    }
    
//    // 只更新权限和菜单 不涉及角色
//    @GetMapping(value = "/server/initMenuAndFuntion")
//    public Result<Boolean> initMenuAndFuntion()
//    {
//        Result<Boolean> r = new Result<>();
//        AuthenticationContext context = SecurityContextUtil.getAuthenticationContext();
//        Map<String, AccessList> map = context.getAuthorities();
//        NamedBean currentDomain = context.getCurrentDomain();
//        
//        AccessList acl = map.get(SysFunctionEnum.domainAdmin.name());
//        if (acl != null && acl.canAccessDomain((String)currentDomain.getPkey()))
//        {
//            
//            FuncRoot functionRoot = manager.initFunctionRoot();
//            DomainFunctionGroup g = new DomainFunctionGroup();
//            g.setDomainid(Constant.DomainId);
//            g.setFunctionRoot(functionRoot);
//            domainAdminApi.iniFunctionGroup(g);
//            
//            AppMenuList bossMenus = manager.initBossMenus();
//            AppMenuList menus = new AppMenuList();
//            menus.setAppid(bossMenus.getAppid());
//            menus.setMenus(bossMenus.getMenus());
//            menus.getMenus().addAll(manager.initMarketMenus().getMenus());
//            domainAdminApi.iniAppMenu(menus);
//            
//            r.setResult(true);
//        }
//        else
//        {
//            throw TofocusException.of(SysErrCode.ACCESS_DENIED, "没有域管理权限");
//        }
//        return r;
//    }
//    
//    // 更新权限、菜单、角色,不会接触用户和角色的关系  代码未完成 不可用
//    //	@GetMapping(value = "/server/initNotRelation")
//    public Result<Boolean> initNotRelation()
//    {
//        Result<Boolean> r = new Result<>();
//        AuthenticationContext context = SecurityContextUtil.getAuthenticationContext();
//        Map<String, AccessList> map = context.getAuthorities();
//        NamedBean currentDomain = context.getCurrentDomain();
//        
//        AccessList acl = map.get(SysFunctionEnum.domainAdmin.name());
//        if (acl != null && acl.canAccessDomain((String)currentDomain.getPkey()))
//        {
//            
//            FuncRoot functionRoot = manager.initFunctionRoot();
//            DomainFunctionGroup g = new DomainFunctionGroup();
//            g.setDomainid(Constant.DomainId);
//            g.setFunctionRoot(functionRoot);
//            domainAdminApi.iniFunctionGroup(g);
//            
//            AppMenuList bossMenus = manager.initBossMenus();
//            AppMenuList menus = new AppMenuList();
//            menus.setAppid(bossMenus.getAppid());
//            menus.setMenus(bossMenus.getMenus());
//            menus.getMenus().addAll(manager.initMarketMenus().getMenus());
//            domainAdminApi.iniAppMenu(menus);
//            
//            //			domainAdminApi.
//            r.setResult(true);
//        }
//        else
//        {
//            throw TofocusException.of(SysErrCode.ACCESS_DENIED, "没有域管理权限");
//        }
//        return r;
//    }
//    
//    // 只更新角色
//    @GetMapping(value = "/server/initRole")
//    public Result<Boolean> initRole(@RequestParam("username") String username,
//        @RequestParam("password") String password)
//    {
//        Result<Boolean> r = new Result<>();
//        securityContextUtil.runAsUser(username, password);
//        
//        AuthenticationContext context = SecurityContextUtil.getAuthenticationContext();
//        Map<String, AccessList> map = context.getAuthorities();
//        NamedBean currentDomain = context.getCurrentDomain();
//        
//        AccessList acl = map.get(SysFunctionEnum.domainAdmin.name());
//        if (acl != null && acl.canAccessDomain((String)currentDomain.getPkey()))
//        {
//            DomainRoleList authority = new DomainRoleList();
//            authority.setDomainid(Constant.DomainId);
//            authority.setRoles(manager.getRoles());
//            domainAdminApi.iniSysRole(authority).fetchResult();
//            r.setResult(true);
//        }
//        else
//        {
//            throw TofocusException.of(SysErrCode.ACCESS_DENIED, "没有域管理权限");
//        }
//        securityContextUtil.exitRunAs();
//        return r;
//    }
//    
//    @GetMapping(value = "/server/insRoleAcl")
//    public Result<Boolean> insRoleAcl(String roleid, String subNodePkey, String subNodeName, int subIndex, int sort)
//    {
//        return new Result<>(manager.insRoleAcl(roleid, subNodePkey, subNodeName, subIndex, sort));
//    }
    
    @Autowired
    private AccessMap accessMap;
    
    @PostMapping("/redis")
    public Result<List<String>> getRedis(String keysRoot)
    {
        Set<String> all = accessMap.findAll(keysRoot);
        Iterator<String> iterator = all.iterator();
        List<String> list = new ArrayList<>();
        while (iterator.hasNext())
        {
            list.add(iterator.next());
        }
        return new Result<>(list);
    }
}
