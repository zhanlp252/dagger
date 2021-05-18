package io.github.novareseller.security.context;

import io.jsonwebtoken.Claims;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author: Bowen huang
 * @date: 2021/04/28
 */
@Getter
@Setter
@ToString
public class LoginUser {

    private long tenantId;
    private long uid;
    private String clientIp;
    private String language = "en";
    private String token = "";


    public static final LoginUser claims2LoginUser(Claims claims) {
        LoginUser loginUser = new LoginUser();
        loginUser.setUid(Long.parseLong(claims.getSubject()));
        loginUser.setTenantId(Long.parseLong(claims.getIssuer()));
        return loginUser;
    }
}
