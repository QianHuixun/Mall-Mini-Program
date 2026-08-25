package cn.tofocus.account.command.bean;

import java.util.List;
import cn.tofocus.account.db.entity.application.AppLoginCheckEntity;
import cn.tofocus.account.db.entity.application.MenuEntity;
import cn.tofocus.account.db.entity.domain.ModelEntity;
import cn.tofocus.account.db.entity.org.DeptMenuEntity;
import cn.tofocus.account.db.entity.org.DeptModelEntity;
import cn.tofocus.account.db.entity.org.OrgMenuEntity;
import cn.tofocus.account.db.entity.org.OrgModelEntity;
import cn.tofocus.account.db.entity.role.AppFunctionEntity;
import cn.tofocus.account.db.entity.role.AppRoleEntity;
import cn.tofocus.account.db.entity.role.RoleAccessInstance;
import cn.tofocus.account.db.entity.role.RoleMenuEntity;
import cn.tofocus.account.db.entity.user.AccessInstance;
import cn.tofocus.account.db.entity.user.RoleInstance;
import lombok.Data;

@Data
public class DomainAllData
{
    private List<AppRoleEntity> roles;
    
    private List<AppFunctionEntity> functions;
    
    private List<ModelEntity> models;
    
    private List<MenuEntity> menus;
    
    private List<AppLoginCheckEntity> appChecks;
    
    private List<RoleInstance> userRoles;
    
    private List<AccessInstance> userFuncs;
    
    private List<RoleAccessInstance> roleFuncs;
    
    private List<RoleMenuEntity> roleMenus;
    
    private List<OrgModelEntity> orgModels;
    
    private List<OrgMenuEntity> orgMenus;
    
    private List<DeptModelEntity> deptModels;
    
    private List<DeptMenuEntity> deptMenus;
}
