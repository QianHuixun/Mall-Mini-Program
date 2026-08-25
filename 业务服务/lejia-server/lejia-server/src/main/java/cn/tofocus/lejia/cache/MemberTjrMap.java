package cn.tofocus.lejia.cache;

import java.time.Duration;

import org.springframework.stereotype.Component;

import cn.tofocus.db.redis.RedisMap;

@Component
public class MemberTjrMap extends RedisMap<Long> {

    @Override
    protected String cacheName() {
        return "lejia:member:tjr";
    }

    @Override
    protected Duration defaultTimeout() {
        return Duration.ofDays(100);
    }
}
