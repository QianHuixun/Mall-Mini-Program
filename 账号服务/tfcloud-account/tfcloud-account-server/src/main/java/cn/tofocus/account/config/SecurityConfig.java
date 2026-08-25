package cn.tofocus.account.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

import cn.tofocus.account.db.cache.application.AppReadCache;
import cn.tofocus.account.db.cache.user.UserCache;
import cn.tofocus.account.db.dao.application.AppLoginCheckDao;
import cn.tofocus.authentication.action.captcha.ImageCaptchaGenerate;
import cn.tofocus.authentication.auth.runas.ServerRunAsGrantAuthenticationProvider;
import cn.tofocus.authentication.bean.TofocusAuthenticationProvider;
import cn.tofocus.authentication.config.TfPasswordEncoder;
import cn.tofocus.authentication.service.TofocusUserDetailsService;
import cn.tofocus.config.SwaggerApiConfig;
import cn.tofocus.core.security.BaseSecurityConfig;

@Configuration
@EnableResourceServer
@Order(1)
public class SecurityConfig extends BaseSecurityConfig
{
    @Autowired
    private TofocusUserDetailsService userDetailsService;
    
    @Autowired
    private ImageCaptchaGenerate captchaGenerate;
    
    @Autowired
    private TfPasswordEncoder tfPasswordEncoder;
    
    @Autowired
    private AppReadCache appCache;
    
    @Autowired
    private AppLoginCheckDao appLoginCheckDao;
    
    @Autowired
    private TofocusUserDetailsService tofocusUserDetailsService;
    
    @Autowired
    private UserCache userCache;

    @Override
    protected void configure(AuthenticationManagerBuilder auth)
        throws Exception
    {
        
        TofocusAuthenticationProvider authenticationProvider =
            new TofocusAuthenticationProvider(userDetailsService, tfPasswordEncoder, captchaGenerate);
        ServerRunAsGrantAuthenticationProvider serverRunAsGrantProvider = new ServerRunAsGrantAuthenticationProvider(
            userDetailsService, appCache, appLoginCheckDao, tofocusUserDetailsService, userCache, captchaGenerate);
        auth.authenticationProvider(serverRunAsGrantProvider)
            .authenticationProvider(authenticationProvider)
            .userDetailsService(userDetailsService)
            .passwordEncoder(tfPasswordEncoder);
    }
    
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean()
        throws Exception
    {
        return super.authenticationManagerBean();
    }
    
    @Override
    public void configure(HttpSecurity http)
        throws Exception
    {
        // @formatter:off
        
        //图片验证码放在认证之前
        http
        .csrf().disable()
            //允许从其他主机用POST方式访问
            .cors().and()
            
            //面向Oauth2认证的接口，不需加入
            .requestMatchers()
            //会跳转统一登录页面的url
            .antMatchers("/") //页面
            //资源
            .antMatchers("/js/**", "/css/**","/webjars/**","/assets/**","/images/**")
            //验证是否启动完成
            .antMatchers("/actuator/info", "/server/actuator")
            .antMatchers("/captcha/**")
            //账号服务
            .antMatchers("/v2/user/regist/**")
            .antMatchers("/v2/user/resetpwd/**")
            .antMatchers("/v2/user/loginCaptcha")
            .antMatchers(SwaggerApiConfig.swaggerApiUrls())
            //登录
            .antMatchers("/login/**","/oauth/authorize", "/logout/**")
            .antMatchers("/oauth/token")
            .and()
            .authorizeRequests()
            //页面url公开
            .antMatchers("/").permitAll() //公开页面
            .antMatchers("/actuator/info", "/server/actuator").permitAll()
            .antMatchers("/captcha/**").permitAll()
            //账号服务
            .antMatchers("/v2/user/regist/**").permitAll()
            .antMatchers("/v2/user/resetpwd/**").permitAll()
            .antMatchers("/v2/user/loginCaptcha").permitAll()
            .antMatchers(SwaggerApiConfig.swaggerApiUrls()).permitAll()
            //资源url公开
            .antMatchers("/js/**", "/css/**","/webjars/**","/assets/**","/images/**").permitAll()
            //.antMatchers("/oauth/token").permitAll()
            //其他url跳转登录页面
            .anyRequest().authenticated() //其他页面需要登录
        .and()
            .formLogin()
            .loginPage("/login")
            .permitAll();
        // @formatter:on
    }
}
