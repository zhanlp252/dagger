package io.github.novareseller.cache.autoconfigure;

import io.github.novareseller.cache.properties.J2CacheProperties;
import io.github.novareseller.cache.support.J2CacheCacheManger;
import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.J2Cache;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 集成spring cache的配置入口。
 * 当配置spring.dagger.cache.open-spring-cache=true
 *
 * 可使用 {@link org.springframework.cache.annotation.Cacheable}等注解
 *
 * @author bowen
 * @date 2021/05/13
 */
@Configuration
@ConditionalOnClass(J2Cache.class)
@EnableConfigurationProperties({ J2CacheProperties.class, CacheProperties.class })
@ConditionalOnProperty(name = "spring.dagger.cache.open-spring-cache", havingValue = "true")
@EnableCaching
public class J2CacheSpringCacheAutoConfiguration {

	private final CacheProperties cacheProperties;
	
	private final J2CacheProperties j2CacheProperties;

	J2CacheSpringCacheAutoConfiguration(CacheProperties cacheProperties, J2CacheProperties cacheConfig) {
		this.cacheProperties = cacheProperties;
		this.j2CacheProperties = cacheConfig;
	}

	@Bean
	@ConditionalOnBean(CacheChannel.class)
	public J2CacheCacheManger cacheManager(CacheChannel cacheChannel) {
		List<String> cacheNames = cacheProperties.getCacheNames();
		J2CacheCacheManger cacheCacheManger = new J2CacheCacheManger(cacheChannel);
		cacheCacheManger.setAllowNullValues(j2CacheProperties.isAllowNullValues());
		cacheCacheManger.setCacheNames(cacheNames);
		return cacheCacheManger;
	}


}
