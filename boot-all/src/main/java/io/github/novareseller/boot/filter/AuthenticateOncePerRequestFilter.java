package io.github.novareseller.boot.filter;

import io.github.novareseller.boot.constant.ErrorCode;
import io.github.novareseller.boot.constant.WebConst;
import io.github.novareseller.boot.exception.ApiException;
import io.github.novareseller.boot.interceptor.HttpLogHandler;
import io.github.novareseller.boot.properties.WebProperties;
import io.github.novareseller.boot.utils.IpUtils;
import io.github.novareseller.boot.utils.SpringUtils;
import io.github.novareseller.security.config.JwtRegisterBean;
import io.github.novareseller.security.context.LoginUser;
import io.github.novareseller.security.context.SecurityContext;
import io.github.novareseller.tool.utils.Validator;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * @author: Bowen huang
 * @date: 2021/04/29
 */
@Slf4j
public class AuthenticateOncePerRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtRegisterBean jwtRegisterBean;

    private static final AntPathMatcher MATCHER = new AntPathMatcher();

    private List<String> excludePathPatterns;

    public AuthenticateOncePerRequestFilter(List<String> excludePathPatterns) {
        this.excludePathPatterns = excludePathPatterns;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        if (!isExcludePath(requestURI)) {

            String authorization = request.getHeader(WebConst.TOKEN);
            if ( null == authorization || "null".equals(authorization)||"".equals(authorization)) {
                log.warn("login token is required. url={}", request.getRequestURI());
                throw new ApiException(ErrorCode.REQUIRED_TOKEN, "Login token is required");
            }

            try {
                //parse jwt
                Claims claims = jwtRegisterBean.parseTokenClaims(authorization);
                LoginUser loginUser = LoginUser.claims2LoginUser(claims);
                String ip = IpUtils.getRemoteHost(request);
                loginUser.setClientIp(ip);
                loginUser.setLanguage(getLanguage(request));
                //set context data
                SecurityContext.setLoginUser(loginUser);
            } catch (ExpiredJwtException ex) {

                log.error("token expired. login token={},url={},method={}", authorization, request.getRequestURI(), request.getMethod(), ex);
                throw new ApiException(ErrorCode.EXPIRED_TOKEN, "token has expired");
            } catch (UnsupportedJwtException |
                    MalformedJwtException |
                    SignatureException |
                    IllegalArgumentException ex) {

                log.error("token decode error. login token={},url={},method={}", authorization, request.getRequestURI(), request.getMethod(), ex);
                throw new ApiException(ErrorCode.ERROR_TOKEN, "token error");
            }
        }
        filterChain.doFilter(request, response);
    }



    protected String getLanguage(HttpServletRequest request) {
        String languageParams = request.getParameter(WebConst.LANGUAGE);
        if (Objects.nonNull(languageParams) && !"".equals(languageParams)) {
            return languageParams;
        }
        Object language = request.getSession().getAttribute(WebConst.LANGUAGE);
        if (Objects.nonNull(language) && !"".equals(language)) {
            return String.valueOf(language);
        }
        String languageInCookie = getCookieAsString(request, WebConst.LANGUAGE);
        if (Objects.nonNull(languageInCookie)) {
            return languageInCookie;
        }
        return "en";
    }

    private String getCookieAsString(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (Objects.nonNull(cookies)) {
            for (int i = 0; i < cookies.length; i++) {
                if (cookies[i].getName().equals(cookieName)) {
                    return cookies[i].getValue();
                }
            }
        }
        return null;
    }

    private boolean isExcludePath(String uri) {
        if (!Validator.isNullOrEmpty(excludePathPatterns)) {
            for (String excludePathPattern : excludePathPatterns) {
                if (MATCHER.match(excludePathPattern, uri)) {
                    return true;
                }
            }
        }
        return false;
    }

}
