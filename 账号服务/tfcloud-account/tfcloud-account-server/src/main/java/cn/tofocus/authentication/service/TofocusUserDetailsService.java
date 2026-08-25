package cn.tofocus.authentication.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import cn.tofocus.account.bean.org.DeptKV;
import cn.tofocus.account.bean.org.OrgKV;
import cn.tofocus.account.db.cache.domain.DomainReadCache;
import cn.tofocus.account.db.cache.org.DeptReadCache;
import cn.tofocus.account.db.cache.org.OrgReadCache;
import cn.tofocus.account.db.cache.role.AppRoleCache;
import cn.tofocus.account.db.dao.user.UserDao;
import cn.tofocus.account.db.entity.role.AppRoleEntity;
import cn.tofocus.account.db.entity.user.RoleInstance;
import cn.tofocus.account.db.entity.user.UserEntity;
import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.core.data.NamedBean;
import cn.tofocus.core.data.StrKeyName;
import cn.tofocus.core.security.AccessList;
import cn.tofocus.core.security.TofocusUser;
import cn.tofocus.core.security.cache.LastAccessMap;
import cn.tofocus.domain.manager.UserPermissionManager;
import cn.tofocus.domain.user.role.RoleInfo;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * 用户信息获取接口的实现。
 * 对输入的用户名进行进行判断，按邮箱，手机号码，用户id三种方式去获取用户信息
 * 
 * @author  wyw
 * @version  [版本号, 2018年5月23日]
 */
@Slf4j
@Component
public class TofocusUserDetailsService implements UserDetailsService
{
    
    @Autowired
    private UserDao userService;
    
    //用户最终权限缓存
    @Autowired
    private UserPermissionManager userPermissionManager;
    
    @Autowired
    private AppRoleCache appRoleCache;
    
    @Autowired
    private OrgReadCache orgCache;
    
    @Autowired
    private DeptReadCache deptCache;
    
    @Autowired
    private DomainReadCache domainCache;
    
    @Autowired
    private LastAccessMap lastAccessMap;
    
    @Override
    public UserDetails loadUserByUsername(String username)
        throws UsernameNotFoundException
    {
        //用户ID登录
        UserEntity userdetail = userService.getUserByUserid(username);
        
        /**
         * 加载权限
         */
        if (userdetail != null)
        {
            Collection<? extends GrantedAuthority> authoritieset;
            Map<String, AccessList> acl = userPermissionManager.getCachedUserAcls(userdetail.getPkey());
            if (acl != null)
                authoritieset = acl.values();
            else
            {
                authoritieset = new ArrayList<>();
            }
            TofocusUser user = userdetail.toTofocusUser(authoritieset);
            List<RoleInstance> roles = userPermissionManager.getUserRole(userdetail.getPkey());
            Map<String, List<NamedBean>> domainRoles = new HashMap<>();
            Map<String, List<NamedBean>> orgRoles = new HashMap<>();
            Map<String, List<NamedBean>> deptRoles = new HashMap<>();
            for (RoleInstance r : roles)
            {
                if (r.getScopeType() != null && r.getScope() != null)
                {
                    RoleInfo role = BeanUtil.beanFrom(RoleInfo.class, appRoleCache.get(r.getValue()));
                    switch (r.getScopeType())
                    {
                        case dept:
                            deptRoles.computeIfAbsent(r.getScope(), k -> new ArrayList<>())
                                .add(new NamedBean(role.getPkey(), role.getName()));
                            break;
                        case domain:
                            domainRoles.computeIfAbsent(r.getScope(), k -> new ArrayList<>())
                                .add(new NamedBean(role.getPkey(), role.getName()));
                            break;
                        case org:
                            orgRoles.computeIfAbsent(r.getScope(), k -> new ArrayList<>())
                                .add(new NamedBean(role.getPkey(), role.getName()));
                            break;
                        default:
                            break;
                    }
                }
            }
            user.setDomainRoles(domainRoles);
            user.setOrgRoles(orgRoles);
            user.setDeptRoles(deptRoles);
            return user;
        }
        else
        {
            log.warn("用户 {} 不存在!", username);
            throw new UsernameNotFoundException("用户" + username + "不存在!");
        }
        
    }
    
    /**
     * 过滤权限访问列表和角色
     * @param user
     * @return
     */
    public Map<String, AccessList> filterRolesAndAuthorities(TofocusUser user, String domain)
    {
        Map<String, AccessList> authMap = new HashMap<>();
        Iterator<GrantedAuthority> iter = user.getAuthorities().iterator();
        while (iter.hasNext())
        {
            AccessList auth = (AccessList)iter.next();
            if (auth.getDomainid() != null && !auth.getDomainid().equals(domain))
            {
                //筛选掉非本域的权限
                iter.remove();
            }
            else
            {
                authMap.put(auth.getFuncKey(), auth);
            }
        }
        
        filterRoles(user.getDomainRoles(), domain, k -> k);
        filterRoles(user.getOrgRoles(), domain, k -> {
            OrgKV e = orgCache.get(k);
            return e == null ? null : e.getDomainid();
        });
        filterRoles(user.getDeptRoles(), domain, k -> {
            DeptKV e = deptCache.get(k);
            return e == null ? null : e.getDomainid();
        });
        return authMap;
    }
    
    private void filterRoles(Map<String, List<NamedBean>> map, String domain, Function<String, String> domainFunc)
    {
        Iterator<Entry<String, List<NamedBean>>> iter = map.entrySet().iterator();
        while (iter.hasNext())
        {
            Entry<String, List<NamedBean>> entry = iter.next();
            Iterator<NamedBean> iter2 = entry.getValue().iterator();
            while (iter2.hasNext())
            {
                AppRoleEntity role = appRoleCache.get(iter2.next().getPkey().toString());
                if (role.getDomainid() != null && !role.getDomainid().equals(domain))
                    iter2.remove();
            }
            String d = domainFunc.apply(entry.getKey());
            if ((!domain.equals(d) && !"*".equals(d)) || entry.getValue().isEmpty())
                iter.remove();
        }
    }
    
    public void extendInfo(TofocusUser u, String domain)
    {
        u.setCurrentDomain(domainCache.getNamedBean(domain));
        String lastAccessOrg = lastAccessMap.getLastAccessOrg(domain, u.getUserkey());
        String lastAccessDept = lastAccessMap.getLastAccessDept(domain, u.getUserkey());
        if (lastAccessOrg != null)
            u.setLastAccessOrg(orgCache.getNamedBean(lastAccessOrg));
        if (lastAccessDept != null)
            u.setLastAccessDept(deptCache.getNamedBean(lastAccessDept));
    }
}
