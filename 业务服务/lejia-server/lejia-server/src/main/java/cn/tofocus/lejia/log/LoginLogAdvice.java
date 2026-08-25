package cn.tofocus.lejia.log;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.tofocus.common.util.StringUtil;
import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.core.Result;
import cn.tofocus.core.data.NamedBean;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.core.log.LogApi;
import cn.tofocus.core.security.AuthenticationContext;
import cn.tofocus.core.security.SecurityContextUtil;
//import cn.tofocus.core.security.dto.SimpleRoleInstance;
import cn.tofocus.lejia.bean.entity.sys.SysLog;
import cn.tofocus.lejia.bean.entity.sys.SysUser;
import cn.tofocus.lejia.cache.LogWriteCache;
import cn.tofocus.lejia.dao.sys.SysUserDao;

@Aspect
@Service
public class LoginLogAdvice 
{
	@Autowired
    private LogWriteCache dao;
	
	@Autowired
	private SysUserDao userDao;

    private final int maxStrLen = 200;
    
    @Around("execution(* cn.tofocus.lejia.api.v1.SaasLoginRest.login(..)) && @annotation(logApi)")
    private Object accessLogin(ProceedingJoinPoint joinPoint, LogApi logApi)
        throws Throwable
    {
        AuthenticationContext context = SecurityContextUtil.getAuthenticationContext();
        Object[] args = joinPoint.getArgs();
        SysLog log = new SysLog();
        log.setRemoteAddress(context.getRemoteAddress());
        log.setAppId((String)args[0]);
        log.setUserName((String)args[2]);
        log.setOperation("登录");
        
//        StringBuilder sb =
//            new StringBuilder().append("[").append(args[0]).append("]从[").append((String)args[3]).append("]登录");
        StringBuilder sb =
                new StringBuilder().append("账号:[").append(args[0]).append("] 于").append(DateUtil.formatDate(new Date())).append("登录后台");
        log.setContent(sb.toString());
        long start = System.currentTimeMillis();
        try
        {
            Object result = joinPoint.proceed();
            @SuppressWarnings("unchecked")
			AuthenticationContext response = ((Result<AuthenticationContext>)result).fetchResult();
            log.setSuccess(true);
            if (response.getUserkey() != null)
                log.setUserPkey(response.getUserkey().intValue());
            log.setUserId(response.getUserid());
            if(log.getUserPkey() != null)
            {
                SysUser sysUser = userDao.get(log.getUserPkey());
                if(sysUser != null)
                    log.setAscription(sysUser.getAscription());
            }
            log.setUserName(response.getNickname());
            log.setMobile(response.getBindPhone());
            if (response.getLastAccessOrg() != null)
                log.setCompany((String)response.getLastAccessOrg().getPkey());
            if (response.getLastAccessDept() != null)
                log.setMarket((String)response.getLastAccessDept().getPkey());
            if(response.getOrgRoles() != null && response.getOrgRoles().size() > 0)
            {
                Map<String, List<NamedBean>> orgRoles = response.getOrgRoles();
                System.out.println("orgRoles: " + JsonUtil.toString(orgRoles, true));
//                for(String key : response.getOrgRoles().keySet())
//                {
//                    List<NamedBean> list = response.getOrgRoles().get(key);
//                    if(response.getOrgRoles().get(key) != null && response.getOrgRoles().get(key).contains(Constant.Role.COMPANY_HEAD))
//                    {
//                        log.setCompany("运营端");
//                        log.setMarket("运营端");
//                    }
//                }
            }
            return result;
        }
        catch (Exception e)
        {
            log.setSuccess(false);
            if (e instanceof TofocusException)
            {
                log.setResult(StringUtil.limitString(((TofocusException)e).getExceptionMessage(), maxStrLen));
            }
            else
            {
                log.setResult(StringUtil.limitString(e.getClass().getName() + ":" + e.getMessage(), maxStrLen));
            }
            throw e;
        }
        finally
        {
            log.setBeginTime(new Date(start));
            log.setProcMillisecond((int)(System.currentTimeMillis() - start));
            dao.add(log);
        }
    }
}
