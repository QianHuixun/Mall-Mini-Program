package cn.tofocus.lejia.cache;

import java.time.Duration;

import org.springframework.stereotype.Component;

import cn.tofocus.core.acc.CaptchaBean;
import cn.tofocus.db.redis.RedisMap;

@Component
public class CaptchaMap extends RedisMap<CaptchaBean>
{

    @Override
    protected String cacheName()
    {
        return "lejia:wechat:captcha";
    }

    @Override
    protected Duration defaultTimeout()
    {
        return Duration.ofMinutes(3);
    }
}