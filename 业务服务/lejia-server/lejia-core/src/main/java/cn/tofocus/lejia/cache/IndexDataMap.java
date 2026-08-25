package cn.tofocus.lejia.cache;

import java.time.Duration;
import java.util.List;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.TypeReference;

import cn.tofocus.db.redis.RedisMap;
import cn.tofocus.lejia.bean.dto.data.IndexYFDTO;

@Component
public class IndexDataMap extends RedisMap<List<IndexYFDTO>> {

	@Override
	protected String cacheName() {
		return "lejia:yesterday:data";
	}

	@Override
	protected Duration defaultTimeout() {
		return Duration.ofDays(2);
	}
	
	 /**
     * 泛型必须重载此方法
     * @return
     */
    protected TypeReference<List<IndexYFDTO>> valueType()
    {
        return new TypeReference<List<IndexYFDTO>>() {};
    }
}
