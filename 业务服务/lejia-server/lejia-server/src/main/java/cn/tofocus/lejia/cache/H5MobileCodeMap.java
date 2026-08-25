package cn.tofocus.lejia.cache;

import java.time.Duration;

import org.springframework.stereotype.Component;

import cn.tofocus.db.redis.RedisMap;

@Component
public class H5MobileCodeMap extends RedisMap<String> {

    @Override
    protected String cacheName() {
        return "zyysc:h5:mobile:number";
    }

    @Override
    protected Duration defaultTimeout() {
        return Duration.ofMinutes(3);
    }
}
