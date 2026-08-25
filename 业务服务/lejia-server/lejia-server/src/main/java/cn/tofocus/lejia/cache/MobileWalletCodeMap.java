package cn.tofocus.lejia.cache;

import java.time.Duration;

import org.springframework.stereotype.Component;

import cn.tofocus.db.redis.RedisMap;

@Component
public class MobileWalletCodeMap extends RedisMap<String> {

    @Override
    protected String cacheName() {
        return "lejia:mobile:wallet:number";
    }

    @Override
    protected Duration defaultTimeout() {
        return Duration.ofMinutes(3);
    }
}
