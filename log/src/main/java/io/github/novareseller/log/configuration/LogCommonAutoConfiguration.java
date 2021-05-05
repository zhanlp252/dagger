package io.github.novareseller.log.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author bowen
 * @date 2021/05/05
 */
@Configuration
public class LogCommonAutoConfiguration {

    @Bean
    public LogSpringAware tLogSpringAware(){
        return new LogSpringAware();
    }
}
