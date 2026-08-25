package cn.tofocus.authentication.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenGranter;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeTokenGranter;
import org.springframework.security.oauth2.provider.implicit.ImplicitTokenGranter;
import org.springframework.security.oauth2.provider.password.ResourceOwnerPasswordTokenGranter;
import org.springframework.security.oauth2.provider.refresh.RefreshTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import cn.tofocus.authentication.auth.runas.ServerRunAsTokenGranter;
import cn.tofocus.authentication.service.ApplicationClientDetails;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter
{
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private RedisConnectionFactory connectionFactory;
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    @Autowired
    private ApplicationClientDetails applicationClientDetails;
    
    @Value("${spring.redis.prefix}")
    private String redisTokenPrefix;
    
    @Bean
    public TokenStore tokenStore()
    {
        RedisTokenStore redisTokenStore = new RedisTokenStore(connectionFactory);
        redisTokenStore.setPrefix(redisTokenPrefix + ":");
        return redisTokenStore;
    }
    
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints)
        throws Exception
    {
        endpoints.authenticationManager(authenticationManager)
            .userDetailsService(userDetailsService)
            .tokenStore(tokenStore());
        TokenGranter tokenGranter = new CompositeTokenGranter(getAllTokenGranters(endpoints.getTokenServices(),
            endpoints.getAuthorizationCodeServices(),
            endpoints.getOAuth2RequestFactory()));
        endpoints.tokenGranter(tokenGranter);
    }
    
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security)
        throws Exception
    {
        security.tokenKeyAccess("permitAll()").checkTokenAccess("permitAll()").allowFormAuthenticationForClients();
    }
    
    @Override
    public void configure(ClientDetailsServiceConfigurer clients)
        throws Exception
    {
        clients.withClientDetails(applicationClientDetails);
    }
    
    private List<TokenGranter> getAllTokenGranters(AuthorizationServerTokenServices tokenServices,
        AuthorizationCodeServices authorizationCodeServices, OAuth2RequestFactory requestFactory)
    {
        List<TokenGranter> tokenGranters = new ArrayList<>();
        tokenGranters.add(new AuthorizationCodeTokenGranter(tokenServices, authorizationCodeServices,
            applicationClientDetails, requestFactory));
        tokenGranters.add(new RefreshTokenGranter(tokenServices, applicationClientDetails, requestFactory));
        ImplicitTokenGranter implicit =
            new ImplicitTokenGranter(tokenServices, applicationClientDetails, requestFactory);
        tokenGranters.add(implicit);
        tokenGranters.add(new ClientCredentialsTokenGranter(tokenServices, applicationClientDetails, requestFactory));
        if (authenticationManager != null)
        {
            tokenGranters.add(new ResourceOwnerPasswordTokenGranter(authenticationManager, tokenServices,
                applicationClientDetails, requestFactory));
            tokenGranters.add(new ServerRunAsTokenGranter(authenticationManager, tokenServices,
                applicationClientDetails, requestFactory));
        }
        return tokenGranters;
    }
}
