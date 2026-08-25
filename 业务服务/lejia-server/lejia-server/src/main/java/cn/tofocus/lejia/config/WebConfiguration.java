package cn.tofocus.lejia.config;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.core.exception.SysErrCode;
import cn.tofocus.core.exception.TofocusException;

@Component
public class WebConfiguration implements ApplicationListener<WebServerInitializedEvent>
{
    private int serverPort;
    
    @Autowired
    private RequestMappingHandlerAdapter handlerAdapter;
    
    @PostConstruct
    public void initEditableValidation()
    {
        ConfigurableWebBindingInitializer initializer =
            (ConfigurableWebBindingInitializer)handlerAdapter.getWebBindingInitializer();
        if (initializer != null && initializer.getConversionService() != null)
        {
            GenericConversionService genericConversionService =
                (GenericConversionService)initializer.getConversionService();
            if (genericConversionService != null)
                genericConversionService.addConverter(new StringToDateConverter());
        }
    }
    
    public static class StringToDateConverter implements Converter<String, Date>
    {
        @Override
        public Date convert(String source)
        {
            try
            {
                Date d = DateUtil.formatDateStr(source);
                return d;
            }
            catch (Exception e)
            {
                throw TofocusException.of(SysErrCode.REQUIRED_PRARAM_ERROR,
                    source + "无法转换为时间类型: " + e.getClass().getSimpleName() + " " + e.getMessage());
            }
        }
    }
    
    @Override
    public void onApplicationEvent(WebServerInitializedEvent event)
    {
        this.serverPort = event.getWebServer().getPort();
    }
    
    public int getPort()
    {
        return this.serverPort;
    }
    
    public String getHost()
    {
        
        String host = null;
        try
        {
            host = InetAddress.getLocalHost().getHostAddress();
        }
        catch (UnknownHostException e)
        {
        }
        
        return host;
    }
}
