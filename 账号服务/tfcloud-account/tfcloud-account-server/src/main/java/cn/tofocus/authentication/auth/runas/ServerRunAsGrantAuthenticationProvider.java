package cn.tofocus.authentication.auth.runas;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;

import cn.tofocus.account.bean.application.AppKV;
import cn.tofocus.account.db.cache.application.AppReadCache;
import cn.tofocus.account.db.cache.user.UserCache;
import cn.tofocus.account.db.dao.application.AppLoginCheckDao;
import cn.tofocus.authentication.action.captcha.ImageCaptchaGenerate;
import cn.tofocus.authentication.service.TofocusUserDetailsService;
import cn.tofocus.core.captcha.CaptchaChecker;
import cn.tofocus.core.exception.SysErrCode;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.security.AccessList;
import cn.tofocus.core.security.TofocusUser;

import java.util.Map;

/**
 * 
 * ServerRunAs 认证
 * 
 * @author  MSI_NB
 * @version  [版本号, 2024-4-19]
 */
@Slf4j
public class ServerRunAsGrantAuthenticationProvider implements AuthenticationProvider
{
    private TofocusUserDetailsService userDetailsService;
    
    private AppReadCache appCache;
    
    private AppLoginCheckDao appLoginCheckDao;
    
    private TofocusUserDetailsService tofocusUserDetailsService;
    
    private UserCache userCache;
    
    private ImageCaptchaGenerate captchaGenerate;
    
    private PasswordEncoder delegatingPasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    
    public ServerRunAsGrantAuthenticationProvider(TofocusUserDetailsService userDetailsService, AppReadCache appCache,
        AppLoginCheckDao appLoginCheckDao, TofocusUserDetailsService tofocusUserDetailsService, UserCache userCache,
        ImageCaptchaGenerate captchaGenerate)
    {
        super();
        this.userDetailsService = userDetailsService;
        this.appCache = appCache;
        this.appLoginCheckDao = appLoginCheckDao;
        this.tofocusUserDetailsService = tofocusUserDetailsService;
        this.userCache = userCache;
        this.captchaGenerate = captchaGenerate;
    }
    
    @Override
    public Authentication authenticate(Authentication authentication)
    {
        ServerRunAsGrantAuthenticationToken authenticationToken = (ServerRunAsGrantAuthenticationToken)authentication;
        
        String username = authenticationToken.getName();
        String pwd = authenticationToken.getPwd();
        String host = authenticationToken.getHost();
        String clientId = authenticationToken.getClientId();
        AppKV app = appCache.get(clientId);
        if (app == null)
            throw new InvalidClientException(clientId + " 不存在");
        String domain = app.getDomainid();
        if (domain == null)
            throw new InvalidClientException(clientId + " 没有配置域");
        log.info("用户RunAs认证：{} @ [{}] in {}", username, host, clientId);
        
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
        
        //如果没有密码，对于本域注册的用户，可以免密登录
        if (pwd == null)
        {
            if (!domain.equals(userCache.get(userDetails.getUserkey()).getRegistFromDomain()))
                throw new DisabledException("用户名密码不匹配");
        }
        //如果有密码就密码校验
        else
        {
            if (app.getNeedCaptcha() != null && app.getNeedCaptcha())
            {
                String target = authenticationToken.getCodeTarget();
                String code = authenticationToken.getCode();
                validate(target, code);
            }
            if (!delegatingPasswordEncoder.matches(pwd, userDetails.getPassword()))
                throw new DisabledException("用户名密码不匹配");
        }
        tofocusUserDetailsService.extendInfo(userDetails, domain);
        Map<String, AccessList> authMap = tofocusUserDetailsService.filterRolesAndAuthorities(userDetails, domain);
        //检查登录权限
        appLoginCheckDao.checkLoginFunc(domain, clientId, authMap);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }
    
    @Override
    public boolean supports(Class<?> authentication)
    {
        return ServerRunAsGrantAuthenticationToken.class.isAssignableFrom(authentication);
    }
    
    /**
     * 图片验证码校验
     *
     * @param request
     */
    private void validate(String target, String code)
    {
        if (StringUtils.isBlank(code))
        {
            throw TofocusException.of(SysErrCode.REQUIRED_PRARAM_NULL, "验证码的值不能为空");
        }
        CaptchaChecker.checkErr(captchaGenerate.check(target, code));
    }
}
