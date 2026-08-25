package cn.tofocus.lejia.cache;

import java.time.Duration;

import org.springframework.stereotype.Component;

import cn.tofocus.core.token.AccessToken;
import cn.tofocus.db.redis.RedisMap;

@Component
public class SaasTokenCache extends RedisMap<AccessToken> 
{
    @Override
    protected String cacheName() {
        return "zyysc:saas:token";
    }

    @Override
    protected Duration defaultTimeout() {
        return Duration.ofDays(365);
    }
}
