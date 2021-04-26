package io.github.novareseller.security.model;

import cn.dev33.satoken.stp.SaTokenInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author: Bowen huang
 * @date: 2021/04/26
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class LoginUser  {


    private String tenantId;
    private String loginId;
    private String type;
    private String account;
    private String country;
    private String language;
    private String terminal;

}
