package cn.tofocus.account.db.dao.application;

import java.util.List;
import java.util.Map;

import org.springframework.security.oauth2.common.exceptions.UserDeniedAuthorizationException;
import org.springframework.stereotype.Component;

import cn.tofocus.account.db.entity.application.AppLoginCheckEntity;
import cn.tofocus.account.db.entity.application.AppLoginCheckEntity.F;
import cn.tofocus.core.security.AccessList;
import cn.tofocus.core.user.SysFunctionEnum;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;

@Component
public class AppLoginCheckDao extends JpaSpecificationDelegate<String, AppLoginCheckEntity>
{

    public List<AppLoginCheckEntity> listByDomain(String domain)
    {
        return this.select().strict(true).eq(F.domainid, domain).exec();
    }

    public boolean isPkeyUsed(String excludeDomain, List<String> keyList)
    {
        return this.selectOne().strict(true).notEq(F.domainid, excludeDomain).in(F.pkey, keyList).exec() != null;
    }
    
    public void checkLoginFunc(String domain, String clientId, Map<String, AccessList> authMap)
    {
        AppLoginCheckEntity e = this.get(clientId);
        String func = e == null ? null : e.getFuncKey();
        if (func != null)
        {
            boolean match = false;
            AccessList domainAcl = authMap.get(SysFunctionEnum.domainAdmin.name());
            if (domainAcl != null && domainAcl.canAccessDomain(domain))
            {
                match = true;
            }
            else
            {
                AccessList acl = authMap.get(func);
                match = acl != null && acl.canAccess();
            }
            if (!match)
                throw new UserDeniedAuthorizationException("没有登录权限");
        }
    }
}
