package io.github.novareseller.log.configuration;

import io.github.novareseller.log.aop.AspectLogAop;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 自定义埋点的springboot自动装配类
 *
 * @author bowen
 * @date 2021/05/05
 */
@Configuration
public class LogAspectAutoConfiguration {

    @Bean
    public AspectLogAop aspectLogAop() {
        return new AspectLogAop();
    }
}
