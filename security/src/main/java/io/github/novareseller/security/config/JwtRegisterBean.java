package io.github.novareseller.security.config;

import io.github.novareseller.security.properties.Const;
import io.github.novareseller.security.properties.JwtProperties;
import io.github.novareseller.tool.utils.SystemClock;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StopWatch;
import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * Create/parse jwt bean
 *
 * The method of use is as follows
 *
 * step 1, Turn on the @EnableDaggerJwt annotation on the @SpringBootApplication annotated class.
 * Example:
 *
 * @SpringBootApplication
 * @EnableDaggerJwt
 * public class ExampleApplication {
 *
 *     public static void main(String[] args) {
 *         SpringApplication.run(ExampleApplication.class, args);
 *     }
 *
 * }
 *
 *
 * step 2, Inject JwtRegisterBean into the classes that need to be used.
 * Example:
 *
 * @RestController
 * @RequestMapping("/security/")
 * public class TestController {
 *
 * 	@Autowired
 * 	private JwtRegisterBean jwtRegisterBean;
 *
 *  @RequestMapping("login")
 *  public String login(@RequestParam(defaultValue = "101") long tenantId,
 *  @RequestParam(defaultValue = "100001") long uid){
 *          String token=jwtRegisterBean.createToken(tenantId,uid);
 *          return token;
 *  }
 *
 * }
 *
 * @author: Bowen huang
 * @date: 2021/04/26
 */
@Configuration
@EnableConfigurationProperties(JwtProperties.class)
@Slf4j
public class JwtRegisterBean {

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * Create token based on tenant id and user id
     * @param tenantId tenant id, e.g 10001(tve-br)
     * @param uid user id
     *
     * @return token. Subsequent interface requests need to carry the token in the http header
     */
    public String createToken(long tenantId, long uid) {

        long now = SystemClock.now();
        Date date = new Date(now);

        JwtBuilder builder = Jwts.builder()
                .setHeaderParam("type", "JWT")
                .setIssuedAt(date)
                .setIssuer(String.valueOf(tenantId))
                .setSubject(String.valueOf(uid))
                .signWith(SignatureAlgorithm.forName(jwtProperties.getAlgorithm()), secretKey());

        if (jwtProperties.getTtl() >= 0) {
            builder.setExpiration(new Date(now + 1000 * jwtProperties.getTtl()));
        }

        String compact = builder.compact();

        log.info("createToken complete. tenantId={}, uid={}, token={}",
                tenantId, uid, compact);
        return compact;
    }


    /**
     * Parse the body of jwt
     * @param tokenValue token
     *
     * @return claims
     * @throws ExpiredJwtException
     * @throws UnsupportedJwtException
     * @throws MalformedJwtException
     * @throws SignatureException
     * @throws IllegalArgumentException
     */
    public Claims parseTokenClaims(String tokenValue) throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException {
        StopWatch stopWatch = null;
        if (jwtProperties.isParseDebug()) {
            stopWatch = new StopWatch();
            stopWatch.start();
        }

        Claims claims = Jwts.parser()
                .setSigningKey(Const.JWT_SECURITY.getBytes())
                .parseClaimsJws(tokenValue).getBody();

        if (jwtProperties.isParseDebug()) {
            stopWatch.stop();
            stopWatch.getTotalTimeMillis();
            log.debug("Token parsing time-consuming: {} millis. Parsed claims={}", stopWatch.getTotalTimeMillis(), claims);
        }

        return claims;
    }


    /**
     * Parse the header of jwt
     * @param tokenValue
     * @return
     * @throws ExpiredJwtException
     * @throws UnsupportedJwtException
     * @throws MalformedJwtException
     * @throws SignatureException
     * @throws IllegalArgumentException
     */
    public JwsHeader parseTokenHeader(String tokenValue) throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException {
        JwsHeader jwsHeader = Jwts.parser()
                .setSigningKey(secretKey())
                .parseClaimsJws(tokenValue).getHeader();
        return jwsHeader;
    }


    /**
     * @return UTF-8 byte array
     */
    public static byte[] secretKey() {
        byte[] bytes;
        try {
            bytes = Const.JWT_SECURITY.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw  new RuntimeException(e.getMessage());
        }
        return bytes;
    }

}
