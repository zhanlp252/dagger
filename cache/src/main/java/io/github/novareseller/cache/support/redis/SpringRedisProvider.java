package io.github.novareseller.cache.support.redis;

import io.github.novareseller.cache.properties.J2CacheProperties;
import io.github.novareseller.cache.support.util.SpringUtil;
import net.oschina.j2cache.*;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * spring redis缓存
 * @author zhangsaizz
 *
 */
public class SpringRedisProvider implements CacheProvider {

	private RedisTemplate<String, Serializable> redisTemplate;
	
	private J2CacheProperties config;

	private String namespace;

	private String storage;
	
	protected ConcurrentHashMap<String, Cache> caches = new ConcurrentHashMap<>();

	@Override
	public String name() {
		return "redis";
	}

	@Override
	public int level() {
		return CacheObject.LEVEL_2;
	}

	@Override
	public Collection<CacheChannel.Region> regions() {
		return Collections.emptyList();
	}

	@Override
	public Cache buildCache(String region, CacheExpiredListener listener) {
		if(config.isL2CacheOpen() == false) {
			return new NullCache();
		}
		Cache cache = caches.get(region);
		if (cache == null) {
			synchronized (SpringRedisProvider.class) {
				cache = caches.get(region);
				if (cache == null) {
	                if("hash".equalsIgnoreCase(this.storage)){
						cache = new SpringRedisCache(this.namespace, region, redisTemplate);
					} else {
	                	cache = new SpringRedisGenericCache(this.namespace, region, redisTemplate);
					}
					caches.put(region, cache);
				}
			}
		}
		return cache;
	}

	@Override
	public Cache buildCache(String region, long timeToLiveInSeconds, CacheExpiredListener listener) {
		return buildCache(region, listener);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void start(Properties props) {
		this.namespace = props.getProperty("namespace");
		this.storage = props.getProperty("storage");
		this.config =  SpringUtil.getBean(J2CacheProperties.class);
		if(config.isL2CacheOpen() == false) {
			return;
		}
		this.redisTemplate = SpringUtil.getBean("cacheRedisTemplate", RedisTemplate.class);
	}

	@Override
	public void stop() {
		// 由spring控制
	}

}
