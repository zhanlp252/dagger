package io.github.novareseller.cache.autoconfigure;

import io.github.novareseller.cache.properties.J2CacheProperties;
import io.github.novareseller.cache.support.util.SpringJ2CacheConfigUtil;
import io.github.novareseller.cache.support.util.SpringUtil;
import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.J2Cache;
import net.oschina.j2cache.J2CacheBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.StandardEnvironment;

import java.io.IOException;

/**
 * 集成j2cache到spring中的配置类
 *
 * @author bowen
 * @date 2021/05/13
 */
@ConditionalOnClass(J2Cache.class)
@EnableConfigurationProperties({J2CacheProperties.class})
@Configuration
@PropertySource(value = "${spring.dagger.cache.config-location}", encoding = "UTF-8", ignoreResourceNotFound = true)
public class J2CacheAutoConfiguration {

    @Autowired
    private StandardEnvironment standardEnvironment;

    @Bean
    public net.oschina.j2cache.J2CacheConfig j2CacheConfig() throws IOException{
    	net.oschina.j2cache.J2CacheConfig cacheConfig = SpringJ2CacheConfigUtil.initFromConfig(standardEnvironment);
    	return cacheConfig;
    }

    @Bean
    @DependsOn({"springUtil","j2CacheConfig"})
    public CacheChannel cacheChannel(net.oschina.j2cache.J2CacheConfig j2CacheConfig) throws IOException {
    	J2CacheBuilder builder = J2CacheBuilder.init(j2CacheConfig);
        return builder.getChannel();
    }

    @Bean
    public SpringUtil springUtil() {
    	return new SpringUtil();
    }

}
