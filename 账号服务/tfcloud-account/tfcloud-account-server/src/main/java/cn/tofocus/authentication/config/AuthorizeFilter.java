package cn.tofocus.authentication.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 拦截SESSION 失效
 */
@Configuration
public class AuthorizeFilter extends OncePerRequestFilter
{
    
    private RequestCache requestCache = new HttpSessionRequestCache();
    
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
        FilterChain filterChain)
        throws ServletException, IOException
    {
        String uri = httpServletRequest.getRequestURI();
        if ("/oauth/authorize".equals(uri))
        {
            SavedRequest savedRequest = requestCache.getRequest(httpServletRequest, httpServletResponse);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && savedRequest == null)
            {
                httpServletRequest.getSession().invalidate();
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
