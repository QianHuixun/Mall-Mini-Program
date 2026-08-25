package cn.tofocus.domain.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.account.bean.role.FuncInfo;
import cn.tofocus.account.bean.role.RoleAclTree;
import cn.tofocus.account.bean.role.RoleMenuTree;
import cn.tofocus.account.bean.role.RoleMenuTree.AppMenuTree;
import cn.tofocus.account.bean.user.app.AppRoleInfo;
import cn.tofocus.account.bean.user.app.AppRoleInfoOnPage;
import cn.tofocus.account.db.cache.role.AppRoleCache;
import cn.tofocus.account.db.dao.application.ApplicationDao;
import cn.tofocus.account.db.dao.role.AppRoleDao;
import cn.tofocus.account.db.entity.role.AppRoleEntity;
import cn.tofocus.account.db.entity.role.RoleAccessInstance;
import cn.tofocus.common.Constant;
import cn.tofocus.core.data.StrKeyName;
import cn.tofocus.core.data.TreeModel;
import cn.tofocus.core.data.TreeView;
import cn.tofocus.core.enums.MenuType;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.core.security.SecurityContextUtil;
import cn.tofocus.core.user.SysFunctionEnum;

@Component
public class RoleManager
{
    @Autowired
    private AppRoleCache appRoleCache;
    
    @Autowired
    private AppRoleDao roleDao;
    
    @Autowired
    private UserPermissionManager userPermissionManager;
    
    @Autowired
    private FunctionManager functionManager;
    
    @Autowired
    private MenuManager menuManager;
    
    @Autowired
    private ApplicationDao applicationDao;
    
    /**************************
     * 
     *    应用角色
     *    
     **************************/
    
    public AppRoleEntity getAppRole(String roleid)
    {
        return appRoleCache.get(roleid);
    }
    
    /**
     * 新增应用角色
     * <p/>
     * <功能详细描述>
     * @param role
     * @return
     */
    public AppRoleEntity addAppRole(AppRoleEntity role)
    {
        return appRoleCache.add(role);
    }
    
    /**
     * 修改应用角色
     * <p/>
     * <功能详细描述>
     * @param role
     * @return
     */
    public AppRoleEntity modAppRole(AppRoleEntity role)
    {
        return appRoleCache.update(role);
    }
    
    public void saveAppRole(AppRoleEntity role)
    {
        appRoleCache.put(role);
    }
    
    /**
     * 删除应用角色
     * <p/>
     * <功能详细描述>
     * @param roleid
     * @return
     */
    public boolean delAppRole(String roleid)
    {
        boolean r = appRoleCache.removeById(roleid);
        return r;
    }
    
    public String delAppRole(String roleid, boolean force)
    {
        if (!force && userPermissionManager.isRoleUsed(roleid))
        {
            return "角色已被使用，是否强制删除";
        }
        userPermissionManager.removeRole(roleid);
        appRoleCache.removeById(roleid);
        return null;
    }
    
    /**************************
     * 
     *    应用角色权限配置
     * 
     **************************/
    
    public List<RoleAccessInstance> listAppRoleAcl(String roleid)
    {
        List<RoleAccessInstance> p = userPermissionManager.getRoleAcl(roleid);
        return p;
    }
    
    public boolean isExistRole(String excludeDomainid, Set<String> roleKeySet)
    {
        return appRoleCache.isExistRole(excludeDomainid, roleKeySet);
    }
    
    public void delAllRole(String domainid)
    {
        List<AppRoleEntity> oldRoles = appRoleCache.select().eq("domainid", domainid).exec();
        for (AppRoleEntity r : oldRoles)
        {
            userPermissionManager.removeRole(r.getPkey());
        }
        appRoleCache.removeAll(oldRoles);
    }
    
    public void enableAppRole(String roleid, boolean enable)
    {
        AppRoleEntity role = appRoleCache.get(roleid);
        if (enable != role.isEnable())
        {
            role.setEnable(enable);
            appRoleCache.update(role);
            userPermissionManager.resetRoleAcl(roleid);
        }
    }
    
    public PageResult<AppRoleInfoOnPage> queryRole(Integer page, Integer pagesize, String domain, boolean onlySysRole)
    {
        return roleDao.queryRole(page, pagesize, domain, onlySysRole);
    }
    
    public RoleMenuTree getRoleMenu(String roleid)
    {
        RoleMenuTree tree = new RoleMenuTree();
        tree.setPkey(roleid);
        List<AppMenuTree> data = new ArrayList<>();
        AppRoleEntity role = appRoleCache.get(roleid);
        Map<String, Boolean> map = userPermissionManager.getRoleMenu(roleid);
        if (role.getDomainid() != null)
        {
            List<StrKeyName> appList = applicationDao.listFrontEndAppName(role.getDomainid());
            for (StrKeyName app : appList)
            {
                List<TreeModel<String, MenuType>> menus;
                if (role.getOrgid() == null && role.getDeptid() == null)
                {
                    menus = menuManager.listAllMenu(app.getPkey());
                }
                else
                {
                    menus = menuManager
                        .listMenuByDept(role.getDomainid(), app.getPkey(), role.getOrgid(), role.getDeptid());
                }
                for (TreeModel<String, MenuType> f : menus)
                {
                    f.updateSelected(map);
                }
                if (menus != null && !menus.isEmpty())
                {
                    AppMenuTree top = new AppMenuTree();
                    top.setPkey(app.getPkey());
                    top.setName(app.getName());
                    top.setSub(menus);
                    data.add(top);
                }
            }
        }
        tree.setData(data);
        return tree;
    }
    
    public void setRoleMenu(RoleMenuTree data)
    {
        String roleid = data.getPkey();
        RoleMenuTree tree = getRoleMenu(roleid);
        Map<String, Boolean> map = new HashMap<>();
        if (data.getData() != null && !data.getData().isEmpty())
        {
            for (int i = 0; i < data.getData().size(); i++)
            {
                AppMenuTree currentapp = tree.getData().get(i);
                AppMenuTree app = data.getData().get(i);
                TreeView.updateSelected(currentapp.getSub(), app.getSub());
                for (TreeModel<String, MenuType> m : app.getSub())
                {
                    m.collectSelected(map);
                }
            }
        }
        //只留下已选择的
        Iterator<Entry<String, Boolean>> iter = map.entrySet().iterator();
        while (iter.hasNext())
        {
            Entry<String, Boolean> e = iter.next();
            if (!e.getValue())
                iter.remove();
        }
        userPermissionManager.setRoleMenu(roleid, map);
    }
    
    public RoleAclTree getRoleFunc(String domain, String roleid, String funcGroup)
    {
        RoleAclTree result = new RoleAclTree();
        result.setPkey(roleid);
        result.setFuncGroup(funcGroup);
        List<FuncInfo> l = new ArrayList<>();
        Map<String, Boolean> map;
        if (roleid == null)
        {
            map = new HashMap<>();
        }
        else
        {
            AppRoleEntity role = appRoleCache.get(roleid);
            domain = role.getDomainid();
            map = userPermissionManager.getRoleAclMap(roleid);
        }
        if (domain == null)
        {
            l.addAll(functionManager.listFunc(Constant.NULLID, funcGroup));
        }
        else
        {
            l.add(new FuncInfo(SysFunctionEnum.managerUser));
            l.add(new FuncInfo(SysFunctionEnum.managerRole));
            l.add(new FuncInfo(SysFunctionEnum.managerOrg));
            l.addAll(functionManager.listFunc(domain, funcGroup));
        }
        List<TreeModel<String, String>> data = new ArrayList<>();
        for (FuncInfo f : l)
        {
            boolean disabled = false;
            if (domain == null)
                disabled = true;
            else
                disabled = !SecurityContextUtil.hasRight(f.getPkey(), domain);
            boolean selected = false;
            if (map.containsKey(f.getPkey()))
                selected = map.get(f.getPkey());
            
            TreeModel<String, String> node = new TreeModel<>(f.getPkey(), f.getName());
            node.setDisabled(disabled);
            node.setSelected(selected);
            data.add(node);
        }
        result.setData(data);
        return result;
    }
    
    public void setRoleFunc(String domain, RoleAclTree data)
    {
        String roleid = data.getPkey();
        String funcGroup = data.getFuncGroup();
        RoleAclTree tree = getRoleFunc(domain, roleid, funcGroup);
        TreeView.updateSelected(tree.getData(), data.getData());
        Map<String, Boolean> map = new HashMap<>();
        if (data.getData() != null && !data.getData().isEmpty())
        {
            for (TreeModel<String, String> t : data.getData())
            {
                if (t.isSelected())
                {
                    map.put(t.getPkey(), true);
                }
            }
        }
        userPermissionManager.setRoleAcl(roleid, map);
    }
    
    public RoleAclTree getRoleFuncTree(String domain, String roleid, String funcGroup)
    {
        RoleAclTree result = new RoleAclTree();
        result.setPkey(roleid);
        result.setFuncGroup(funcGroup);
        List<TreeModel<String, String>> data = new ArrayList<>();
        Map<String, Boolean> map;
        if (roleid == null)
        {
            map = new HashMap<>();
        }
        else
        {
            //角色
            AppRoleEntity role = appRoleCache.get(roleid);
            domain = role.getDomainid();
            //角色当前的权限
            map = userPermissionManager.getRoleAclMap(roleid);
        }
        List<StrKeyName> funcGroups = new ArrayList<>();
        List<FuncInfo> l = new ArrayList<>();
        if (domain == null)
        {
            funcGroups = functionManager.listFunctionGroup(Constant.NULLID, funcGroup);
            l.addAll(functionManager.listFunc(Constant.NULLID, funcGroup));
        }
        else
        {
            funcGroups.add(new StrKeyName("sys", "系统权限"));
            funcGroups.addAll(functionManager.listFunctionGroup(domain, funcGroup));
            l.add(new FuncInfo(SysFunctionEnum.managerUser));
            l.add(new FuncInfo(SysFunctionEnum.managerRole));
            l.add(new FuncInfo(SysFunctionEnum.managerOrg));
            l.addAll(functionManager.listFunc(domain, funcGroup));
        }
        
        //组Map
        Map<String, TreeModel<String, String>> groupMap = new HashMap<>();
        //默认组
        TreeModel<String, String> defaultGroup = null;
        //初始化组
        for (StrKeyName g : funcGroups)
        {
            TreeModel<String, String> node = new TreeModel<>(g.getPkey(), g.getName());
            node.setSelected(true);
            data.add(node);
            groupMap.put(g.getPkey(), node);
        }
        
        for (FuncInfo f : l)
        {
            boolean disabled = false;
            if (domain == null)
                disabled = true;
            else
                disabled = !SecurityContextUtil.hasRight(f.getPkey(), domain);
            boolean selected = false;
            if (map.containsKey(f.getPkey()))
                selected = map.get(f.getPkey());
            
            TreeModel<String, String> node = new TreeModel<>(f.getPkey(), f.getName());
            node.setDisabled(disabled);
            node.setSelected(selected);
            
            if (f.getFuncGroup() == null || !groupMap.containsKey(f.getFuncGroup()))
            {
                //加到默认组
                if (defaultGroup == null)
                {
                    defaultGroup = new TreeModel<>("def", "其他权限");
                    data.add(defaultGroup);
                }
                defaultGroup.addSub(node);
            }
            else
            {
                //加到组
                groupMap.get(f.getFuncGroup()).addSub(node);
            }
        }
        result.setData(data);
        return result;
    }
    
    public void setRoleFuncTree(String domain, RoleAclTree data)
    {
        String roleid = data.getPkey();
        String funcGroup = data.getFuncGroup();
        RoleAclTree tree = getRoleFuncTree(domain, roleid, funcGroup);
        TreeView.updateSelected(tree.getData(), data.getData());
        Map<String, Boolean> map = new HashMap<>();
        if (data.getData() != null && !data.getData().isEmpty())
        {
            //第一层是组
            for (TreeModel<String, String> sub : data.getData())
            {
                if (sub.getSub() != null && !sub.getSub().isEmpty())
                {
                    //第二层权限
                    for (TreeModel<String, String> t : sub.getSub())
                    {
                        if (t.isSelected())
                        {
                            map.put(t.getPkey(), true);
                        }
                    }
                }
            }
        }
        userPermissionManager.setRoleAcl(roleid, map);
    }
    
    public List<AppRoleInfo> listRoleInDept(String domain, String group, String deptid)
    {
        return roleDao.listRoleInDept(domain, group, deptid);
    }
}
