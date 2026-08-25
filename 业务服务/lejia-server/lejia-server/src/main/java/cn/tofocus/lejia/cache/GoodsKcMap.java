//package cn.tofocus.lejia.cache;
//
//import java.time.Duration;
//
//import org.springframework.stereotype.Component;
//
//import cn.tofocus.db.redis.RedisMap;
//
//@Component
//public class GoodsKcMap extends RedisMap<Long> {
//
//    @Override
//    protected String cacheName() {
//        return "lejia:goods:kc";
//    }
//
//    @Override
//    protected Duration defaultTimeout() {
//        return Duration.ofDays(2);
//    }
//}
