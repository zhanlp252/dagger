package io.github.novareseller.log.configuration;

import io.github.novareseller.log.configuration.property.LogProperty;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Log的参数自动装配类
 *
 * @author bowen
 * @date 2021/05/05
 */
@Configuration
@EnableConfigurationProperties(LogProperty.class)
@AutoConfigureAfter(LogCommonAutoConfiguration.class)
@PropertySource(
        name = "Log Default Properties",
        value = "classpath:/META-INF/log-default.properties")
public class LogPropertyConfiguration {

    @Bean
    public LogPropertyInit tLogPropertyInit(LogProperty logProperty) {
        LogPropertyInit logPropertyInit = new LogPropertyInit();
        logPropertyInit.setPattern(logProperty.getPattern());
        logPropertyInit.setEnableInvokeTimePrint(logProperty.enableInvokeTimePrint());
        logPropertyInit.setIdGenerator(logProperty.getIdGenerator());
        logPropertyInit.setMdcEnable(logProperty.getMdcEnable());
        return logPropertyInit;
    }
}
