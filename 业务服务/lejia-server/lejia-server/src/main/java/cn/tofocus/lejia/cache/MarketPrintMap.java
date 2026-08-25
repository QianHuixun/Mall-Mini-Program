//package cn.tofocus.lejia.cache;
//
//import java.time.Duration;
//
//import org.springframework.stereotype.Component;
//
//import cn.tofocus.db.redis.RedisMap;
//
//@Component
//public class MarketPrintMap extends RedisMap<String>
//{
//
//    @Override
//    protected String cacheName()
//    {
//        return "zyysc:market:order:print";
//    }
//    
//    @Override
//    protected Duration defaultTimeout() {
//        return Duration.ofDays(1);
//    }
//}
