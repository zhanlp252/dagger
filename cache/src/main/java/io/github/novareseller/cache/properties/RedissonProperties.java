package io.github.novareseller.cache.properties;

import io.github.novareseller.cache.enums.LockModel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 *
 * @author Nikita Koksharov
 * @author AnJia (https://anjia0532.github.io/)
 *
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "spring.dagger.cache.redisson")
public class RedissonProperties {

    private String config;

    private String file;

    private String open = "false";

    private long attemptTimeout = 30 * 1000;

    private long lockWatchdogTimeout = 30 * 1000;

    private LockModel lockModel = LockModel.AUTO;


}
