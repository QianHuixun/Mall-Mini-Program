package cn.tofocus.authentication.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import cn.tofocus.authentication.action.captcha.ImageCaptchaGenerate;
import cn.tofocus.common.util.security.Base64;
import cn.tofocus.core.captcha.CaptchaChecker;
import cn.tofocus.core.exception.SysErrCode;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.account.db.dao.application.ApplicationDao;
import cn.tofocus.account.db.entity.application.ApplicationEntity;

/**
 * 图片验证码过滤器
 * OncePerRequestFilter 过滤器只会调用一次
 *
 * @author CaiRui
 * @date 2018-12-10 12:23
 */
@Configuration
@Order(100)
public class CaptchaFilter extends OncePerRequestFilter
{
    @Autowired
    private ApplicationDao appCache;
    
    @Autowired
    private ImageCaptchaGenerate captchaGenerate;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException
    {
        //表单登录的post请求
        if (StringUtils.equals("/oauth/token", request.getRequestURI())
            && StringUtils.equalsIgnoreCase("post", request.getMethod()))
        {
            if ("password".equals(request.getParameter("grant_type")))
            {
                String appid = request.getParameter("client_id");
                if (appid == null)
                {
                    String s = request.getHeader("authorization");
                    if (s != null && s.startsWith("Basic ") && s.length() > 6)
                    {
                        String str = Base64.decodeString(s.substring(6));
                        if (str.contains(":"))
                        {
                            appid = str.split(":")[0];
                        }
                    }
                }
                if (!StringUtils.isBlank(appid))
                {
                    ApplicationEntity app = appCache.get(appid);
                    if (app != null && app.getNeedCaptcha() != null && app.getNeedCaptcha())
                    {
                        try
                        {
                            String target = request.getParameter("codeTarget");
                            String code = request.getParameter("code");
                            validate(target, code);
                        }
                        catch (Exception captchaException)
                        {
                            response.setContentType("application/json;charset=UTF-8");
                            Map<String, String> map = new HashMap<>();
                            map.put("error", "invalid_captcha");
                            map.put("error_description", "无效的验证码");
                            response.getWriter().write(JsonUtil.toString(map));
                            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                            return;
                        }
                    }
                }
            }
        }
        filterChain.doFilter(request, response);
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
