package cn.tofocus.lejia.cache;

import java.time.Duration;

import org.springframework.stereotype.Component;

import cn.tofocus.db.redis.RedisMap;

@Component
public class H5OrderTokenMap extends RedisMap<Long> {

    @Override
    protected String cacheName() {
        return "zyysc:h5:order:token";
    }

    @Override
    protected Duration defaultTimeout() {
        return Duration.ofMinutes(1);
    }
}
