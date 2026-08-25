package cn.tofocus.lejia.cache;

import java.time.Duration;

import org.springframework.stereotype.Component;

import cn.tofocus.db.redis.RedisMap;

@Component
public class OrderTokenMap extends RedisMap<Long> {

    @Override
    protected String cacheName() {
        return "lejia:order:token";
    }

    @Override
    protected Duration defaultTimeout() {
        return Duration.ofMinutes(1);
    }
}
