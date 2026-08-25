package cn.tofocus.account.db.dao.role;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import cn.tofocus.account.db.entity.role.RoleAccessInstance.F;
import cn.tofocus.account.db.entity.role.RoleAccessInstance;
import cn.tofocus.core.user.SysFunctionEnum;
import cn.tofocus.core.user.SysRoleEnum;
import cn.tofocus.db.jpa.dao.ComposedDao;

@Component
public class RoleAccessDao extends ComposedDao<String, RoleAccessInstance>
{
    
    @Override
    protected Map<String, RoleAccessInstance> iniMap()
    {
        Map<String, RoleAccessInstance> map = new HashMap<>();
        
        //初始化系统角色权限
        map.putAll(iniRoleAcl(SysRoleEnum.sysAdmin,
            SysFunctionEnum.managerApplication,
            SysFunctionEnum.managerRole,
            SysFunctionEnum.managerFunction,
            SysFunctionEnum.managerMenu,
            SysFunctionEnum.domainAdmin,
            SysFunctionEnum.managerUser,
            SysFunctionEnum.managerOrg));
        
        map.putAll(iniRoleAcl(SysRoleEnum.oauth2Admin, SysFunctionEnum.managerApplication));
        
        map.putAll(iniRoleAcl(SysRoleEnum.domainAdmin,
            SysFunctionEnum.managerRole,
            SysFunctionEnum.managerFunction,
            SysFunctionEnum.managerMenu,
            SysFunctionEnum.domainAdmin,
            SysFunctionEnum.managerUser,
            SysFunctionEnum.managerOrg));
        return map;
    }
    
    private Map<String, RoleAccessInstance> iniRoleAcl(SysRoleEnum role, SysFunctionEnum... functions)
    {
        Map<String, RoleAccessInstance> map = new HashMap<>();
        for (int i = 0; i < functions.length; i++)
        {
            SysFunctionEnum function = functions[i];
            RoleAccessInstance ac = new RoleAccessInstance(null, "sys" + "_" + role.name() + "_" + function.name(),
                role.name(), function.name(), true);
            map.put(ac.getPkey(), ac);
        }
        return map;
    }

    public List<RoleAccessInstance> listByDomain(String domain)
    {
        return this.select().strict(true).eq(F.domainid, domain).exec();
    }

    public boolean isFuncUsed(String funcid)
    {
        return this.selectOne().strict(true).eq(F.funcKey, funcid).exec() != null;
    }
}
