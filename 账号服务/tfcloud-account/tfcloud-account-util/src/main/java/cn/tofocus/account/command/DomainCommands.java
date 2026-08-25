package cn.tofocus.account.command;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import cn.tofocus.account.command.bean.DomainAllData;
import cn.tofocus.account.db.cache.role.AppFunctionCache;
import cn.tofocus.account.db.cache.role.AppRoleCache;
import cn.tofocus.account.db.cache.role.RoleAclCache;
import cn.tofocus.account.db.cache.user.UserAclCache;
import cn.tofocus.account.db.cache.user.UserRoleCache;
import cn.tofocus.account.db.dao.application.AppLoginCheckDao;
import cn.tofocus.account.db.dao.application.MenuDao;
import cn.tofocus.account.db.dao.domain.CloudDomainDao;
import cn.tofocus.account.db.dao.domain.ModelDao;
import cn.tofocus.account.db.dao.org.DeptMenuDao;
import cn.tofocus.account.db.dao.org.DeptModelDao;
import cn.tofocus.account.db.dao.org.OrgMenuDao;
import cn.tofocus.account.db.dao.org.OrgModelDao;
import cn.tofocus.account.db.dao.role.AppFunctionDao;
import cn.tofocus.account.db.dao.role.AppRoleDao;
import cn.tofocus.account.db.dao.role.RoleAccessDao;
import cn.tofocus.account.db.dao.role.RoleMenuDao;
import cn.tofocus.account.db.dao.user.AccessInstanceDao;
import cn.tofocus.account.db.dao.user.RoleInstanceDao;
import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.common.cachemap.redis.RedisCacheMap;
import cn.tofocus.common.util.CollectionUtil;
import cn.tofocus.common.util.file.FileUtil;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.db.PutMerge;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate.SaveState;

@ShellComponent
@ShellCommandGroup("域管理")
public class DomainCommands extends BaseCommands
{
    @Autowired
    private CloudDomainDao domainDao;
    
    @Autowired
    private AppRoleDao appRoleDao;
    
    @Autowired
    private AppFunctionDao appFunctionDao;
    
    @Autowired
    private ModelDao modelDao;
    
    @Autowired
    private MenuDao menuDao;
    
    @Autowired
    private AppLoginCheckDao appLoginCheckDao;
    
    @Autowired
    private RoleInstanceDao roleInstanceDao;
    
    @Autowired
    private AccessInstanceDao accessInstanceDao;
    
    @Autowired
    private RoleAccessDao roleAccessDao;
    
    @Autowired
    private RoleMenuDao roleMenuDao;
    
    @Autowired
    private OrgModelDao orgModelDao;
    
    @Autowired
    private OrgMenuDao orgMenuDao;
    
    @Autowired
    private DeptModelDao deptModelDao;
    
    @Autowired
    private DeptMenuDao deptMenuDao;
    
    @Autowired
    private UserRoleCache userRoleCache;
    
    @Autowired
    private UserAclCache userAclCache;
    
    @Autowired
    private RoleAclCache roleAclCache;
    
    @Autowired
    private AppRoleCache appRoleCache;
    
    @Autowired
    private AppFunctionCache appFunctionCache;
    
    private static final String AllDataFile = "DomainAllData.json";
    
    private String returnStr = "";
    
    @ShellMethod("导出域的全部菜单权限配置")
    public String exportDomainAll(String domain)
    {
        if (!checkDomain(domain))
            return returnStr;
        
        System.out.println("将导出域下的角色，权限，模块，菜单，以及用户和机构的权限配置");
        if (!confirmation())
            return "取消";
        
        DomainAllData data = new DomainAllData();
        data.setModels(modelDao.listByDomain(domain));
        System.out.println("Model：" + data.getModels().size());
        data.setMenus(menuDao.listByDomain(domain));
        System.out.println("Menu：" + data.getMenus().size());
        data.setRoles(appRoleDao.listByDomain(domain));
        System.out.println("Role：" + data.getRoles().size());
        data.setFunctions(appFunctionDao.listByDomain(domain));
        System.out.println("Function：" + data.getFunctions().size());
        data.setAppChecks(appLoginCheckDao.listByDomain(domain));
        System.out.println("AppCheck：" + data.getAppChecks().size());
        data.setRoleFuncs(roleAccessDao.listByDomain(domain));
        System.out.println("RoleFunc：" + data.getRoleFuncs().size());
        data.setRoleMenus(roleMenuDao.listByDomain(domain));
        System.out.println("RoleMenu：" + data.getRoleMenus().size());
        data.setOrgModels(orgModelDao.listByDomain(domain));
        System.out.println("OrgModel：" + data.getOrgModels().size());
        data.setOrgMenus(orgMenuDao.listByDomain(domain));
        System.out.println("OrgMenu：" + data.getOrgMenus().size());
        data.setDeptModels(deptModelDao.listByDomain(domain));
        System.out.println("DeptModel：" + data.getDeptModels().size());
        data.setDeptMenus(deptMenuDao.listByDomain(domain));
        System.out.println("DeptMenu：" + data.getDeptMenus().size());
        data.setUserRoles(roleInstanceDao.listByDomain(domain));
        System.out.println("UserRole：" + data.getUserRoles().size());
        data.setUserFuncs(accessInstanceDao.listByDomain(domain));
        System.out.println("UserFunc：" + data.getUserFuncs().size());
        
        String json = JsonUtil.toString(data, true);
        File saveFile = new File(System.getProperty("user.dir") + "/" + domain + "_" + AllDataFile);
        FileUtil.deleteFile(saveFile);
        FileUtil.createFileContent(saveFile, json);
        return "已保存在 " + domain + "_" + AllDataFile;
    }
    
    @ShellMethod("导入域的全部菜单权限配置")
    public String importDomainAll(String domain)
    {
        if (!checkDomain(domain))
            return returnStr;
        
        System.out.println("将导入域下的角色，权限，模块，菜单，以及用户和机构的权限配置");
        System.out.println("原有的数据将被清除！！！");
        if (!confirmation())
            return "取消";
        
        System.out.println("第二次确认！！！");
        if (!confirmation())
            return "取消";
        
        String json =
            FileUtil.readFileContent(new File(System.getProperty("user.dir") + "/" + domain + "_" + AllDataFile));
        DomainAllData data = JsonUtil.getBean(json, DomainAllData.class);
        if (!check(domain, "Model", data.getModels(), modelDao))
            return returnStr;
        if (!check(domain, "Menu", data.getMenus(), menuDao))
            return returnStr;
        if (!check(domain, "Role", data.getRoles(), appRoleDao))
            return returnStr;
        if (!check(domain, "Function", data.getFunctions(), appFunctionDao))
            return returnStr;
        if (!check(domain, "AppCheck", data.getAppChecks(), appLoginCheckDao))
            return returnStr;
        if (!check(domain, "RoleFunc", data.getRoleFuncs(), roleAccessDao))
            return returnStr;
        if (!check(domain, "RoleMenu", data.getRoleMenus(), roleMenuDao))
            return returnStr;
        if (!check(domain, "OrgModel", data.getOrgModels(), orgModelDao))
            return returnStr;
        if (!check(domain, "OrgMenu", data.getOrgMenus(), orgMenuDao))
            return returnStr;
        if (!check(domain, "DeptModel", data.getDeptModels(), deptModelDao))
            return returnStr;
        if (!check(domain, "DeptMenu", data.getDeptMenus(), deptMenuDao))
            return returnStr;
        if (!check(domain, "UserRole", data.getUserRoles(), roleInstanceDao))
            return returnStr;
        if (!check(domain, "UserFunc", data.getUserFuncs(), accessInstanceDao))
            return returnStr;
        
        replace(domain, "Model", data.getModels(), modelDao);
        replace(domain, "Menu", data.getMenus(), menuDao);
        replace(domain, "Role", data.getRoles(), appRoleCache);
        replace(domain, "Function", data.getFunctions(), appFunctionCache);
        replace(domain, "AppCheck", data.getAppChecks(), appLoginCheckDao);
        replace(domain, "RoleFunc", data.getRoleFuncs(), roleAclCache);
        replace(domain, "RoleMenu", data.getRoleMenus(), roleMenuDao);
        replace(domain, "OrgModel", data.getOrgModels(), orgModelDao);
        replace(domain, "OrgMenu", data.getOrgMenus(), orgMenuDao);
        replace(domain, "DeptModel", data.getDeptModels(), deptModelDao);
        replace(domain, "DeptMenu", data.getDeptMenus(), deptMenuDao);
        replace(domain, "UserRole", data.getUserRoles(), userRoleCache);
        replace(domain, "UserFunc", data.getUserFuncs(), userAclCache);
        
        return "完成";
    }

    private <T extends HasPkey<String>> void replace(String domain, String str, List<T> list,
        RedisCacheMap<String, T> dao)
    {
        List<T> dels = dao.select().strict(true).eq("domainid", domain).exec();
        PutMerge<String,T> putMerge = new PutMerge<>(dels, list);
        if (!putMerge.getRemoveMap().isEmpty())
            dao.removeAll(putMerge.getRemoveMap().values());
        if (!putMerge.getPutList().isEmpty())
            dao.putAll(putMerge.getPutList());
        System.out.println(str + "：删除 " + putMerge.getRemoveMap().size() + " 条，增加 " + putMerge.getPutList().size() + " 条");
    }
    
    private <T extends HasPkey<String>> void replace(String domain, String str, List<T> list,
        JpaSpecificationDelegate<String, T> dao)
    {
        List<T> dels = dao.select().strict(true).eq("domainid", domain).exec();
        SaveState state = dao.removeAndPutAll(dels, list);
        System.out.println(str + "：删除 " + state.getDel() + " 条，增加 " + state.getSave() + " 条");
    }
    
    private boolean check(String domain, String str, List<? extends HasPkey<String>> list,
        JpaSpecificationDelegate<String, ? extends HasPkey<String>> dao)
    {
        if (dao.selectOne()
            .strict(true)
            .notEq("domainid", domain)
            .in("pkey", CollectionUtil.keyList(list))
            .exec() != null)
        {
            returnStr = str + "： 主键重复";
            return false;
        }
        else
        {
            System.out.println(str + "：" + list.size());
            return true;
        }
    }
    
    private boolean checkDomain(String domain)
    {
        if (!domainDao.isExistKey(domain))
        {
            returnStr = domain + "不存在";
            return false;
        }
        else
        {
            returnStr = "ok";
            return true;
        }
    }
}
