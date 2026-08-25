package cn.tofocus.domain.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.account.bean.org.DeptKV;
import cn.tofocus.account.bean.org.OrgKV;
import cn.tofocus.account.db.cache.application.AppReadCache;
import cn.tofocus.account.db.cache.org.DeptReadCache;
import cn.tofocus.account.db.cache.org.OrgReadCache;
import cn.tofocus.account.db.cache.role.AppFunctionCache;
import cn.tofocus.account.db.cache.role.AppRoleCache;
import cn.tofocus.account.db.cache.role.RoleAclCache;
import cn.tofocus.account.db.cache.user.AclMap;
import cn.tofocus.account.db.cache.user.UserAclCache;
import cn.tofocus.account.db.cache.user.UserCache;
import cn.tofocus.account.db.cache.user.UserMenuMap;
import cn.tofocus.account.db.cache.user.UserRoleCache;
import cn.tofocus.account.db.dao.role.RoleMenuDao;
import cn.tofocus.account.db.dao.user.RoleInstanceDao;
import cn.tofocus.account.db.entity.role.AppFunctionEntity;
import cn.tofocus.account.db.entity.role.AppRoleEntity;
import cn.tofocus.account.db.entity.role.RoleAccessInstance;
import cn.tofocus.account.db.entity.role.RoleMenuEntity;
import cn.tofocus.account.db.entity.user.AccessInstance;
import cn.tofocus.account.db.entity.user.RoleInstance;
import cn.tofocus.account.db.entity.user.UserEntity;
import cn.tofocus.account.exception.AccErrCode;
import cn.tofocus.common.util.CollectionUtil;
import cn.tofocus.common.util.Util;
import cn.tofocus.core.exception.SysErrCode;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.security.AccessInstanceDTO;
import cn.tofocus.core.security.AccessList;
import cn.tofocus.core.security.AccessListWhole;
import cn.tofocus.core.security.AccessScopeType;
import cn.tofocus.core.security.AclUtil;
import cn.tofocus.core.security.SecurityContextUtil;
import cn.tofocus.core.user.SysFunctionEnum;
import cn.tofocus.core.user.SysRoleEnum;
import cn.tofocus.db.ConditionBuilder;
import cn.tofocus.db.SelectBuilder;
import cn.tofocus.db.redis.lock.RedisLockTemplate;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class UserPermissionManager
{
    private static final String DOMAIN = "tfcloud";
    
    private static final String APP = "acc";
    
    private static final String USER = "user";
    
    //用户最终权限缓存
    @Autowired
    private AclMap aclMap;
    
    //用户最终菜单缓存
    @Autowired
    private UserMenuMap userMenuMap;
    
    //用户配置的角色
    @Autowired
    private UserRoleCache userRoleCache;
    
    @Autowired
    private RoleInstanceDao userRoleDao;
    
    //用户直接配置的权限
    @Autowired
    private UserAclCache userAclCache;
    
    //角色配置的权限
    @Autowired
    private RoleAclCache roleAclCache;
    
    @Autowired
    private RoleMenuDao roleMenuDao;
    
    @Autowired
    private AppFunctionCache functionCache;
    
    @Autowired
    private AclUtil aclUtil;
    
    @Autowired
    private AppRoleCache roleCache;
    
    @Autowired
    private UserCache users;
    
    @Autowired
    private AppReadCache appCache;
    
    @Autowired
    private OrgReadCache orgs;
    
    //部门
    @Autowired
    private DeptReadCache depts;
    
    @Autowired
    private RedisLockTemplate lock;
    
    /**
     * 重新生成权限列表
     */
    public void resetAllAcl()
    {
        List<UserEntity> userList = users.findAll();
        log.info("开始重置{}个用户的权限", userList.size());
        clearAcl(CollectionUtil.keyList(userList));
        log.info("重置完成");
    }
    
    private void clearAcl(List<Long> userkeys)
    {
        int startpos = 0;
        int page = 1000;
        while (startpos < userkeys.size())
        {
            List<Long> content = CollectionUtil.subList(userkeys, startpos, page);
            if (!content.isEmpty())
            {
                List<String> keys = new ArrayList<>(1000);
                for (Long u : content)
                {
                    keys.add(u.toString());
                }
                userMenuMap.remove(keys);
                aclMap.remove(keys);
            }
            startpos = startpos + page;
        }
    }
    
    public void resetUserAcl(long userkey)
    {
        userMenuMap.remove(String.valueOf(userkey));
        aclMap.remove(String.valueOf(userkey));
    }
    
    public void resetRoleAcl(String roleid)
    {
        if (roleid == null)
        {
            List<UserEntity> userList = users.findAll();
            clearAcl(CollectionUtil.keyList(userList));
        }
        else
        {
            updateUserAclbyRoleid(roleid);
        }
    }
    
    /**************************
     * 
     *    关系变更
     * 
     **************************/
    
    /**
     * 角色删除后，级联删除关联的关系
     * @param roleid
     */
    public void removeRole(String roleid)
    {
        //清理角色和权限关系
        Set<String> pkeys = roleAclCache.getIdsByIndex(roleid);
        if (pkeys != null && !pkeys.isEmpty())
        {
            roleAclCache.removeAllById(pkeys);
        }
        //清理角色和菜单关系
        roleMenuDao.delByRole(roleid);
        //清理用户和角色关系
        List<RoleInstance> roleInstances = new ArrayList<>();
        List<Long> userkeys = userRoleDao.findUserByRole(roleid);
        if (userkeys != null)
        {
            for (Long userkey : userkeys)
            {
                List<RoleInstance> list = userRoleCache.getByIndex(String.valueOf(userkey));
                for (RoleInstance r : list)
                {
                    if (roleid.equals(r.getValue()))
                    {
                        roleInstances.add(r);
                    }
                }
            }
        }
        if (!roleInstances.isEmpty())
        {
            userRoleCache.removeAll(roleInstances);
        }
        
        //更新缓存
        updateUserAclbyRoleid(roleid);
    }
    
    /**
     * 用户删除后，级联删除关联的关系
     * @param userkey
     */
    public void removeByUserkey(Long userkey)
    {
        //清理用户和角色关系
        List<RoleInstance> roles = userRoleCache.getByIndex(String.valueOf(userkey));
        if (roles != null && !roles.isEmpty())
        {
            userRoleCache.removeAll(roles);
        }
        //清理用户和权限关系
        List<AccessInstance> m2 = userAclCache.getByIndex(String.valueOf(userkey));
        if (m2 != null && !m2.isEmpty())
        {
            userAclCache.removeAll(m2);
        }
        //更新缓存
        userMenuMap.remove(userkey.toString());
        aclMap.remove(userkey.toString());
        
    }
    
    /**
     * 计算角色下所有用户的权限，并更新Redis
     * @param roleid
     */
    private void updateUserAclbyRoleid(String roleid)
    {
        List<Long> userkeys = userRoleDao.findUserByRole(roleid);
        clearAcl(userkeys);
    }
    
    /**************************
     * 
     *    角色权限
     * 
     **************************/
    
    /**
     * 获得角色的权限列表
     * @param roleid
     * @return
     */
    public List<RoleAccessInstance> getRoleAcl(String roleid)
    {
        List<RoleAccessInstance> list = roleAclCache.getByIndex(roleid);
        return list;
    }
    
    public Map<String, Boolean> getRoleAclMap(String roleid)
    {
        Map<String, Boolean> map = new HashMap<>();
        List<RoleAccessInstance> list = roleAclCache.getByIndex(roleid);
        for (RoleAccessInstance ac : list)
        {
            map.put(ac.getFuncKey(), ac.isAccept());
        }
        return map;
    }
    
    public void checkRoleid(String roleid)
    {
        for (SysRoleEnum e : SysRoleEnum.values())
        {
            if (e.name().equals(roleid))
                throw TofocusException.of(SysErrCode.ACCESS_DENIED);
        }
    }
    
    /**
     * 设置角色权限
     * @param roleid
     * @param accessTokens
     */
    public void setRoleAcl(String roleid, List<RoleAccessInstance> accessTokens)
    {
        List<RoleAccessInstance> oldlist = roleAclCache.getByIndex(roleid);
        roleAclCache.removeAll(oldlist);
        roleAclCache.addAll(accessTokens);
        updateUserAclbyRoleid(roleid);
    }
    
    /**
     * 设置角色权限
     * @param roleid
     * @param accessTokens
     */
    public void setRoleAcl(String roleid, Map<String, Boolean> accessMap)
    {
        AppRoleEntity role = roleCache.get(roleid);
        List<RoleAccessInstance> oldlist = roleAclCache.getByIndex(roleid);
        roleAclCache.removeAll(oldlist);
        List<RoleAccessInstance> accessTokens = new ArrayList<>();
        int i = 0;
        for (Entry<String, Boolean> e : accessMap.entrySet())
        {
            RoleAccessInstance tokens = new RoleAccessInstance();
            tokens.setPkey(roleid + "_func_" + i);
            tokens.setAccept(e.getValue());
            tokens.setOwnerid(roleid);
            tokens.setDomainid(role.getDomainid());
            tokens.setFuncKey(e.getKey());
            accessTokens.add(tokens);
            i++;
        }
        roleAclCache.addAll(accessTokens);
        updateUserAclbyRoleid(roleid);
    }
    
    /**************************
     * 
     *    角色菜单
     * 
     **************************/
    
    public Map<String, Long> countRoleByMenu(String menu)
    {
        return roleMenuDao.countRoleByMenu(menu);
    }
    
    public void delByMenu(String menu)
    {
        Map<String, Long> roles = roleMenuDao.countRoleByMenu(menu);
        List<Long> userkeys = userRoleDao.findUserByRoles(roles.keySet());
        clearAcl(userkeys);
        roleMenuDao.delByMenu(menu);
    }
    
    public boolean isRoleUsed(String roleid)
    {
        List<Long> userkeys = userRoleDao.findUserByRole(roleid);
        return !userkeys.isEmpty();
    }
    
    /**
     * 获得角色的菜单
     * @param roleid
     * @return
     */
    public Map<String, Boolean> getRoleMenu(String roleid)
    {
        Map<String, Boolean> map = new HashMap<>();
        List<RoleMenuEntity> list = roleMenuDao.listByRole(roleid);
        for (RoleMenuEntity ac : list)
        {
            map.put(ac.getMenu(), ac.isAccept());
        }
        return map;
    }
    
    public void setRoleMenu(String roleid, Map<String, Boolean> accessMap)
    {
        AppRoleEntity role = roleCache.get(roleid);
        String domain = role.getDomainid();
        //删除旧菜单关系
        List<RoleMenuEntity> dels = roleMenuDao.listByRole(roleid);
        //保存新菜单管理
        List<RoleMenuEntity> puts = new ArrayList<>();
        int i = 0;
        for (Entry<String, Boolean> e : accessMap.entrySet())
        {
            RoleMenuEntity roleMenu = new RoleMenuEntity();
            roleMenu.setPkey(roleid + "_menu_" + i);
            roleMenu.setAccept(e.getValue());
            roleMenu.setOwnerid(roleid);
            roleMenu.setDomainid(domain);
            roleMenu.setMenu(e.getKey());
            puts.add(roleMenu);
            i++;
        }
        roleMenuDao.removeAndPutAll(dels, puts);
        //更新用户缓存
        updateUserAclbyRoleid(roleid);
    }
    
    /**************************
     * 
     *    用户角色
     * 
     **************************/
    /**
     * 获得用户的角色
     * @return
     */
    public List<RoleInstance> getUserRole(Long userkey)
    {
        List<RoleInstance> rs = userRoleCache.getByIndex(String.valueOf(userkey));
        List<RoleInstance> result = new ArrayList<>();
        for (RoleInstance ins : rs)
        {
            AppRoleEntity role = roleCache.get(ins.getValue());
            if (role != null && role.isEnable())
                result.add(ins);
        }
        return result;
    }
    
    /**
     * 给用户增加一个角色
     * @param role
     */
    public void addUserRole(RoleInstance instance, String currentDomain)
    {
        checkManagerUserRole(instance, currentDomain);
        userRoleCache.add(instance);
        userMenuMap.remove(instance.getOwnerid());
        aclMap.remove(instance.getOwnerid());
    }
    
    /**
     * 给用户增加多个角色
     * @param userkey
     * @param result
     */
    public void addUserRole(Long userkey, List<RoleInstance> accessTokens, String currentDomain)
    {
        if (accessTokens != null && !accessTokens.isEmpty())
        {
            for (RoleInstance instance : accessTokens)
            {
                checkManagerUserRole(instance, currentDomain);
            }
            for (RoleInstance instance : accessTokens)
            {
                userRoleCache.add(instance);
            }
            userMenuMap.remove(userkey.toString());
            aclMap.remove(userkey.toString());
        }
    }
    
    public void removeUserRole(Long userkey, String rolekey, AccessScopeType scopeType, String scope, String currentDomain)
    {
        List<RoleInstance> list = userRoleCache.getByIndex(String.valueOf(userkey));
        if (list != null && !list.isEmpty())
        {
            for (RoleInstance instance : list)
            {
                if (instance != null && instance.getValue().equals(rolekey)
                    && Util.equal(instance.getScopeType(), scopeType) && Util.equal(instance.getScope(), scope))
                {
                    checkManagerUserRole(instance, currentDomain);
                    userRoleCache.remove(instance);
                    userMenuMap.remove(userkey.toString());
                    aclMap.remove(userkey.toString());
                }
            }
        }
    }
    
    public void removeUserRoleByScope(Long userkey, AccessScopeType scopeType, String scope, String currentDomain)
    {
        List<RoleInstance> list = userRoleCache.getByIndex(String.valueOf(userkey));
        if (list != null && !list.isEmpty())
        {
            for (RoleInstance instance : list)
            {
                if (instance != null && Util.equal(instance.getScopeType(), scopeType)
                    && Util.equal(instance.getScope(), scope))
                {
                    checkManagerUserRole(instance, currentDomain);
                }
            }
            for (RoleInstance instance : list)
            {
                if (instance != null && Util.equal(instance.getScopeType(), scopeType)
                    && Util.equal(instance.getScope(), scope))
                {
                    userRoleCache.remove(instance);
                }
            }
            userMenuMap.remove(userkey.toString());
            aclMap.remove(userkey.toString());
        }
    }
    
    public void removeUserRoleInGroupByScope(Long userkey, AccessScopeType scopeType, String scope, String roleGroup, String currentDomain)
    {
        List<RoleInstance> list = userRoleCache.getByIndex(String.valueOf(userkey));
        if (list != null && !list.isEmpty())
        {
            for (RoleInstance instance : list)
            {
                if (instance != null && Util.equal(instance.getScopeType(), scopeType)
                    && Util.equal(instance.getScope(), scope))
                {
                    AppRoleEntity role = roleCache.get(instance.getValue());
                    if (Util.equal(role.getGroup(), roleGroup))
                    {
                        checkManagerUserRole(instance, currentDomain);
                    }
                }
            }
            for (RoleInstance instance : list)
            {
                if (instance != null && Util.equal(instance.getScopeType(), scopeType)
                    && Util.equal(instance.getScope(), scope))
                {
                    AppRoleEntity role = roleCache.get(instance.getValue());
                    if (Util.equal(role.getGroup(), roleGroup))
                    {
                        userRoleCache.remove(instance);
                    }
                }
            }
            userMenuMap.remove(userkey.toString());
            aclMap.remove(userkey.toString());
        }
    }
    
    public void removeUserRole(Long userkey, List<String> rolekeys, String currentDomain)
    {
        List<RoleInstance> list = userRoleCache.getByIndex(String.valueOf(userkey));
        if (list != null && !list.isEmpty())
        {
            for (RoleInstance instance : list)
            {
                if (instance != null && rolekeys.contains(instance.getValue()))
                {
                    checkManagerUserRole(instance, currentDomain);
                }
            }
            for (RoleInstance instance : list)
            {
                if (instance != null && rolekeys.contains(instance.getValue()))
                {
                    userRoleCache.remove(instance);
                }
            }
            userMenuMap.remove(userkey.toString());
            aclMap.remove(userkey.toString());
        }
    }
    
    /**
     * 删除一个用户角色
     * @param userkey
     * @param pkey 角色实例的主键
     */
    public void removeUserRoleByDomain(Long userkey, String domainid, String currentDomain)
    {
        List<RoleInstance> list = userRoleCache.getByIndex(String.valueOf(userkey));
        if (list != null && !list.isEmpty())
        {
            for (RoleInstance instance : list)
            {
                if (instance != null && Util.equal(instance.getDomainid(), domainid))
                {
                    checkManagerUserRole(instance, currentDomain);
                }
            }
            for (RoleInstance instance : list)
            {
                if (instance != null && Util.equal(instance.getDomainid(), domainid))
                {
                    userRoleCache.remove(instance);
                }
            }
            userMenuMap.remove(userkey.toString());
            aclMap.remove(userkey.toString());
        }
    }
    
    /**
     * 删除一个用户角色
     * @param userkey
     * @param pkey 角色实例的主键
     */
    public void removeUserRole(Long userkey, String instancePkey, String currentDomain)
    {
        List<RoleInstance> list = userRoleCache.getByIndex(String.valueOf(userkey));
        if (list != null && !list.isEmpty())
        {
            for (RoleInstance instance : list)
            {
                if (instance != null && instance.getPkey().equals(instancePkey))
                {
                    checkManagerUserRole(instance, currentDomain);
                    userRoleCache.remove(instance);
                    userMenuMap.remove(userkey.toString());
                    aclMap.remove(userkey.toString());
                }
            }
        }
    }
    
    public List<UserEntity> getUsersbyRole(String roleid)
    {
        List<Long> userkeys = userRoleDao.findUserByRole(roleid);
        if (userkeys.isEmpty())
            return new ArrayList<>();
        else
            return users.get(userkeys);
    }
    
    /**
     * 列出域中机构下的可选角色
     * @param orgScope
     * @param roleGroup
     * @return
     */
    public List<AppRoleEntity> listDomainOrgRole(String orgScope, String roleGroup)
    {
        String domain = appCache.currentApp().getDomainid();
        String orgid = null;
        if (orgScope != null)
        {
            OrgKV org = orgs.get(orgScope);
            if (org == null)
                throw TofocusException.of(AccErrCode.ORG_NOT_EXIST);
            orgid = org.getPkey();
        }
        // @formatter:off
        ConditionBuilder<SelectBuilder<String, AppRoleEntity>> builder =
            roleCache.select()
                .or()
                  .and()
                    .eq("domainid", domain)
                    .isNull("orgid")
                    .isNull("deptid")
                    .eq("group", roleGroup)
                  .close();
        if(orgScope != null)
        {
            builder
            .and()
              .eq("orgid", orgid)
              .isNull("deptid")
            .close();
        }
        List<AppRoleEntity> roleList =builder.done().sort("createdTime", false)
            .exec();
        // @formatter:on
        return roleList;
    }
    
    /**************************
     * 
     *    用户权限
     * 
     **************************/
    
    /**
     * 获得用户设置的权限
     * @param userkey
     * @return
     */
    public Map<String, List<AccessInstanceDTO>> getUserAcl(Long userkey)
    {
        Map<String, List<AccessInstanceDTO>> result = new HashMap<>();
        List<AccessInstance> acl = userAclCache.getByIndex(String.valueOf(userkey));
        if (acl != null)
        {
            for (AccessInstance at : acl)
            {
                String funckey = at.getFuncKey();
                List<AccessInstanceDTO> list = result.get(funckey);
                if (list == null)
                {
                    list = new ArrayList<>();
                    result.put(funckey, list);
                }
                AccessInstanceDTO dto = createAccessInstanceDTO(at, functionCache.getAsSystem(funckey));
                list.add(dto);
            }
        }
        return result;
    }
    
    /**
     * 获得用户的全部权限
     * @param userkey
     * @return
     */
    public Map<String, List<AccessInstanceDTO>> getUserFullAcl(Long userkey)
    {
        Map<String, List<AccessInstanceDTO>> result = getAclInRole(userkey);
        Map<String, List<AccessInstanceDTO>> userAcls = getUserAcl(userkey);
        for (Entry<String, List<AccessInstanceDTO>> entry : userAcls.entrySet())
        {
            String funckey = entry.getKey();
            List<AccessInstanceDTO> list = result.get(funckey);
            if (list == null)
            {
                list = new ArrayList<>();
                result.put(funckey, list);
            }
            list.addAll(entry.getValue());
        }
        return result;
    }
    
    /**
     * 获得用户的全部权限
     * @param userkey
     * @return
     */
    private HashMap<String, Boolean> getUserFullMenu(Long userkey)
    {
        HashMap<String, Boolean> result = new HashMap<>();
        //获取角色列表
        List<RoleInstance> roleinstances = getUserRole(userkey);
        Set<String> roles = new HashSet<>();
        for (RoleInstance ur : roleinstances)
        {
            if (ur.getValue() != null)
                roles.add(ur.getValue());
        }
        for (String roleid : roles)
        {
            Map<String, Boolean> acl = getRoleMenu(roleid);
            for (Entry<String, Boolean> at : acl.entrySet())
            {
                String menu = at.getKey();
                boolean accept = at.getValue();
                if (!accept || !result.containsKey(menu))
                    result.put(menu, accept);
            }
        }
        return result;
    }
    
    /**
     * 
     * 获得用户的角色的权限
     * 
     * @param userkey
     * @return
     */
    private Map<String, List<AccessInstanceDTO>> getAclInRole(Long userkey)
    {
        String userid = String.valueOf(userkey);
        Map<String, List<AccessInstanceDTO>> result = new HashMap<>();
        //获取角色列表
        List<RoleInstance> roleinstances = getUserRole(userkey);
        for (RoleInstance ur : roleinstances)
        {
            //获取角色的访问控制列表
            String roleid = ur.getValue();
            if (roleid != null)
            {
                List<RoleAccessInstance> acl = getRoleAcl(roleid);
                for (RoleAccessInstance at : acl)
                {
                    String funckey = at.getFuncKey();
                    List<AccessInstanceDTO> list = result.get(funckey);
                    if (list == null)
                    {
                        list = new ArrayList<>();
                        result.put(funckey, list);
                    }
                    AccessInstance a = new AccessInstance(ur.getDomainid(), at, ur, userid);
                    AccessInstanceDTO dto = createAccessInstanceDTO(a, functionCache.getAsSystem(funckey));
                    dto.setInherited(true);
                    list.add(dto);
                }
            }
        }
        return result;
    }
    
    /**
     * 给用户增加一个权限
     * @param userkey
     * @param instance
     */
    public void addUserAcl(AccessInstance instance)
    {
        checkManagerUserAcl(instance);
        userAclCache.add(instance);
        aclMap.remove(instance.getOwnerid());
    }
    
    /**
     * 给用户增加多个权限
     * @param userkey
     * @param accessTokens
     */
    public void addUserAcl(Long userkey, List<AccessInstance> accessTokens)
    {
        if (accessTokens != null && !accessTokens.isEmpty())
        {
            for (AccessInstance instance : accessTokens)
            {
                checkManagerUserAcl(instance);
            }
            for (AccessInstance accessToken : accessTokens)
            {
                userAclCache.add(accessToken);
            }
            aclMap.remove(userkey.toString());
        }
    }
    
    /**
     * 删除用户的权限
     * @param userkey
     * @param pkey
     */
    public void removeUserAcl(Long userkey, String pkey)
    {
        AccessInstance inst = userAclCache.get(pkey);
        if (inst != null && inst.getOwnerid().equals(String.valueOf(userkey)))
        {
            checkManagerUserAcl(inst);
            userAclCache.remove(inst);
            aclMap.remove(userkey.toString());
        }
    }
    
    /**************************
     * 
     *    权限验证
     * 
     **************************/
    
    public void checkManagerUserRole(RoleInstance instance, String currentDomain)
    {
        checkManagerUserRole(instance.getValue(), instance.getScopeType(), instance.getScope(), currentDomain);
    }
    
    private void checkManagerUserRole(String roleid, AccessScopeType scopeType, String scope, String currentDomain)
    {
        //获取当前的scope
        AccessListWhole acl = aclUtil.getUserAccessList(SysFunctionEnum.managerUser.name());
        if (acl == null)
            throw TofocusException.of(SysErrCode.ACCESS_DENIED, "没有 managerUserRole 权限");
        AppRoleEntity role = roleCache.getAsSystem(roleid);
        if (role == null)
            throw TofocusException.of(SysErrCode.ACCESS_DENIED, roleid + " 角色不存在");
        else
        {
            //拥有全系统scope
            if (acl.canAccessAllDomain())
                return;
            
            String domain = role.getDomainid();
            String org = role.getOrgid();
            String dept = role.getDeptid();
            //判断是否可以分配系统角色
            if (domain == null)
            {
                //系统角色
                if (role.getPkey().equals(SysRoleEnum.sysAdmin.name())
                    || (role.getPkey().equals(SysRoleEnum.oauth2Admin.name())))
                    throw TofocusException.of(SysErrCode.ACCESS_DENIED, "没有操作 <" + role.getName() + "> 角色的权限");
            }
            else
            {
                if (!domain.equals(currentDomain))
                    throw TofocusException.of(SysErrCode.ACCESS_DENIED, "不能操作不是 <" + currentDomain + "> 的角色");
                
                if (org != null)
                {
                    boolean hasDomainRight = acl.canAccessDomain(domain);
                    boolean hasOrgRight = acl.canAccessOrg(org);
                    if (dept == null)
                    {
                        if (!hasOrgRight && !hasDomainRight)
                            throw TofocusException.of(SysErrCode.ACCESS_DENIED, "不能操作 <" + role.getName() + "> 角色");
                    }
                    else
                    {
                        boolean hasDeptRight = acl.canAccessDept(dept);
                        if (!hasDeptRight && !hasOrgRight && !hasDomainRight)
                            throw TofocusException.of(SysErrCode.ACCESS_DENIED, "不能操作 <" + role.getName() + "> 角色");
                    }
                }
            }
            checkScope(acl, scopeType, scope);
        }
    }
    
    private void checkManagerUserAcl(AccessInstance instance)
    {
        //获取当前的scope
        AccessListWhole acl = aclUtil.getUserAccessList(SysFunctionEnum.managerUser.name());
        if (acl == null)
            throw TofocusException.of(SysErrCode.ACCESS_DENIED, "没有 managerUserFunction 权限");
        AppFunctionEntity function = functionCache.getAsSystem(instance.getFuncKey());
        if (function == null)
            throw TofocusException.of(SysErrCode.ACCESS_DENIED, instance.getFuncKey() + " 功能不存在");
        else
        {
            //拥有全系统scope
            if (acl.canAccessAllDomain())
                return;
            checkAcl(function);
            checkScope(acl, instance.getScopeType(), instance.getScope());
        }
    }
    
    /**
     * 检查对角色是否有管理权限
     * @param acl
     * @param role
     */
    public void checkManageRole(AppRoleEntity role)
    {
        AccessListWhole acl = aclUtil.getUserAccessList(SysFunctionEnum.managerRole.name());
        String domain = role.getDomainid();
        String org = role.getOrgid();
        //判断是否对系统角色有操作权限
        if (domain == null)
        {
            //系统角色
            throw TofocusException.of(SysErrCode.ACCESS_DENIED, " <" + role.getName() + "> 角色必须设置域");
        }
        else
        {
            //判断是否对域角色有操作权限
            if (org == null)
            {
                if (!acl.canAccessDomain(domain))
                    throw TofocusException.of(SysErrCode.ACCESS_DENIED, "没有操作域 <" + domain + "> 下角色的权限");
            }
            else
            {
                //判断是否对应用角色有操作权限
                if (org != null)
                {
                    if (!acl.canAccessDomain(domain) && !acl.canAccessOrg(org))
                        throw TofocusException.of(SysErrCode.ACCESS_DENIED,
                            "没有操作 <" + domain + "> 下的 <" + org + "> 机构角色的权限");
                }
            }
        }
    }
    
    private void checkAcl(AppFunctionEntity function)
    {
        //系统角色
        if (function.getPkey().equals(SysFunctionEnum.managerApplication.name()))
            throw TofocusException.of(SysErrCode.ACCESS_DENIED, "没有操作 " + function.getName() + " 功能的权限");
        else
        {
            //自己没有的功能，不能赋权
            if (!SecurityContextUtil.getAuthenticationContext().getAuthorities().containsKey(function.getPkey()))
            {
                //域管理员可以赋权域下所有功能
                AccessListWhole domainAdminAcl = aclUtil.getUserAccessList(SysFunctionEnum.domainAdmin.name());
                if (domainAdminAcl != null)
                {
                    if (function.getDomainid() == null || !domainAdminAcl.canAccessDomain(function.getDomainid()))
                    {
                        throw TofocusException.of(SysErrCode.ACCESS_DENIED, "没有操作 " + function.getName() + " 功能的权限");
                    }
                }
                else
                {
                    throw TofocusException.of(SysErrCode.ACCESS_DENIED, "没有操作 " + function.getName() + " 功能的权限");
                }
            }
        }
    }
    
    /**
     * 判断scope授权范围
     * @param acl
     * @param type
     * @param scope
     */
    private void checkScope(AccessListWhole acl, AccessScopeType type, String scope)
    {
        //判断scope是否有权限
        if (type != null)
        {
            switch (type)
            {
                case domain:
                    if (!acl.canAccessDomain(scope))
                        throw TofocusException.of(SysErrCode.ACCESS_DENIED, type + ":" + scope + " 不在可授权的范围内");
                    break;
                case dept:
                    DeptKV dept = depts.get(scope);
                    if (dept == null)
                        throw TofocusException.of(AccErrCode.DEPT_NOT_EXIST, scope);
                    OrgKV org1 = orgs.get(dept.getOrgid());
                    if (org1 == null)
                        throw TofocusException.of(AccErrCode.ORG_NOT_EXIST, dept.getOrgid());
                    if (!acl.canAccessDept(scope) && !acl.canAccessOrg(dept.getOrgid())
                        && !acl.canAccessDomain(org1.getDomainid()))
                        throw TofocusException.of(SysErrCode.ACCESS_DENIED, type + ":" + scope + " 不在可授权的范围内");
                    break;
                case org:
                    OrgKV org = orgs.get(scope);
                    if (org == null)
                        throw TofocusException.of(AccErrCode.ORG_NOT_EXIST, scope);
                    if (!acl.canAccessOrg(scope) && !acl.canAccessDomain(org.getDomainid()))
                        throw TofocusException.of(SysErrCode.ACCESS_DENIED, type + ":" + scope + " 不在可授权的范围内");
                    break;
                default:
                    break;
            }
        }
    }
    
    public boolean isFuncUsed(String funcid)
    {
        return roleAclCache.isFuncUsed(funcid) || userAclCache.isFuncUsed(funcid);
    }
    
    /**
     * 获取用户的最终权限列表
     * @param userkey
     * @return
     */
    public Map<String, AccessList> getCachedUserAcls(Long userkey)
    {
        String key = String.valueOf(userkey);
        Map<String, AccessList> map = aclMap.get(key);
        if (map == null)
        {
            if (users.get(userkey) == null)
                return null;
            else
            {
                //更新权限列表
                updateUserAcl(userkey);
                return aclMap.get(key);
            }
        }
        else
        {
            aclMap.expire(key, aclMap.getDefaultTimeout());
            return map;
        }
    }
    
    /**
     * 获取用户的最终权限列表
     * @param userkey
     * @return
     */
    public Map<String, Boolean> getCachedUserMenus(Long userkey)
    {
        String key = String.valueOf(userkey);
        Map<String, Boolean> map = userMenuMap.get(key);
        if (map == null)
        {
            if (users.get(userkey) == null)
                return new HashMap<>();
            else
            {
                //更新权限列表
                updateUserMenu(userkey);
                return userMenuMap.get(key);
            }
        }
        else
        {
            userMenuMap.expire(key, userMenuMap.getDefaultTimeout());
            return map;
        }
    }
    
    /**
     * 计算用户的权限并更新到Redis
     * @param userkey
     */
    private void updateUserAcl(Long userkey)
    {
        lock.lock(DOMAIN, APP, USER + userkey);
        try
        {
            HashMap<String, AccessList> aclmap = new HashMap<>();
            Map<String, List<AccessInstanceDTO>> map = getUserFullAcl(userkey);
            for (Entry<String, List<AccessInstanceDTO>> entry : map.entrySet())
            {
                AccessList alist = new AccessList(entry.getKey());
                for (AccessInstanceDTO dto : entry.getValue())
                {
                    alist.add(dto);
                }
                aclmap.put(entry.getKey(), alist);
            }
            aclMap.put(String.valueOf(userkey), aclmap);
        }
        finally
        {
            lock.unlock(DOMAIN, APP, USER + userkey);
        }
    }
    
    /**
     * 计算用户的菜单并更新到Redis
     * @param userkey
     */
    private void updateUserMenu(Long userkey)
    {
        lock.lock(DOMAIN, APP, USER + userkey);
        try
        {
            HashMap<String, Boolean> map = getUserFullMenu(userkey);
            userMenuMap.put(String.valueOf(userkey), map);
        }
        finally
        {
            lock.unlock(DOMAIN, APP, USER + userkey);
        }
    }
    
    private AccessInstanceDTO createAccessInstanceDTO(AccessInstance a, AppFunctionEntity f)
    {
        AccessInstanceDTO dto = new AccessInstanceDTO();
        dto.setPkey(a.getPkey());
        dto.setFuncKey(a.getFuncKey());
        dto.setAccept(a.isAccept());
        dto.setOwnerDomainid(a.getDomainid());
        if (f != null)
        {
            dto.setFuncDomainid(f.getDomainid());
            dto.setFuncName(f.getName());
        }
        dto.setScope(a.getScope());
        dto.setScopeType(a.getScopeType());
        return dto;
    }
    
}
