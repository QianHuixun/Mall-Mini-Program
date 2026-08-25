package cn.tofocus.lejia.cache;

import java.time.Duration;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import cn.tofocus.db.redis.KeySerializer;

@Component
public class AccessMap {

	@Value("${tofocus.prefix}")
	private String prefix;

	protected String root;

	@Autowired
	private RedisConnectionFactory connectionFactory;

	protected RedisTemplate<String, String> template;

	private SetOperations<String, String> setOperations;

	protected Duration defaultTimeout;
	protected KeySerializer<String> keysKeySerializer;

	protected String cacheName() {
		return "lejia:access:time";
	}

	protected Duration defaultTimeout() {
		return Duration.ofDays(2);
	}

	@PostConstruct
	private void init() {

		template = new RedisTemplate<>();
		template.setConnectionFactory(connectionFactory);
		// redis value使用的序列化器
		template.setValueSerializer(new StringRedisSerializer());
		// redis key使用的序列化器
		template.setKeySerializer(new StringRedisSerializer());
		template.afterPropertiesSet();
		setOperations = template.opsForSet();
		root = prefix + ":redis_cache:" + cacheName() + ":";
		defaultTimeout = defaultTimeout();

	}

	public void put(String key, String value) {
		setOperations.add(root + key, value);
	}

	public Set<String> findAll(String keysRoot) 
	{
		String s = root + keysRoot;
		return setOperations.members(s);
	}
	
	public Boolean removeAll(String keysRoot)
	{
		String s = root + keysRoot;
		return template.delete(s);
	}
}
