package cn.tofocus.account.api.v4;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.account.bean.AppMenu;
import cn.tofocus.account.bean.MenuConfig;
import cn.tofocus.account.bean.application.AppKV;
import cn.tofocus.account.bean.application.MenuForUpd;
import cn.tofocus.account.bean.application.MenuInfo;
import cn.tofocus.account.db.cache.application.AppReadCache;
import cn.tofocus.account.db.cache.domain.DomainReadCache;
import cn.tofocus.account.db.entity.application.MenuEntity;
import cn.tofocus.account.db.entity.domain.ModelEntity;
import cn.tofocus.account.db.entity.org.DepartmentEntity;
import cn.tofocus.account.exception.AccErrCode;
import cn.tofocus.core.Result;
import cn.tofocus.core.data.TreeModel;
import cn.tofocus.core.enums.MenuType;
import cn.tofocus.core.exception.SysErrCode;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.security.SecurityContextUtil;
import cn.tofocus.core.security.aop.CheckAppPermission;
import cn.tofocus.core.security.aop.CheckAppPermission.ScopeCheckType;
import cn.tofocus.core.user.SysFunctionEnum;
import cn.tofocus.domain.manager.MenuManager;
import cn.tofocus.domain.manager.ModelManager;
import cn.tofocus.domain.manager.OrginazationManager;

@RequestMapping("/v4/menu")
@RestController
public class MenuApiV4Impl implements MenuApiV4
{
    private static final String PermFunction = SysFunctionEnum.ManagerMenu;
    
    private static final String ManagerOrgFunction = SysFunctionEnum.ManagerOrg;
    
    @Autowired
    private MenuManager menuManager;
    
    @Autowired
    private DomainReadCache domainReadCache;
    
    @Autowired
    private ModelManager modelManager;
    
    @Autowired
    private OrginazationManager orginazationManager;
    
    @Autowired
    private AppReadCache appReadCache;
    
    @Override
    @CheckAppPermission(value = PermFunction, checkType = ScopeCheckType.current)
    public Result<List<TreeModel<String, MenuInfo>>> queryMenu(String application)
    {
        List<TreeModel<String, MenuInfo>> t = menuManager.queryMenu(application, false);
        return new Result<>(t);
    }
    
    @Override
    @CheckAppPermission(value = PermFunction, checkType = ScopeCheckType.current)
    public Result<List<TreeModel<String, MenuType>>> listParentMenu(String application, MenuType type)
    {
        List<TreeModel<String, MenuType>> t = menuManager.listParentMenu(application, type);
        return new Result<>(t);
    }
    
    @Override
    @CheckAppPermission(value = PermFunction, checkType = ScopeCheckType.current)
    public Result<Boolean> addMenu(@Valid MenuInfo info)
    {
        if (!domainReadCache.isExistKey(info.getDomainid()))
            throw TofocusException.of(SysErrCode.REQUIRED_PRARAM_ERROR, info.getDomainid() + " 域不存在");
        MenuEntity current = menuManager.getMenu(info.getPkey());
        if (current != null)
            throw TofocusException.of(SysErrCode.Cache.KEY_IS_EXIST);
        
        if (info.getModelId() != null)
        {
            ModelEntity model = modelManager.getModel(info.getModelId());
            if (model == null)
                throw TofocusException.of(AccErrCode.MODEL_NOT_EXIST);
        }
        if (info.getParentid() != null)
        {
            MenuEntity parent = menuManager.getMenu(info.getParentid());
            if (parent == null)
                throw TofocusException.of(AccErrCode.MENU_NOT_EXIST);
        }
        
        MenuEntity newMenu = new MenuEntity();
        newMenu.setPkey(info.getPkey());
        newMenu.setDomainid(info.getDomainid());
        newMenu.setName(info.getName());
        newMenu.setAppid(info.getAppid());
        newMenu.setDescription(info.getDescription());
        newMenu.setSort(info.getSort());
        newMenu.setModelId(info.getModelId());
        newMenu.setParentid(info.getParentid());
        newMenu.setType(info.getType());
        newMenu.setEnable(info.isEnable());
        SecurityContextUtil.checkRight(PermFunction, newMenu);
        menuManager.saveMenu(newMenu);
        return new Result<>(true);
    }
    
    @Override
    @CheckAppPermission(value = PermFunction, checkType = ScopeCheckType.current)
    public Result<Boolean> updMenu(@Valid MenuForUpd info)
    {
        MenuEntity current = menuManager.getMenu(info.getPkey());
        if (current == null)
            throw TofocusException.of(SysErrCode.Cache.KEY_IS_NOT_EXIST);
        SecurityContextUtil.checkRight(PermFunction, current);
        
        if (info.getModelId() != null)
        {
            ModelEntity model = modelManager.getModel(info.getModelId());
            if (model == null)
                throw TofocusException.of(AccErrCode.MODEL_NOT_EXIST);
        }
        if (info.getParentid() != null)
        {
            MenuEntity parent = menuManager.getMenu(info.getParentid());
            if (parent == null)
                throw TofocusException.of(AccErrCode.MENU_NOT_EXIST);
        }
        current.setName(info.getName());
        current.setDescription(info.getDescription());
        current.setSort(info.getSort());
        current.setModelId(info.getModelId());
        current.setParentid(info.getParentid());
        current.setEnable(info.isEnable());
        
        menuManager.saveMenu(current);
        return new Result<>(true);
    }
    
    @Override
    @CheckAppPermission(value = PermFunction, checkType = ScopeCheckType.current)
    public Result<String> delMenu(String pkey, boolean force)
    {
        MenuEntity current = menuManager.getMenu(pkey);
        if (current != null)
        {
            SecurityContextUtil.checkRight(PermFunction, current);
            String errMsg = menuManager.delMenu(pkey, force);
            return new Result<>(errMsg);
        }
        else
            return new Result<>();
    }
    
    @Override
    @CheckAppPermission(value = PermFunction, checkType = ScopeCheckType.current)
    public Result<Boolean> setMenuEnable(String pkey, boolean enable)
    {
        MenuEntity current = menuManager.getMenu(pkey);
        if (current == null)
            throw TofocusException.of(SysErrCode.Cache.KEY_IS_NOT_EXIST);
        SecurityContextUtil.checkRight(PermFunction, current);
        current.setEnable(enable);
        menuManager.saveMenu(current);
        return new Result<>(true);
    }
    
    @Override
    @CheckAppPermission(value = ManagerOrgFunction)
    public Result<MenuConfig<String>> listMenuConfigByOrg(String orgid, String model, String application)
    {
        String domainid = SecurityContextUtil.currentDomain();
        SecurityContextUtil.checkRight(ManagerOrgFunction, domainid, orgid, null);
        List<TreeModel<String, MenuType>> data = menuManager.listMenuConfigByOrg(domainid, orgid, model, application);
        return new Result<>(new MenuConfig<String>(orgid, model, application, data));
    }
    
    @Override
    @CheckAppPermission(value = ManagerOrgFunction)
    public Result<Boolean> setMenuConfigByOrg(MenuConfig<String> data)
    {
        String domainid = SecurityContextUtil.currentDomain();
        String orgid = data.getOwner();
        SecurityContextUtil.checkRight(ManagerOrgFunction, domainid, orgid, null);
        menuManager.updateMenuConfigByOrg(domainid, orgid, data.getModel(), data.getApplication(), data.getData());
        return new Result<>(true);
    }
    
    @Override
    @CheckAppPermission(value = ManagerOrgFunction)
    public Result<MenuConfig<String>> listMenuConfigByDept(String deptid, String model, String application)
    {
        String domainid = SecurityContextUtil.currentDomain();
        DepartmentEntity dept = orginazationManager.getDepartment(deptid);
        SecurityContextUtil.checkRight(ManagerOrgFunction, domainid, dept.getOrgid(), deptid);
        List<TreeModel<String, MenuType>> data =
            menuManager.listMenuConfigByDept(domainid, dept.getOrgid(), deptid, model, application);
        return new Result<>(new MenuConfig<String>(deptid, model, application, data));
    }
    
    @Override
    @CheckAppPermission(value = ManagerOrgFunction)
    public Result<Boolean> setMenuConfigByDept(MenuConfig<String> data)
    {
        String domainid = SecurityContextUtil.currentDomain();
        String deptid = data.getOwner();
        DepartmentEntity dept = orginazationManager.getDepartment(deptid);
        SecurityContextUtil.checkRight(ManagerOrgFunction, domainid, dept.getOrgid(), deptid);
        menuManager.updateMenuConfigByDept(domainid,
            dept.getOrgid(),
            deptid,
            data.getModel(),
            data.getApplication(),
            data.getData());
        return new Result<>(true);
    }
    
    @Override
    public Result<List<AppMenu>> allAppMenu(String application)
    {
        List<AppMenu> mlist = new ArrayList<>();
        String domainid = SecurityContextUtil.currentDomain();
        AppKV app = appReadCache.get(application);
        if (app != null && app.getDomainid().equals(domainid))
        {
            mlist = menuManager.findAllMenuByApp(application);
        }
        return new Result<>(mlist);
    }
}
