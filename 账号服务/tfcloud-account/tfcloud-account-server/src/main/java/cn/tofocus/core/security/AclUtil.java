package cn.tofocus.core.security;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.springframework.stereotype.Component;

import cn.tofocus.core.user.SysFunctionEnum;

@Component
public class AclUtil
{
    public AccessListWhole getUserAccessList(String function)
    {
        Map<String, AccessList> map = SecurityContextUtil.getAuthenticationContext().getAuthorities();
        AccessList acl = map.get(function);
        if (acl == null || (acl.getAccept() != null && !acl.getAccept()))
            return null;
        else
        {
            AccessListWhole dto = access2Whole(acl);
            return dto;
        }
    }
    
    private AccessListWhole access2Whole(AccessList acl)
    {
        AccessListWhole dto = new AccessListWhole();
        dto.setDomainid(acl.getDomainid());
        dto.setFuncKey(acl.getFuncKey());
        Map<AccessScopeType, AccessScopeWhole> scopes = new HashMap<>();
        for (Entry<AccessScopeType, AccessScope> entry : acl.getScopes().entrySet())
        {
            AccessScopeType type = entry.getKey();
            AccessScope scope = entry.getValue();
            AccessScopeWhole wholescope = access2Whole(scope, type);
            scopes.put(type, wholescope);
        }
        dto.setScopes(scopes);
        return dto;
    }

    private AccessScopeWhole access2Whole(AccessScope aclscope, AccessScopeType type)
    {
        AccessScopeWhole wholescope = new AccessScopeWhole();
        
        Map<String, Boolean> accepts = aclscope.getPermit();
        Map<String, Boolean> denys = aclscope.getDeny();
        
        Set<String> fullaccepts = new HashSet<>();
        Set<String> fulldenys = new HashSet<>();
        for (Entry<String, Boolean> e : accepts.entrySet())
        {
            fullaccepts.add(e.getKey());
            /* 不加子机构
            if (e.getValue() != null && e.getValue())
            {
                if (AccessScopeType.org.equals(type))
                {
                    List<String> subNodes =  orgQueryInterface.getAllChildrenId(e.getKey());
                    if(subNodes != null)
                    {
                        fullaccepts.addAll(subNodes);
                    }
                }
                else if (AccessScopeType.dept.equals(type))
                {
                    List<String> subNodes =  deptQueryInterface.getAllChildrenId(e.getKey());
                    if(subNodes != null)
                    {
                        fullaccepts.addAll(subNodes);
                    }
                }
            }
            */
        }
        for (Entry<String, Boolean> e : denys.entrySet())
        {
            fulldenys.add(e.getKey());
            /* 不加子机构
            if (e.getValue() != null && e.getValue())
            {
                if (AccessScopeType.org.equals(type))
                {
                    List<String> subNodes =  orgQueryInterface.getAllChildrenId(e.getKey());
                    if(subNodes != null)
                    {
                        fulldenys.addAll(subNodes);
                    }
                }
                else if (AccessScopeType.dept.equals(type))
                {
                    List<String> subNodes =  deptQueryInterface.getAllChildrenId(e.getKey());
                    if(subNodes != null)
                    {
                        fulldenys.addAll(subNodes);
                    }
                }
            }
            */
        }
        wholescope.setPermit(fullaccepts);
        wholescope.setDeny(fulldenys);
        return wholescope;
    }

    public boolean isDomainAdmin(String domain)
    {
        Map<String, AccessList> map = SecurityContextUtil.getAuthenticationContext().getAuthorities();
        AccessList acl = map.get(SysFunctionEnum.domainAdmin.name());
        if (acl == null)
            return false;
        else
        {
            return acl.canAccessDomain(domain);
        }
    }
}
