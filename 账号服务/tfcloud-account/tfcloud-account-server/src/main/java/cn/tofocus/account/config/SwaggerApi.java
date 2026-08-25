package cn.tofocus.account.config;

import org.springframework.stereotype.Component;

import cn.tofocus.config.SwaggerApiConfig;

@Component
public class SwaggerApi implements SwaggerApiConfig
{
    @Override
    public String basePackage()
    {
        return "cn.tofocus.account.api";
    }
    
}
