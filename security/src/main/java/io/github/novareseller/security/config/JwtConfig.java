package io.github.novareseller.security.config;

import java.util.Date;
import java.util.Map;

import cn.dev33.satoken.spring.SaTokenSpringAutowired;
import com.google.common.collect.Maps;
import io.github.novareseller.security.model.LoginUser;
import io.github.novareseller.security.properties.Const;
import io.github.novareseller.security.properties.JwtProperties;
import io.github.novareseller.security.utils.SystemClock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.context.model.SaStorage;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaTokenConsts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;

@Configuration
@EnableConfigurationProperties(SaTokenConfig.class)
@AutoConfigureAfter({SaTokenSpringAutowired.class})
@Slf4j
public class JwtConfig {

    @Autowired
    private JwtProperties jwtProperties;


    @Autowired
    private SaTokenConfig saTokenConfig;

    /**
     * @param loginUser
     * @return
     */
    public String createToken(LoginUser loginUser) {

        log.info("Login user information,{}", loginUser);
        JwtBuilder builder = Jwts.builder()
                .setHeaderParam("type", "JWT")
                .setIssuedAt(new Date())
                .setIssuer(loginUser.getTenantId())
                .setSubject(loginUser.getAccount())
                .setClaims(convertLoginClaims(loginUser))
                .signWith(SignatureAlgorithm.forName(jwtProperties.getAlgorithm()), Const.BASE64_SECURITY.getBytes());

        if (saTokenConfig.getTimeout() > 0) {
            builder.setExpiration(new Date(SystemClock.now() + 1000 * saTokenConfig.getTimeout()));
        }
        return builder.compact();
    }

    /**
     * @param loginUser
     * @return
     */
    private Map<String, Object> convertLoginClaims(LoginUser loginUser) {
        Map<String, Object> claims = Maps.newHashMap();
        claims.put(Const.LOGIN_ID_KEY, loginUser.getLoginId());
        claims.put(Const.LOGIN_COUNTRY, loginUser.getCountry());
        claims.put(Const.LOGIN_LANGUAGE, loginUser.getLanguage());
        claims.put(Const.LOGIN_TERMINAL, loginUser.getTerminal());
        claims.put(Const.LOGIN_TYPE, loginUser.getType());
        return claims;
    }


    public Claims getClaims(String tokenValue) {
        Claims claims = Jwts.parser()
                .setSigningKey(Const.BASE64_SECURITY.getBytes())
                .parseClaimsJws(tokenValue).getBody();
        return claims;
    }

    /**
     * @param tokenValue
     * @return
     */
    public String getUid(String tokenValue) {
        try {
            Object loginId = getClaims(tokenValue).get(Const.LOGIN_ID_KEY);
            if (loginId == null) {
                return null;
            }
            return String.valueOf(loginId);
        } catch (ExpiredJwtException e) {
            return NotLoginException.TOKEN_TIMEOUT;
        } catch (MalformedJwtException e) {
            throw NotLoginException.newInstance(StpUtil.stpLogic.loginKey, NotLoginException.INVALID_TOKEN);
        } catch (Exception e) {
            throw new SaTokenException(e);
        }
    }


    @Bean
    @ConditionalOnMissingBean(StpLogic.class)
    public StpLogic initStpLogic() {

        if (Const.BASE64_SECURITY.equals("79e7c69681b8270162386e6daa53d1dd")) {
            log.warn("----------------------------");
            log.warn("The default password salt is risky, please change [spring.sa-token.jwt.salt] in time");
            log.warn("----------------------------");
        }

        // 修改默认实现
        return new StpLogic("login") {

            // 重写 (随机生成一个tokenValue)
            @Override
            public String createTokenValue(Object loginUser) {
                return createToken((LoginUser) loginUser);
            }

            // 重写 (在当前会话上登录id )
            @Override
            public void setLoginId(Object loginId, SaLoginModel loginModel) {
                // ------ 1、获取相应对象
                SaStorage storage = SaManager.getSaTokenContext().getStorage();
                SaTokenConfig config = getConfig();
                // ------ 2、生成一个token
                String tokenValue = createTokenValue(loginId);
                storage.set(splicingKeyJustCreatedSave(), tokenValue);    // 将token保存到本次request里
                if (config.getIsReadCookie() == true) {    // cookie注入
                    SaManager.getSaTokenContext().getResponse().addCookie(getTokenName(), tokenValue, "/", config.getCookieDomain(), (int) config.getTimeout());
                }
            }

            // 重写 (获取指定token对应的登录id)
            @Override
            public String getLoginIdNotHandle(String tokenValue) {
                try {
                    return getUid(tokenValue);
                } catch (Exception e) {
                    return null;
                }
            }

            // 重写 (当前会话注销登录)
            @Override
            public void logout() {
                // 如果连token都没有，那么无需执行任何操作
                String tokenValue = getTokenValue();
                if (tokenValue == null) {
                    return;
                }
                // 如果打开了cookie模式，把cookie清除掉
                if (getConfig().getIsReadCookie() == true) {
                    SaManager.getSaTokenContext().getResponse().deleteCookie(getTokenName());
                }
                SaManager.getSaTokenListener().doLogout(loginKey, getLoginIdDefaultNull(), tokenValue);
            }

            // 重写 (获取指定key的session)
            @Override
            public SaSession getSessionBySessionId(String sessionId, boolean isCreate) {
                throw new SaTokenException("jwt has not session");
            }

            // 重写 (获取当前登录者的token剩余有效时间 (单位: 秒))
            @Override
            public long getTokenTimeout() {
                // 如果没有token
                String tokenValue = getTokenValue();
                if (tokenValue == null) {
                    return SaTokenDao.NOT_VALUE_EXPIRE;
                }
                // 开始取值
                Claims claims;
                try {
                    claims = getClaims(tokenValue);
                } catch (Exception e) {
                    return SaTokenDao.NOT_VALUE_EXPIRE;
                }
                if (claims == null) {
                    return SaTokenDao.NOT_VALUE_EXPIRE;
                }
                Date expiration = claims.getExpiration();
                if (expiration == null) {
                    return SaTokenDao.NOT_VALUE_EXPIRE;
                }
                return (expiration.getTime() - SystemClock.now()) / 1000;
            }

            // 重写 (返回当前token的登录设备)
			/*@Override
			public String getLoginDevice() {
				return SaTokenConsts.DEFAULT_LOGIN_DEVICE;
			}*/

            // 重写 (获取当前会话的token信息)
            @Override
            public SaTokenInfo getTokenInfo() {
                SaTokenInfo info = new SaTokenInfo();
                info.tokenName = getTokenName();
                info.tokenValue = getTokenValue();
                info.isLogin = isLogin();
                info.loginId = getLoginIdDefaultNull();
                info.loginKey = getLoginKey();
                info.tokenTimeout = getTokenTimeout();
                info.loginDevice = getLoginDevice();
                return info;
            }
        };
    }


    @Autowired(required = false)
    public void initStpUtil(StpLogic stpLogic) {
        StpUtil.stpLogic = stpLogic;
    }

}
