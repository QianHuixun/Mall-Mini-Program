package cn.tofocus.lejia.cache;

import java.time.Duration;

import org.springframework.stereotype.Component;

import cn.tofocus.db.redis.RedisMap;

@Component
public class IndexSingleDataMap extends RedisMap<Object>
{


	@Override
	protected String cacheName() {
		return "lejia:yesterday:single:data";
	}

	@Override
	protected Duration defaultTimeout() {
		return Duration.ofDays(2);
	}

}
