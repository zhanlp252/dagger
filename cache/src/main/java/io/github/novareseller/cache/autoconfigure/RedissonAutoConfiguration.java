package io.github.novareseller.cache.autoconfigure;

import io.github.novareseller.cache.constant.CacheConst;
import io.github.novareseller.cache.properties.RedissonProperties;
import io.github.novareseller.cache.support.util.SpringUtil;
import net.oschina.j2cache.J2CacheConfig;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * redisson 配置入口
 *
 * @author bowen
 * @date 2021/05/14
 */
@Configuration
@ConditionalOnBean({J2CacheConfig.class, J2CacheSpringRedisAutoConfiguration.class})
@AutoConfigureAfter(J2CacheSpringRedisAutoConfiguration.class)
@EnableConfigurationProperties({RedissonProperties.class})
@ConditionalOnProperty(value = "spring.dagger.cache.redisson.open", havingValue = "true")
public class RedissonAutoConfiguration {

    private static final String REDIS_PROTOCOL_PREFIX = "redis://";
    private static final String REDISS_PROTOCOL_PREFIX = "rediss://";

    @Autowired(required = false)
    private List<RedissonAutoConfigurationCustomizer> redissonAutoConfigurationCustomizers;

    @Autowired
    private RedissonProperties redissonProperties;


    @Bean
    @ConditionalOnMissingBean(StringRedisTemplate.class)
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }


    @Bean("redissonConfig")
    @DependsOn("j2CacheConfig")
    public Config redissonConfig(J2CacheConfig j2CacheConfig) {
        Properties l2CacheProperties = j2CacheConfig.getL2CacheProperties();
        String hosts = l2CacheProperties.getProperty("hosts");
        String mode = l2CacheProperties.getProperty("mode") == null ? "null" : l2CacheProperties.getProperty("mode");
        String clusterName = l2CacheProperties.getProperty("cluster_name");
        String password = StringUtils.isEmpty(l2CacheProperties.getProperty("password")) ? null
                : l2CacheProperties.getProperty("password");

        int database = l2CacheProperties.getProperty("database") == null ? 0
                : Integer.parseInt(l2CacheProperties.getProperty("database"));

        boolean isSSL = l2CacheProperties.getProperty("isSsl") == null ? false
                : Boolean.valueOf(l2CacheProperties.getProperty("isSsl"));

        int timeout = l2CacheProperties.getProperty("timeout") == null ? 5000
                : Integer.parseInt(l2CacheProperties.getProperty("timeout"));

        Config config ;
        if (redissonProperties.getConfig() != null) {
            try {
                config = Config.fromYAML(redissonProperties.getConfig());
            } catch (IOException e) {
                try {
                    config = Config.fromJSON(redissonProperties.getConfig());
                } catch (IOException e1) {
                    throw new IllegalArgumentException("Can't parse config", e1);
                }
            }
        } else if (redissonProperties.getFile() != null) {
            try {
                InputStream is = getConfigStream();
                config = Config.fromYAML(is);
            } catch (IOException e) {
                // trying next format
                try {
                    InputStream is = getConfigStream();
                    config = Config.fromJSON(is);
                } catch (IOException e1) {
                    throw new IllegalArgumentException("Can't parse config", e1);
                }
            }
        } else if (CacheConst.SENTINEL_MODE.equalsIgnoreCase(mode)) {
            config = new Config();
            config.useSentinelServers()
                    .setMasterName(clusterName)
                    .addSentinelAddress(convert(hosts))
                    .setDatabase(database)
                    .setConnectTimeout(timeout)
                    .setPassword(password);
        } else if (CacheConst.CLUSTER_MODE.equalsIgnoreCase(mode)) {
            config = new Config();
            config.useClusterServers()
                    .addNodeAddress(convert(hosts))
                    .setConnectTimeout(timeout)
                    .setPassword(password);
        } else if (CacheConst.SINGLE_MODE.equalsIgnoreCase(mode)){
            config = new Config();
            String prefix = isSSL ? REDISS_PROTOCOL_PREFIX : REDIS_PROTOCOL_PREFIX;

            config.useSingleServer()
                    .setAddress(prefix + hosts)
                    .setConnectTimeout(timeout)
                    .setDatabase(database)
                    .setPassword(password);
        }else {
            throw new IllegalArgumentException("redisson not support use mode [sharded]!!");
        }
        if (redissonAutoConfigurationCustomizers != null) {
            for (RedissonAutoConfigurationCustomizer customizer : redissonAutoConfigurationCustomizers) {
                customizer.customize(config);
            }
        }
        return config;
    }


    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean(RedissonClient.class)
    @DependsOn("redissonConfig")
    public RedissonClient redissonClient(Config config) {
        return Redisson.create(config);
    }

    private String[] convert(String hosts) {
        String[] hostArr = hosts.split(CacheConst.COMMA);
        List<String> nodes = new ArrayList<>(hostArr.length);
        for (String host : hostArr) {
            if (!host.startsWith(REDIS_PROTOCOL_PREFIX) && !host.startsWith(REDISS_PROTOCOL_PREFIX)) {
                nodes.add(REDIS_PROTOCOL_PREFIX + host);
            } else {
                nodes.add(host);
            }
        }
        return nodes.toArray(new String[nodes.size()]);
    }

    private InputStream getConfigStream() throws IOException {
        Resource resource = SpringUtil.getApplicationContext().getResource(redissonProperties.getFile());
        InputStream is = resource.getInputStream();
        return is;
    }


}
