package io.github.novareseller.security;

import io.github.novareseller.security.config.JwtRegisterBean;
import io.github.novareseller.security.config.PrintCommandLineRunner;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author: Bowen huang
 * @date: 2021/04/27
 */
@Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(value = {java.lang.annotation.ElementType.TYPE})
@Documented
@Import({JwtRegisterBean.class, PrintCommandLineRunner.class})
public @interface EnableDaggerJwt {

}
