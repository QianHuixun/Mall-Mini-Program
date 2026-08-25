package cn.tofocus.file;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.validation.ConstraintViolationException;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestController;
import cn.tofocus.core.exception.ExceptionHandle;
import cn.tofocus.core.exception.TofocusException;
import feign.FeignException;

@Aspect
@Service
public class ApiAdvice
{
    private ExceptionHandle exceptionHandle;
    
    @PostConstruct
    public void init()
    {
        exceptionHandle= new ExceptionHandle();
        Set<String> skipstatck = new HashSet<>();
        skipstatck.add("90000020");
        exceptionHandle.setSkipstatck(skipstatck);
    }
    
    @Around("execution(cn.tofocus.core.Result cn.tofocus.file.api.v3..*.*(..))")
    public Object printApi(ProceedingJoinPoint joinPoint)
        throws Throwable
    {
        try
        {
            Object result = joinPoint.proceed();
            return result;
        }
        catch (FeignException e)
        {
            return exceptionHandle.handleFeignException(e);
        }
        catch (TofocusException e)
        {
            return exceptionHandle.handleTofocusException(e);
        }
        catch (MethodArgumentNotValidException e)
        {
            return exceptionHandle.handleMethodArgumentNotValidException(e);
        }
        catch (ConstraintViolationException e)
        {
            return exceptionHandle.handleConstraintViolationException(e);
        }
        catch (Exception e)
        {
            return exceptionHandle.handle(e);
        }
    }
    
    
}
