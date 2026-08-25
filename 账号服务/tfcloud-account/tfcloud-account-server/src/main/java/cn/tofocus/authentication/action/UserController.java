package cn.tofocus.authentication.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.account.db.cache.application.AppReadCache;
import cn.tofocus.authentication.service.TofocusUserDetailsService;
import cn.tofocus.core.security.TofocusUser;
import java.security.Principal;

@RestController
public class UserController
{
    @Autowired
    private AppReadCache appCache;
    
    @Autowired
    private TofocusUserDetailsService tofocusUserDetailsService;
    
    @GetMapping("/userinfo")
    public Principal user(Principal user)
    {
        if (user instanceof OAuth2Authentication)
        {
            OAuth2Authentication au = (OAuth2Authentication)user;
            Object principal = au.getPrincipal();
            if (principal instanceof TofocusUser)
            {
                TofocusUser u = (TofocusUser)principal;
                if (u.getCurrentDomain() == null)
                {
                    String appid = au.getOAuth2Request().getClientId();
                    String domain = appCache.getDomainId(appid);
                    if (domain != null)
                    {
                        tofocusUserDetailsService.extendInfo(u, domain);
                        tofocusUserDetailsService.filterRolesAndAuthorities(u, domain);
                    }
                }
            }
        }
        return user;
    }
}
