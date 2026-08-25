package cn.tofocus.account.db.dao.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import cn.tofocus.account.db.entity.user.RoleInstance.F;
import cn.tofocus.account.db.entity.user.RoleInstance;
import cn.tofocus.core.exception.SysErrCode;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.security.AccessScopeType;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.domain.user.def.RoleInstanceDTO;

@Component
public class RoleInstanceDao extends JpaSpecificationDelegate<String, RoleInstance>
{
    private Object[] ownerids(List<Long> userkeys)
    {
        Object[] array = new Object[userkeys.size()];
        int i = 0;
        for (Long userkey : userkeys)
        {
            if (userkey == null)
                throw TofocusException.of(SysErrCode.REQUIRED_PRARAM_NULL);
            else
                array[i] = userkey.toString();
            i++;
        }
        return array;
    }
    
    public List<Long> findUserByRole(String roleid)
    {
        // @formatter:off
        List<RoleInstance> list = this.select()
            .and()
               .eq(F.value, roleid)
               .or()
                  .isNull(F.expireTime)
                  .gt(F.expireTime, new Date())
               .close()
            .done()
            .exec();
        // @formatter:on
        List<Long> keys = new ArrayList<>();
        for (RoleInstance roleInst : list)
        {
            keys.add(Long.parseLong(roleInst.getOwnerid()));
        }
        return keys;
    }
    
    public List<RoleInstance> listByDomain(String domain)
    {
        return this.select().strict(true).eq(F.domainid, domain).exec();
    }
    
    public List<Long> findUserByRoles(Collection<String> roleids)
    {
        // @formatter:off
        Map<String, Long> list = this.aggregation()
            .and()
               .in(F.value, roleids)
               .or()
                  .isNull(F.expireTime)
                  .gt(F.expireTime, new Date())
               .close()
            .done()
            .execGroupByCountDistinct(F.ownerid, F.ownerid);
        // @formatter:on
        List<Long> keys = new ArrayList<>();
        for (String u : list.keySet())
        {
            keys.add(Long.parseLong(u));
        }
        return keys;
    }
    
    public List<RoleInstance> listByUserAndRoleInDept(List<Long> userkeys, List<String> roles)
    {
        // @formatter:off
        List<RoleInstance> list = this.select().strict(true)
            .and()
               .in(F.ownerid, ownerids(userkeys))
               .eq(F.scopeType, AccessScopeType.dept)
               .in(F.value, roles)
               .or()
                  .isNull(F.expireTime)
                  .gt(F.expireTime, new Date())
               .close()
            .done()
            .exec();
        // @formatter:on
        return list;
    }

    public List<RoleInstance> listAllByDeptScope(String deptid)
    {
        // @formatter:off
        List<RoleInstance> list = this.select().strict(true)
            .and()
               .eq(F.scope, deptid)
               .eq(F.scopeType, AccessScopeType.dept)
            .done()
            .exec();
        // @formatter:on
        return list;
    }
    
    public List<RoleInstance> listByRoleAndDeptScope(List<String> depts, List<String> roles)
    {
        // @formatter:off
        List<RoleInstance> list = this.select().strict(true)
            .and()
               .in(F.scope, depts)
               .eq(F.scopeType, AccessScopeType.dept)
               .in(F.value, roles)
               .or()
                  .isNull(F.expireTime)
                  .gt(F.expireTime, new Date())
               .close()
            .done()
            .exec();
        // @formatter:on
        return list;
    }

    public List<RoleInstance> listByRoleAndOrgScope(List<String> orgs, List<String> roles)
    {
        // @formatter:off
        List<RoleInstance> list = this.select().strict(true)
            .and()
               .in(F.scope, orgs)
               .eq(F.scopeType, AccessScopeType.org)
               .in(F.value, roles)
               .or()
                  .isNull(F.expireTime)
                  .gt(F.expireTime, new Date())
               .close()
            .done()
            .exec();
        // @formatter:on
        return list;
    }

    public List<RoleInstanceDTO> listByUserAndRole(Long userkey, String role)
    {
        // @formatter:off
        List<RoleInstanceDTO> list = this.select().strict(true)
            .and()
               .eq(F.ownerid, userkey)
               .eq(F.value, role)
               .or()
                  .isNull(F.expireTime)
                  .gt(F.expireTime, new Date())
               .close()
            .done()
            .execDto(RoleInstanceDTO.class);
        // @formatter:on
        return list;
    }

    public List<RoleInstance> listByRole(List<String> roles)
    {
        // @formatter:off
        List<RoleInstance> list = this.select().strict(true)
            .and()
               .in(F.value, roles)
               .or()
                  .isNull(F.expireTime)
                  .gt(F.expireTime, new Date())
               .close()
            .done()
            .exec();
        // @formatter:on
        return list;
    }
}
