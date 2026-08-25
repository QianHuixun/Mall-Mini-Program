package cn.tofocus.lejia.cache;

import java.time.Duration;
import java.util.ArrayList;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.TypeReference;

import cn.tofocus.db.redis.RedisMap;

@Component
public class OrderNumberMap extends RedisMap<ArrayList<String>> {

    @Override
    protected String cacheName() {
        return "lejia:order:number";
    }

    @Override
    protected Duration defaultTimeout() {
        return Duration.ofDays(1);
    }
    /**
     * 泛型必须重载此方法
     * @return
     */
    @Override
    protected TypeReference<ArrayList<String>> valueType()
    {
        return new TypeReference<ArrayList<String>>() {};
    }
}
