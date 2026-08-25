package cn.tofocus.authentication.auth.runas;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import cn.tofocus.common.util.security.OpenSSHUtils;
import cn.tofocus.core.security.SecurityConstants;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerRunAsTokenGranter extends AbstractTokenGranter
{
    private static final String GRANT_TYPE = SecurityConstants.GRANT_TYPE_SERVER_RUN_AS;
    
    private final AuthenticationManager authenticationManager;
    
    public ServerRunAsTokenGranter(AuthenticationManager authenticationManager,
        AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService,
        OAuth2RequestFactory requestFactory)
    {
        this(authenticationManager, tokenServices, clientDetailsService, requestFactory, GRANT_TYPE);
    }
    
    protected ServerRunAsTokenGranter(AuthenticationManager authenticationManager,
        AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService,
        OAuth2RequestFactory requestFactory, String grantType)
    {
        super(tokenServices, clientDetailsService, requestFactory, grantType);
        this.authenticationManager = authenticationManager;
    }
    
    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest)
    {
        
        Map<String, String> parameters = new LinkedHashMap<>(tokenRequest.getRequestParameters());
        String username = parameters.get("user");
        String host = parameters.get("host");
        String pwd = parameters.get("pwd");
        String codeTarget = parameters.get("codeTarget");
        String code = parameters.get("code");
        String principal = null;
        try
        {
            principal = OpenSSHUtils.decryptByAuthorizedKey(username, host);
            if (pwd != null)
                pwd = OpenSSHUtils.decryptByAuthorizedKey(pwd, host);
        }
        catch (Exception e1)
        {
            log.warn("ServerRunAs 认证解密失败，{}", host);
            throw new InvalidGrantException("认证失败：ServerRunAs 认证解密失败");
        }
        Authentication userAuth =
            new ServerRunAsGrantAuthenticationToken(principal, pwd, host, client.getClientId(), codeTarget, code);
        try
        {
            userAuth = authenticationManager.authenticate(userAuth);
        }
        catch (AccountStatusException ase)
        {
            //covers expired, locked, disabled cases (mentioned in section 5.2, draft 31)
            throw new InvalidGrantException(ase.getMessage());
        }
        catch (BadCredentialsException e)
        {
            // If the username/password are wrong the spec says we should send 400/invalid grant
            throw new InvalidGrantException(e.getMessage());
        }
        if (userAuth == null || !userAuth.isAuthenticated())
        {
            throw new InvalidGrantException("Could not authenticate user: " + principal);
        }
        
        OAuth2Request storedOAuth2Request = getRequestFactory().createOAuth2Request(client, tokenRequest);
        return new OAuth2Authentication(storedOAuth2Request, userAuth);
    }
    
}
