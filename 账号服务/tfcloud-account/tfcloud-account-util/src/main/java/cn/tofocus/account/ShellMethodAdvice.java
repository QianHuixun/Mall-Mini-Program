package cn.tofocus.account;

import java.util.concurrent.TimeUnit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.stereotype.Service;

import cn.tofocus.common.util.NumUtil;

@Aspect
@Service
public class ShellMethodAdvice
{
    @Around("within(cn.tofocus.account.command..*) && @annotation(target)")
    public Object printApi(ProceedingJoinPoint joinPoint, ShellMethod target)
        throws Throwable
    {
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long t = System.currentTimeMillis() - start;
        if (t > 100)
            System.out.println("[执行时间 - " + NumUtil.timeToCh(t, TimeUnit.MILLISECONDS) + "]");
        return result;
    }
}
