//package cn.tofocus.lejia.domain.sys;
//
//import java.util.Map;
//import java.util.Map.Entry;
//
//import org.springframework.beans.BeansException;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.ApplicationContextAware;
//import org.springframework.context.ApplicationListener;
//import org.springframework.context.event.ContextClosedEvent;
//import org.springframework.stereotype.Component;
//
//import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
//
//@Component
//public class LejiaServerInstance
//    implements CommandLineRunner, ApplicationContextAware, ApplicationListener<ContextClosedEvent>
//{
//
//    @Override
//    public void onApplicationEvent(ContextClosedEvent event)
//    {
//        
//    }
//
//    @Override
//    public void setApplicationContext(ApplicationContext applicationContext)
//        throws BeansException
//    {
//        Map<String, Object> map = applicationContext.getBeansWithAnnotation(Component.class);
//        for (Entry<String, Object> entry : map.entrySet())
//        {
//            String name = entry.getKey();
//            Object bean = entry.getValue();
//            if(bean instanceof JpaSpecificationDelegate)
//            {
//                JpaSpecificationDelegate s = (JpaSpecificationDelegate)bean;
//                System.out.println(bean);
//                System.out.println("初始化20220321");
////                s.resetID(1);
//            }
//        }
//    }
//
//    @Override
//    public void run(String... args)
//        throws Exception
//    {
//        
//    }
//    
//}
