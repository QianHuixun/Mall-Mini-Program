package cn.tofocus.authentication.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.CollectionUtil;
import cn.tofocus.core.enums.AppGrantType;
import cn.tofocus.core.security.GrantType;
import cn.tofocus.core.security.SecurityConstants;
import cn.tofocus.account.db.dao.application.ApplicationDao;
import cn.tofocus.account.db.entity.application.ApplicationEntity;

@Component
public class ApplicationClientDetails implements ClientDetailsService
{
    @Autowired
    private ApplicationDao appCache;
    
    private List<String> scope = Arrays.asList("all","min");
    
    @Override
    public ClientDetails loadClientByClientId(String clientId)
        throws ClientRegistrationException
    {
        ApplicationEntity app = appCache.get(clientId);
        if (app != null)
        {
            if (app.getApptype() == null)// || AppTypeEnum.OtherApp.equals(app.getApptype()))
            {
                throw new NoSuchClientException(clientId + "不是SpingOauth2应用");
            }
            else
            {
                BaseClientDetails client = new BaseClientDetails();
                client.setClientId(app.getPkey());
                client.setClientSecret("{noop}" + app.getSecret());
                client.setScope(scope);
                client.setAuthorizedGrantTypes(grantTypeSet(app.getGrantType()));
                client.setRegisteredRedirectUri(app.getUri());
                client.setAutoApproveScopes(CollectionUtil.string2List("true", ","));
                return client;
            }
        }
        else
        {
            throw new NoSuchClientException(clientId + "应用不存在");
        }
        
    }
    
    private Set<String> grantTypeSet(AppGrantType appGrantType)
    {
        Set<String> result = new HashSet<>();
        switch (appGrantType)
        {
            case Full:
                result.add(GrantType.password.name());
                result.add(GrantType.authorization_code.name());
                result.add(GrantType.refresh_token.name());
                result.add(GrantType.client_credentials.name());
                result.add(SecurityConstants.GRANT_TYPE_SERVER_RUN_AS);
                break;
            case NoUI:
                result.add(GrantType.password.name());
                result.add(GrantType.client_credentials.name());
                result.add(SecurityConstants.GRANT_TYPE_SERVER_RUN_AS);
                result.add(GrantType.refresh_token.name());
                break;
            case OtherApp:
                result.add(GrantType.client_credentials.name());
                break;
            case WithUI:
                result.add(GrantType.password.name());
                result.add(GrantType.authorization_code.name());
                result.add(GrantType.refresh_token.name());
                result.add(SecurityConstants.GRANT_TYPE_SERVER_RUN_AS);
                break;
            default:
                break;
        }
        return result;
    }
}
