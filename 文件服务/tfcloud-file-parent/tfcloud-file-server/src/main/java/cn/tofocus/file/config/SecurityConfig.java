package cn.tofocus.file.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

import cn.tofocus.config.SwaggerApiConfig;
import cn.tofocus.core.security.BaseSecurityConfig;
import cn.tofocus.file.bean.Constant;

@Configuration
@EnableResourceServer
@Order(1)
public class SecurityConfig extends BaseSecurityConfig
{
    
    @Override
    public void configure(HttpSecurity http)
        throws Exception
    {
        // @formatter:off
        http
            .csrf().disable()
            .requestMatchers()
            .antMatchers("/v1/download/**", "/v1/image/**", "/v2/download/**", "/v2/image/**")
            .antMatchers(Constant.imgUrl, Constant.imgDownLoadUrl, Constant.fileDownLoadUrl)
            .antMatchers("/actuator/info", "/server/actuator")
            .antMatchers(SwaggerApiConfig.swaggerApiUrls())
        .and()
            .authorizeRequests()
            .antMatchers("/v1/download/**", "/v1/image/**", "/v2/download/**", "/v2/image/**").permitAll()
            .antMatchers(Constant.imgUrl, Constant.imgDownLoadUrl, Constant.fileDownLoadUrl).permitAll()
            .antMatchers("/actuator/info", "/server/actuator").permitAll()
            .antMatchers(SwaggerApiConfig.swaggerApiUrls()).permitAll()
            .anyRequest().authenticated();
        // @formatter:on
    }
    
}
