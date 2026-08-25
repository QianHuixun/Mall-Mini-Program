//package cn.tofocus.lejia.log;
//
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.stereotype.Service;
//
//import cn.tofocus.common.util.StringUtil;
//import cn.tofocus.common.util.date.DateUtil;
//import cn.tofocus.core.json.JsonUtil;
//import cn.tofocus.lejia.bean.entity.market.MktMember;
//import cn.tofocus.lejia.bean.entity.sys.SysFarmer;
//import cn.tofocus.lejia.cache.AccessMap;
//import cn.tofocus.lejia.core.MobileSession;
//
//@Aspect
//@Service
//public class ZxApiAdvice
//{
//    
//    //cn.tofocus.core.Result
//    @Around(value = "execution(* cn.tofocus.lejia.domain..*(..)) && @annotation(zxAnnotation)")
//    private Object access(ProceedingJoinPoint joinPoint, ZxAnnotation zxAnnotation)
//        throws Throwable
//    {
//        Object result = joinPoint.proceed();
//        String resultFormat = zxAnnotation.resultFormat();
//        char[] chars = resultFormat.toCharArray();
//        StringBuilder wordBuilder = null;
//        Map<String,Object> map = new HashMap<>();
//        map.put("result", result);
////        System.out.println("result: " + JsonUtil.toString(result));
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < chars.length; i++)
//        {
//            char c = chars[i];
//            if (c == '{')
//            {
//                wordBuilder = new StringBuilder();
//            }
//            else if (c == '}')
//            {
//                if (wordBuilder != null)
//                {
//                    String word = wordBuilder.toString();
//                    String[] strs = word.split("\\.");
//                    if (strs.length > 0)
//                    {
//                        Object obj = map.get(strs[0]);
//                        if (strs.length == 1)
//                        {
//                            sb.append(StringUtil.toLimitJsonString(result, 200));
//                        }
//                        else
//                        {
////                            buildObject(sb, strs, 1, obj, 200);
//                        }
//                    }
//                }
//                wordBuilder = null;
//            }
//            else
//            {
//                if (wordBuilder == null)
//                    sb.append(c);
//                else
//                    wordBuilder.append(c);
//            }
//        }
////        System.out.println("sb: " + sb);
//        if(wordBuilder != null)
//            System.out.println("wordBuilder: " + wordBuilder);
//        return result;
//    }
//  
//    
//    
//}
