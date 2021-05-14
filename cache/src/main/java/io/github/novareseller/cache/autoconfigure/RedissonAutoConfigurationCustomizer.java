package io.github.novareseller.cache.autoconfigure;

import org.redisson.config.Config;
/**
 * Callback interface that can be implemented by beans wishing to customize
 * the {@link org.redisson.api.RedissonClient} auto configuration
 *
 * @author Nikos Kakavas (https://github.com/nikakis)
 */
@FunctionalInterface
public interface RedissonAutoConfigurationCustomizer {

    /**
     * Customize the RedissonClient configuration.
     * @param configuration the {@link Config} to customize
     */
    void customize(final Config configuration);
}
