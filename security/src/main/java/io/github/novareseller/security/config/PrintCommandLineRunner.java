package io.github.novareseller.security.config;

import io.github.novareseller.security.properties.JwtProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author: Bowen huang
 * @date: 2021/04/27
 */
@Component
@Slf4j
public class PrintCommandLineRunner implements CommandLineRunner {

    @Autowired
    private JwtProperties jwtProperties;

    @Override
    public void run(String... args) throws Exception {
        log.info("=============Dagger Security Properties=============");
        log.info(jwtProperties.toString());
        log.info("====================================================");
    }
}
