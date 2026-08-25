package cn.tofocus.authentication.config;

import javax.annotation.PostConstruct;

import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.security.MD5;

@Component
public class TfPasswordEncoder implements PasswordEncoder
{
    private PasswordEncoder delegatingPasswordEncoder;
    
    @PostConstruct
    public void init()
    {
        delegatingPasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
    
    @Override
    public String encode(CharSequence rawPassword)
    {
        return delegatingPasswordEncoder.encode(rawPassword);
    }
    
    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword)
    {
        String key = null;
        String pwd = null;
        String[] array = rawPassword.toString().split("\n");
        if (array.length > 1)
        {
            key = array[0];
            pwd = array[1];
        }
        else
        {
            pwd = array[0];
        }
        if (encodedPassword.startsWith("{"))
        {
            return delegatingPasswordEncoder.matches(pwd, encodedPassword);
        }
        else
        {
            //心安食足兼容
            return encodedPassword.equalsIgnoreCase(MD5.getMD5(key + pwd));
        }
    }
}
