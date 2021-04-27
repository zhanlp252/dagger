package io.github.novareseller.security.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: Bowen huang
 * @date: 2021/04/26
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "spring.dagger.security.jwt")
@ToString
public class JwtProperties {

    /**
     * Algorithms supported by jwt
     * @see https://github.com/jwtk/jjwt
     */
    private String algorithm = "HS256";

    /**
     * token expire time
     *
     * Unit: second
     */
    private long ttl = 1000 * 60 * 60 * 12;

    /**
     * Parsing time-consuming switches
     */
    private boolean parseDebug  = false;

}
