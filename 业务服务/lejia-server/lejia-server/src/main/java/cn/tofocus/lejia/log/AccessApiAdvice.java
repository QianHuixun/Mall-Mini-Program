package cn.tofocus.lejia.log;

import java.util.Date;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.lejia.bean.entity.member.MktMember;
import cn.tofocus.lejia.bean.entity.sys.SysFarmer;
import cn.tofocus.lejia.cache.AccessMap;
import cn.tofocus.lejia.core.MobileSession;

@Aspect
@Service
public class AccessApiAdvice 
{

	@Autowired
	private AccessMap accessMap;
	
	@Around(value = "execution(cn.tofocus.core.Result cn.tofocus.lejia.app..*(..)) ")
	private Object access(ProceedingJoinPoint joinPoint) throws Throwable
	{
		System.out.println("redis 存");
		Object result = null;
		try {
			String openid = null;
			MktMember member = MobileSession.member();
			SysFarmer farmer = MobileSession.farmer();
			Integer appid = MobileSession.appid();
			if(farmer != null)
			    System.out.println("farmerPkey: " + farmer.getPkey() + " farmerName: " + farmer.getName());
			if(member != null)
				openid = member.getOpenid1();
			if(openid != null)
			{
				if(farmer != null)
				{
					accessMap.put(DateUtil.formatDate(new Date(), "yyyy-MM-dd") + "," + appid, openid + "," + farmer.getPkey() + "," + farmer.getOrg());
				}
				else
					accessMap.put(DateUtil.formatDate(new Date(), "yyyy-MM-dd") + "," + appid, openid);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		result = joinPoint.proceed();
		return result;
	}
	
}
