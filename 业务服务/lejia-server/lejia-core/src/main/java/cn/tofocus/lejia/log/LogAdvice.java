package cn.tofocus.lejia.log;

import javax.annotation.PostConstruct;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.core.log.LogApi;
import cn.tofocus.core.log.LogHelper;
import cn.tofocus.core.log.LogInfo;
import cn.tofocus.core.log.LogWriter;
import cn.tofocus.core.util.SpringBeanUtil;
import cn.tofocus.lejia.bean.entity.sys.SysLog;
import cn.tofocus.lejia.cache.LogWriteCache;
import cn.tofocus.lejia.core.CurrentSession;

@Aspect
@Service
public class LogAdvice implements LogWriter
{
    @Autowired
    private LogWriteCache dao;
    
    @Autowired
    private SpringBeanUtil springBeanUtil;
    
    private final int maxStrLen = 200;
    
    private LogHelper logHelper;
    
    @PostConstruct
    private void init()
    {
        logHelper = new LogHelper(springBeanUtil);
    }
	
    @Around("within(cn.tofocus.lejia.api..*) && @annotation(logApi) && !execution(* cn.tofocus.lejia.api.v1.SaasLoginRest.login(..))")
    private Object accessApi(ProceedingJoinPoint joinPoint, LogApi logApi)
        throws Throwable
    {
        Object result = logHelper.proceed(joinPoint,
            logApi,
            CurrentSession.companyPkey(),
            CurrentSession.marketPkey(),
            maxStrLen,
            this);
        return result;
    }
    
	
	@Override
	public void add(LogInfo log) 
	{
		SysLog logEntity = BeanUtil.beanFrom(SysLog.class, log);
		logEntity.setAscription(CurrentSession.ascriptionPkey());
        dao.add(logEntity);
	}

    
}
