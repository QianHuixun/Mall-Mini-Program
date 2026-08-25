package cn.tofocus.domain.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.account.bean.AppMenu;
import cn.tofocus.account.bean.application.MenuInfo;
import cn.tofocus.account.bean.application.ModelInfo;
import cn.tofocus.account.db.dao.application.ApplicationDao;
import cn.tofocus.account.db.dao.application.MenuDao;
import cn.tofocus.account.db.dao.org.DeptMenuDao;
import cn.tofocus.account.db.dao.org.OrgMenuDao;
import cn.tofocus.account.db.entity.application.MenuEntity;
import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.core.data.StrKeyName;
import cn.tofocus.core.data.TreeModel;
import cn.tofocus.core.data.TreeViewBuilder;
import cn.tofocus.core.enums.MenuType;
import cn.tofocus.core.exception.SysErrCode;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.security.AccessList;
import cn.tofocus.core.user.SysFunctionEnum;

@Component
public class MenuManager
{
    @Autowired
    private MenuDao menuCache;
    
    @Autowired
    private UserPermissionManager userPermissionManager;
    
    @Autowired
    private ModelManager modelManager;
    
    @Autowired
    private OrgMenuDao orgMenuDao;
    
    @Autowired
    private DeptMenuDao deptMenuDao;
    
    private MenuComparator menuComparator = new MenuComparator();
    
    @Autowired
    private ApplicationDao applicationDao;
    
    private class MenuComparator implements Comparator<MenuEntity>
    {
        @Override
        public int compare(MenuEntity o1, MenuEntity o2)
        {
            if (o1 == null || o1.getSort() == null)
                return 1;
            else if (o2 == null || o2.getSort() == null)
                return -1;
            else
                return o1.getSort() - o2.getSort();
        }
    }
    
    public List<AppMenu> findMyMenuByApp(Long userkey, String domainid, String appid, String orgid, String deptid)
    {
        List<MenuEntity> list = menuCache.listEnableMenuByApp(appid);
        Collections.sort(list, menuComparator);
        filterByUser(list, userkey, domainid);
        filterByModel(list, domainid, appid, orgid, deptid);
        return menu2AppMenu(list);
    }

    public List<AppMenu> findAllMenuByApp(String appid)
    {
        List<MenuEntity> list = menuCache.listEnableMenuByApp(appid);
        Collections.sort(list, menuComparator);
        return menu2AppMenu(list);
    }
    
    private void filterByModel(List<MenuEntity> list, String domainid, String appid, String orgid, String deptid)
    {
        Map<String, ModelInfo> models = modelManager.activeModels(domainid, orgid, deptid);
        Map<String, Set<String>> orgDisableMenus = new HashMap<>();
        Map<String, Set<String>> deptDisableMenus = new HashMap<>();
        Map<String, Set<String>> orgEnableMenus = new HashMap<>();
        if (orgid != null)
        {
            orgDisableMenus = orgMenuDao.findDisableMenus(appid, orgid);
            orgEnableMenus = orgMenuDao.findEnableMenus(appid, orgid);
            if (deptid != null)
                deptDisableMenus = deptMenuDao.findDisableMenus(appid, deptid);
        }
        
        Iterator<MenuEntity> iter = list.iterator();
        while (iter.hasNext())
        {
            MenuEntity e = iter.next();
            String model = e.getModelId();
            String menu = e.getPkey();
            boolean defShowMenu = true;
            if (model != null && !models.containsKey(model))
                iter.remove();
            else
            {
                if (model != null && models.containsKey(model))
                    defShowMenu = models.get(model).isDefShowMenu();
                if (defShowMenu)
                {
                    Set<String> orgDisables = orgDisableMenus.get(model);
                    Set<String> deptDisables = deptDisableMenus.get(model);
                    if ((orgDisables != null && orgDisables.contains(menu))
                        || (deptDisables != null && deptDisables.contains(menu)))
                        iter.remove();
                }
                else
                {
                    Set<String> orgEnable = orgEnableMenus.get(model);
                    Set<String> deptDisables = deptDisableMenus.get(model);
                    if ((orgEnable == null || !orgEnable.contains(menu))
                        || (deptDisables != null && deptDisables.contains(menu)))
                        iter.remove();
                }
            }
        }
    }
    
    private void filterByUser(List<MenuEntity> list, Long userkey, String domainid)
    {
        Map<String, AccessList> aclmap = userPermissionManager.getCachedUserAcls(userkey);
        
        AccessList acl = aclmap.get(SysFunctionEnum.domainAdmin.name());
        if (acl != null && acl.canAccessDomain(domainid))
        {
            return;
        }
        
        Map<String, Boolean> userMemuMap = userPermissionManager.getCachedUserMenus(userkey);
        Iterator<MenuEntity> iter = list.iterator();
        while (iter.hasNext())
        {
            MenuEntity e = iter.next();
            if (!e.getType().equals(MenuType.model))
            {
                Boolean b = userMemuMap.get(e.getPkey());
                if (b == null || !b)
                    iter.remove();
            }
        }
    }
    
    private List<AppMenu> menu2AppMenu(List<MenuEntity> list)
    {
        //返回的顶级菜单列表
        List<AppMenu> top = new ArrayList<>();
        //菜单上下级关系
        Map<String, List<AppMenu>> map = new HashMap<>();
        //所有菜单
        Map<String, AppMenu> all = new HashMap<>();
        Map<String, String> parentmap = new HashMap<>();
        //转换list到map
        for (MenuEntity menu : list)
        {
            if (!menu.isEnable())
                continue;
            AppMenu m = BeanUtil.beanFrom(AppMenu.class, menu);
            all.put(menu.getPkey(), m);
            
            //构造上下级关系
            String parentid = menu.getParentid();
            parentmap.put(menu.getPkey(), parentid);
            map.computeIfAbsent(parentid, k -> new ArrayList<>()).add(m);
        }
        //清理空model
        Iterator<Entry<String, AppMenu>> iter = all.entrySet().iterator();
        while (iter.hasNext())
        {
            Entry<String, AppMenu> entry = iter.next();
            AppMenu v = entry.getValue();
            if (MenuType.model.equals(v.getType()))
            {
                List<AppMenu> s = map.get(entry.getKey());
                if (s == null || s.isEmpty())
                {
                    String parentid = parentmap.get(entry.getKey());
                    List<AppMenu> sublist = map.get(parentid);
                    if (sublist != null)
                    {
                        sublist.remove(v);
                    }
                    iter.remove();
                }
            }
        }
        //构造树形结构
        for (Entry<String, AppMenu> m : all.entrySet())
        {
            if (map.containsKey(m.getKey()))
            {
                AppMenu menu = m.getValue();
                List<AppMenu> sub = new ArrayList<>();
                List<AppMenu> button = new ArrayList<>();
                for (AppMenu subm : map.get(m.getKey()))
                {
                    if (MenuType.button.equals(subm.getType()))
                        button.add(subm);
                    else
                        sub.add(subm);
                }
                if (!sub.isEmpty())
                    menu.setSub(sub);
                if (!button.isEmpty())
                    menu.setButtons(button);
            }
        }
        if (map.containsKey(null))
        {
            top = map.get(null);
        }
        return top;
    }
    
    public List<TreeModel<String, MenuInfo>> queryMenu(String application, boolean defSelected)
    {
        List<MenuEntity> list = menuCache.listByApp(application);
        return buildTree(list, application, k -> BeanUtil.beanFrom(MenuInfo.class, k), defSelected);
    }
    
    public List<TreeModel<String, MenuType>> listParentMenu(String application, MenuType type)
    {
        List<MenuEntity> list = menuCache.listByApp(application);
        Iterator<MenuEntity> iter = list.iterator();
        while(iter.hasNext())
        {
            MenuEntity m = iter.next();
            switch(type)
            {
                case model:
                case menu:
                    if(!m.getType().equals(MenuType.model))
                        iter.remove();
                    break;
                case button:
                    if(m.getType().equals(MenuType.button))
                        iter.remove();
                    if(m.getType().equals(MenuType.model))
                        m.setEnable(false);
                    break;
                default:
                    break;
            }
        }
        return buildTree(list, application, MenuEntity::getType, false);
    }
    
    private List<TreeModel<String, MenuType>> listMenuByModel(String application, String model, boolean defSelected)
    {
        List<MenuEntity> list = menuCache.listByApp(application, model);
        List<TreeModel<String, MenuType>> l = buildTree(list, application, MenuEntity::getType, defSelected);
        clearEmptyModel(l);
        return l;
    }

    public List<TreeModel<String, MenuType>> listAllMenu(String appid)
    {
        List<MenuEntity> list = menuCache.listEnableMenuByApp(appid);
        Collections.sort(list, menuComparator);
        List<TreeModel<String, MenuType>> l = buildTree(list, appid, MenuEntity::getType, false);
        clearEmptyModel(l);
        return l;
    }
    
    public List<TreeModel<String, MenuType>> listMenuByDept(String domainid, String appid, String orgid, String deptid)
    {
        List<MenuEntity> list = menuCache.listEnableMenuByApp(appid);
        Collections.sort(list, menuComparator);
        filterByModel(list, domainid, appid, orgid, deptid);
        List<TreeModel<String, MenuType>> l = buildTree(list, appid, MenuEntity::getType, false);
        clearEmptyModel(l);
        return l;
    }
    
    private void clearEmptyModel(List<TreeModel<String, MenuType>> list)
    {
        Iterator<TreeModel<String, MenuType>> iter = list.iterator();
        while (iter.hasNext())
        {
            TreeModel<String, MenuType> entry = iter.next();
            MenuType v = entry.getValue();
            if (!entry.isLeaf())
                clearEmptyModel(entry.getSub());
            if (MenuType.model.equals(v) && entry.isLeaf())
            {
                iter.remove();
            }
        }
    }
    
    private <T> List<TreeModel<String, T>> buildTree(List<MenuEntity> list, String application,
        Function<MenuEntity, T> infoFunc, boolean defSelected)
    {
        Collections.sort(list, menuComparator);
        TreeViewBuilder<String, String, T> builder = new TreeViewBuilder<>();
        builder.pkey(application);
        builder.name(application + "的菜单");
        for (MenuEntity menu : list)
        {
            T info = null;
            if (infoFunc != null)
                info = infoFunc.apply(menu);
            builder.addNode(menu.getPkey(),
                menu.getParentid(),
                menu.getName(),
                menu.isEnable() ? defSelected : false,
                !menu.isEnable(),
                info,
                menu.getSort());
        }
        return builder.build().getData();
    }
    
    public MenuEntity getMenu(String pkey)
    {
        return menuCache.get(pkey);
    }
    
    public void saveMenu(MenuEntity newMenu)
    {
        menuCache.put(newMenu);
    }
    
    public String delMenu(String pkey, boolean force)
    {
        if (!force)
        {
            if (!userPermissionManager.countRoleByMenu(pkey).isEmpty())
            {
                return "菜单已配置到角色，是否强制删除";
            }
            menuCache.removeById(pkey);
        }
        else
        {
            orgMenuDao.delByMenu(pkey);
            deptMenuDao.delByMenu(pkey);
            userPermissionManager.delByMenu(pkey);
            menuCache.removeById(pkey);
        }
        return null;
    }
    
    public List<TreeModel<String, MenuType>> listMenuConfigByOrg(String domainid, String orgid, String model,
        String application)
    {
        boolean match = false;
        List<StrKeyName> appList = applicationDao.listFrontEndAppName(domainid);
        for (StrKeyName app : appList)
        {
            if (application.equals(app.getPkey()))
                match = true;
        }
        if (!match)
            throw TofocusException.of(SysErrCode.REQUIRED_PRARAM_ERROR);
        
        boolean defShow = modelManager.getModel(model).isDefShowMenu();
        List<TreeModel<String, MenuType>> menus;
        if (defShow)
        {
            Set<String> unselected = orgMenuDao.findDisableMenusByModel(orgid, model);
            menus = listMenuByModel(application, model, defShow);
            for (TreeModel<String, MenuType> f : menus)
            {
                f.updateUnSelected(unselected);
            }
        }
        else
        {
            Set<String> selected = orgMenuDao.findEnableMenusByModel(orgid, model);
            menus = listMenuByModel(application, model, defShow);
            for (TreeModel<String, MenuType> f : menus)
            {
                f.updateSelected(selected);
            }
        }
        return menus;
    }
    
    public void updateMenuConfigByOrg(String domainid, String orgid, String model, String application,
        List<TreeModel<String, MenuType>> data)
    {
        List<TreeModel<String, MenuType>> current = listMenuConfigByOrg(domainid, orgid, model, application);
        boolean defShow = modelManager.getModel(model).isDefShowMenu();
        if (defShow)
        {
            Set<String> unselectedMenu = new HashSet<>();
            mergeUnselectedMenuConfigsInApp(current, data, unselectedMenu);
            orgMenuDao.updateConfig(domainid, orgid, model, application, unselectedMenu, false);
        }
        else
        {
            Set<String> selectedMenu = new HashSet<>();
            mergeSelectedMenuConfigsInApp(current, data, selectedMenu);
            orgMenuDao.updateConfig(domainid, orgid, model, application, selectedMenu, true);
        }
    }
    
    public List<TreeModel<String, MenuType>> listMenuConfigByDept(String domainid, String orgid, String deptid,
        String model, String application)
    {
        boolean match = false;
        List<StrKeyName> appList = applicationDao.listFrontEndAppName(domainid);
        for (StrKeyName app : appList)
        {
            if (application.equals(app.getPkey()))
                match = true;
        }
        if (!match)
            throw TofocusException.of(SysErrCode.REQUIRED_PRARAM_ERROR);
        
        boolean defShow = modelManager.getModel(model).isDefShowMenu();
        List<TreeModel<String, MenuType>> menus;
        if (defShow)
        {
            Set<String> disabled = orgMenuDao.findDisableMenusByModel(orgid, model);
            Set<String> unselected = deptMenuDao.findDisableMenusByModel(deptid, model);
            menus = listMenuByModel(application, model, defShow);
            for (TreeModel<String, MenuType> f : menus)
            {
                f.updateDisabledAndUnSelected(disabled);
            }
            for (TreeModel<String, MenuType> f : menus)
            {
                f.updateUnSelected(unselected);
            }
        }
        else
        {
            menus = listMenuByModel(application, model, true);
            Set<String> disabled = new HashSet<>();
            menuCache.listByApp(application, model).forEach(k -> disabled.add(k.getPkey()));
            disabled.removeAll(orgMenuDao.findEnableMenusByModel(orgid, model));
            Set<String> unselected = deptMenuDao.findDisableMenusByModel(deptid, model);
            for (TreeModel<String, MenuType> f : menus)
            {
                f.updateDisabledAndUnSelected(disabled);
            }
            for (TreeModel<String, MenuType> f : menus)
            {
                f.updateUnSelected(unselected);
            }
        }
        return menus;
    }
    
    public void updateMenuConfigByDept(String domainid, String orgid, String deptid, String model, String application,
        List<TreeModel<String, MenuType>> data)
    {
        List<TreeModel<String, MenuType>> current = listMenuConfigByDept(domainid, orgid, deptid, model, application);
        Set<String> unselectedMenu = new HashSet<>();
        mergeUnselectedMenuConfigsInApp(current, data, unselectedMenu);
        deptMenuDao.updateConfig(domainid, orgid, deptid, model, application, unselectedMenu);
    }
    
    private void mergeSelectedMenuConfigsInApp(List<TreeModel<String, MenuType>> current,
        List<TreeModel<String, MenuType>> list, Set<String> selectedMenu)
    {
        if (current.size() != list.size())
            throw TofocusException.of(SysErrCode.REQUIRED_PRARAM_ERROR);
        for (int i = 0; i < current.size(); i++)
        {
            TreeModel<String, MenuType> c = current.get(i);
            TreeModel<String, MenuType> n = list.get(i);
            if (!c.getPkey().equals(n.getPkey()))
                throw TofocusException.of(SysErrCode.REQUIRED_PRARAM_ERROR);
            if (!c.isDisabled() && n.isSelected() && !c.getValue().equals(MenuType.model))
                selectedMenu.add(n.getPkey());
            if (c.getSub() != null && n.getSub() != null)
                mergeSelectedMenuConfigsInApp(c.getSub(), n.getSub(), selectedMenu);
        }
    }
    
    private void mergeUnselectedMenuConfigsInApp(List<TreeModel<String, MenuType>> current,
        List<TreeModel<String, MenuType>> list, Set<String> unselectedMenu)
    {
        if (current.size() != list.size())
            throw TofocusException.of(SysErrCode.REQUIRED_PRARAM_ERROR);
        for (int i = 0; i < current.size(); i++)
        {
            TreeModel<String, MenuType> c = current.get(i);
            TreeModel<String, MenuType> n = list.get(i);
            if (!c.getPkey().equals(n.getPkey()))
                throw TofocusException.of(SysErrCode.REQUIRED_PRARAM_ERROR);
            if (!c.isDisabled() && !n.isSelected() && !c.getValue().equals(MenuType.model))
                unselectedMenu.add(n.getPkey());
            if (c.getSub() != null && n.getSub() != null)
                mergeUnselectedMenuConfigsInApp(c.getSub(), n.getSub(), unselectedMenu);
        }
    }
}
