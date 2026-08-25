package cn.tofocus.lejia.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

import cn.tofocus.config.SwaggerApiConfig;

@Configuration
@EnableResourceServer
@Order(1)
public class SecurityConfig extends WebSecurityConfigurerAdapter
{
    @Override
    public void configure(HttpSecurity http)
        throws Exception
    {
        // @formatter:off
        http
            .csrf().disable()
            .requestMatchers()
            .antMatchers(HttpMethod.OPTIONS)
            .antMatchers("/v1/login", "/v1/login/data")
            .antMatchers("/v1/h5/login", "/v1/h5/captcha", "/v1/h5/goods/query/nottoken", "/v1/h5/login/refreshToken")
            .antMatchers("/server/init")
            .antMatchers("/v1/syb/pay/**")
            .antMatchers("/captcha/**")
            .antMatchers("/v1/app/courierlogin/**")
            .antMatchers("/v1/app/courier/**")
            .antMatchers("/v1/app/vendor/**", "/v1/app/jd/**")
            .antMatchers("/v3/app/promote/**", "/v3/app/vendor/goods/**", "/v3/app/problem/**")
            .antMatchers("/v1/app/saas/**", "/v3/app/vendor/wallet/**")
            .antMatchers("/v3/app/market/**", "/v4/app/market/**", "/v5/app/market/**")
            .antMatchers("/v1/market/desktop/download/qrCode")
            .antMatchers("/v1/app/vendorLogin/**", "/v2/app/vendorLogin/**")
            .antMatchers("/v1/app/market/**")
            .antMatchers("/v2/app/market/**")
            .antMatchers("/v1/app/supplier/**")
            .antMatchers("/v2/app/gift/**")
            .antMatchers("/v2/app/public/**")
            .antMatchers("/v1/sys/data/center/foreign", "/v1/sys/data/center/foreign/order")
            .antMatchers("/v1/wx/**")
            .antMatchers("/v1/pub/**")
            .antMatchers("/v4/app/vendor/goods/**")
            .antMatchers("/v1/vendor/wallet/test/gethdNum")
            .antMatchers(SwaggerApiConfig.swaggerApiUrls())
            .antMatchers("/v1/zx/**", "/v1/chinaums/pay/**")
            .antMatchers("/v1/ns/**")
            .antMatchers("/v1/wanli/wallet/accountRecharge", "/v1/wanli/wallet/balance")
            .antMatchers("/v1/market/vendor/uploadImage")
            .antMatchers("/v1/market/**/down/**")
            .antMatchers("/v2/market/vendor/export/zip", "/v2/market/vendor/down/code")
            .antMatchers("/v1/app/member/ins")
            .antMatchers("/v1/wanli/callback")
            .antMatchers("/v2/api-docs")
            .antMatchers("/v2/app/vendor/**")
            .antMatchers("/actuator/info")
            .antMatchers("/actuator/**")
//            .antMatchers("/swagger-ui.html", "/webjars/springfox-swagger-ui/**", "/swagger-resources/**")
//            .antMatchers("/doc.html", "/webjars/bycdao-ui/**")
            .antMatchers("/server/resetCache", "/server/changeLogLevel/**")
        .and()
            .authorizeRequests()
            .antMatchers(HttpMethod.OPTIONS).permitAll()
            .antMatchers("/v1/login", "/v1/login/data").permitAll()
            .antMatchers("/v1/h5/login", "/v1/h5/captcha", "/v1/h5/goods/query/nottoken", "/v1/h5/login/refreshToken").permitAll()
            .antMatchers("/server/init").permitAll()
            .antMatchers("/v1/syb/pay/**").permitAll()
            .antMatchers("/captcha/**").permitAll()
            .antMatchers("/v1/app/courier/**").permitAll()
            .antMatchers("/v1/app/courierlogin/**").permitAll()
            .antMatchers("/v1/app/vendor/**", "/v1/app/jd/**").permitAll()
            .antMatchers("/v1/app/saas/**", "/v3/app/vendor/wallet/**").permitAll()
            .antMatchers("/v3/app/promote/**", "/v3/app/vendor/goods/**", "/v3/app/problem/**").permitAll()
            .antMatchers("/v3/app/market/**", "/v4/app/market/**", "/v5/app/market/**").permitAll()
            .antMatchers("/v1/app/vendorLogin/**", "/v2/app/vendorLogin/**").permitAll()
            .antMatchers("/v1/app/market/**").permitAll()
            .antMatchers("/v1/app/supplier/**").permitAll()
            .antMatchers("/v1/market/desktop/download/qrCode").permitAll()
            .antMatchers(SwaggerApiConfig.swaggerApiUrls()).permitAll()
            .antMatchers("/v2/app/gift/**").permitAll()
            .antMatchers("/v2/app/public/**").permitAll()
            .antMatchers("/v2/app/market/**").permitAll()
            .antMatchers("/v1/app/member/ins").permitAll()
            .antMatchers("/v1/wx/**").permitAll()
            .antMatchers("/v1/pub/**").permitAll()
            .antMatchers("/v4/app/vendor/goods/**").permitAll()
            .antMatchers("/v1/vendor/wallet/test/gethdNum").permitAll()
            .antMatchers("/v1/wanli/callback").permitAll()
            .antMatchers("/v1/sys/data/center/foreign", "/v1/sys/data/center/foreign/order").permitAll()
            .antMatchers("/v1/zx/**", "/v1/chinaums/pay/**", "/v1/ns/**").permitAll()
            .antMatchers("/v1/wanli/wallet/accountRecharge", "/v1/wanli/wallet/balance").permitAll()
            .antMatchers("/v1/market/vendor/uploadImage").permitAll()
            .antMatchers("/v1/market/**/down/**").permitAll()
            .antMatchers("/v2/market/vendor/export/zip", "/v2/market/vendor/down/code").permitAll()
            .antMatchers("/v2/api-docs").permitAll()
            .antMatchers("/v2/app/vendor/**").permitAll()
            .antMatchers("/actuator/info").permitAll()
            .antMatchers("/actuator/**").permitAll()
//            .antMatchers("/swagger-ui.html", "/webjars/springfox-swagger-ui/**", "/swagger-resources/**").permitAll()
//            .antMatchers("/doc.html", "/webjars/bycdao-ui/**").permitAll()
            .antMatchers("/server/resetCache", "/server/changeLogLevel/**").permitAll()
            .anyRequest().authenticated();
        // @formatter:on
    }

//    private CorsConfiguration buildConfig()
//    {
//        //CorsConfiguration corsConfiguration = new CorsConfiguration();
//        //corsConfiguration.addAllowedOrigin("*"); // 1 设置访问源地址
//        //corsConfiguration.addAllowedHeader("*"); // 2 设置访问源请求头
//        //corsConfiguration.addAllowedMethod("GET"); // 3 设置访问源请求方法
//        //corsConfiguration.addAllowedMethod("POST");
//        //corsConfiguration.addAllowedMethod("DELETE");
//        //corsConfiguration.addAllowedMethod("PUT");
//        //corsConfiguration.addAllowedMethod("OPTIONS");
//        //corsConfiguration.addAllowedMethod("PATCH");
//        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowCredentials(true);
//        // 设置你要允许的网站域名，如果全允许则设为 *
//        config.addAllowedOrigin("*");
//        // 如果要限制 HEADER 或 METHOD 请自行更改
//        config.addAllowedHeader("*");
//        config.addAllowedMethod("*");
//        config.addExposedHeader("Content-Range");//这里是需要额外配置的header内容
//        return config;
//    }
//    
//    @Bean
//    public CorsFilter corsFilter()
//    {
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", buildConfig()); // 4 对接口配置跨域设置
//        return new CorsFilter(source);
//    }
}
