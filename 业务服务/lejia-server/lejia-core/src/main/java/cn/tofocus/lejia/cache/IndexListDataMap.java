package cn.tofocus.lejia.cache;

import java.time.Duration;
import java.util.List;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.TypeReference;

import cn.tofocus.db.redis.RedisMap;

@Component
public class IndexListDataMap extends RedisMap<List<List<Object>>>{
	
	@Override
	protected String cacheName() {
		return "lejia:yesterday:list:data";
	}

	@Override
	protected Duration defaultTimeout() {
		return Duration.ofDays(2);
	}
	
	 /**
     * 泛型必须重载此方法
     * @return
     */
    protected TypeReference<List<List<Object>>> valueType()
    {
        return new TypeReference<List<List<Object>>>() {};
    }
}
