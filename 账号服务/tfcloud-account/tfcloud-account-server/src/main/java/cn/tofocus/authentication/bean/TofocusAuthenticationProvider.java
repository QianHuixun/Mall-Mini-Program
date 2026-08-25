package cn.tofocus.authentication.bean;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import cn.tofocus.authentication.action.captcha.ImageCaptchaGenerate;
import cn.tofocus.authentication.config.TfPasswordEncoder;
import cn.tofocus.authentication.service.TofocusUserDetailsService;
import cn.tofocus.core.captcha.CaptchaErrType;
import cn.tofocus.core.enums.CaptchaPurpose;
import cn.tofocus.core.enums.CaptchaRouter;
import cn.tofocus.core.security.TofocusUser;
import lombok.AllArgsConstructor;

/**
 * 
 * UsernamePassword 认证
 * 
 * @author  MSI_NB
 * @version  [版本号, 2024-4-19]
 */
@AllArgsConstructor
public class TofocusAuthenticationProvider implements AuthenticationProvider
{
    
    private static final Logger log = LoggerFactory.getLogger(TofocusAuthenticationProvider.class);
    
    private TofocusUserDetailsService userDetailsService;
    
    private TfPasswordEncoder tfPasswordEncoder;

    private ImageCaptchaGenerate captchaGenerate;
    
    @Override
    public Authentication authenticate(Authentication authentication)
        throws AuthenticationException
    {
        String username = authentication.getName();
        String password = (String)authentication.getCredentials();

        log.info("用户UsernamePassword认证：{} : {}", username, password);
        TofocusUser userDetails;
        //检查用户名有效性
        try
        {
            userDetails = (TofocusUser)userDetailsService.loadUserByUsername(username);
        }
        catch (UsernameNotFoundException e)
        {
            throw new DisabledException(username + " 不存在");
        }
        if (!userDetails.isEnabled())
            throw new DisabledException(username + " 已禁用");
        if (!userDetails.isAccountNonLocked())
            throw new LockedException(username + " 已锁定");
        if (!userDetails.isAccountNonExpired())
            throw new AccountExpiredException(username + " 已过期");
        if (!userDetails.isCredentialsNonExpired())
            throw new CredentialsExpiredException(username + " 已过期");
        if (userDetails.getPassword() == null)
            throw new DisabledException(username + " 密码不匹配");
        
        //心安食足
        String xaszPwd = userDetails.getUserkey() + "\n" + password;
        
        //优先匹配密码
        if (tfPasswordEncoder.matches(xaszPwd, userDetails.getPassword()))
        {
            Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
            return new UsernamePasswordAuthenticationToken(userDetails, password, authorities);
        }
        else
        {
            log.warn("用户名密码不匹配：{},{}, 尝试手机验证码登录", username, password);
            if (CaptchaErrType.OK.equals(captchaGenerate.check(CaptchaRouter.phone, CaptchaPurpose.login, username, password)))
            {
                Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
                return new UsernamePasswordAuthenticationToken(userDetails, password, authorities);
            }
            else
            {
                throw new DisabledException("用户名密码不匹配");
            }
        }
    }
    
    @Override
    public boolean supports(Class<?> authentication)
    {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
