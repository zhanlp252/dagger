package io.github.novareseller.security.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author: Bowen huang
 * @date: 2021/04/26
 */
@Configuration
public class JwtProperties {


    @Value("spring.sa-token.jwt.algorithm")
    private String algorithm = "HS256";


    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }
}
