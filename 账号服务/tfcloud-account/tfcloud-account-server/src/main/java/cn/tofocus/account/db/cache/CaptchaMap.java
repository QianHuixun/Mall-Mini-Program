package cn.tofocus.account.db.cache;

import java.time.Duration;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.TypeReference;

import cn.tofocus.core.acc.CaptchaBean;
import cn.tofocus.db.redis.RedisMap;

@Component
public class CaptchaMap extends RedisMap<CaptchaBean>
{

    @Override
    protected String cacheName()
    {
        return "acc:captcha";
    }

    @Override
    protected Duration defaultTimeout()
    {
        return Duration.ofMinutes(3);
    }
}
