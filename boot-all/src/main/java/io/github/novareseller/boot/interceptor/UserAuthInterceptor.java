package io.github.novareseller.boot.interceptor;

import io.github.novareseller.boot.constant.ErrorCode;
import io.github.novareseller.boot.constant.WebConst;
import io.github.novareseller.boot.exception.ApiException;
import io.github.novareseller.boot.utils.HttpUtils;
import io.github.novareseller.security.annotation.VerifyUser;
import io.github.novareseller.security.config.JwtRegisterBean;
import io.github.novareseller.security.context.LoginUser;
import io.github.novareseller.security.context.SecurityContext;
import io.github.novareseller.tool.utils.Validator;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 登陆拦截器，为所有请求添加一个traceId
 * @author Bowen Huang
 * @Date 2021/3/19 23:13
 */
@Slf4j
public class UserAuthInterceptor extends AbsWebHandlerMethodInterceptor {


    @Autowired(required = false)
    private JwtRegisterBean jwtRegisterBean;

    private static final AntPathMatcher MATCHER = new AntPathMatcher();

    private List<String> excludePathPatterns;

    public UserAuthInterceptor(List<String> excludePathPatterns) {
        this.excludePathPatterns = excludePathPatterns;
    }


    @Override
    public boolean preHandleByHandlerMethod(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (!hasVerifyUserAnnotation((HandlerMethod) handler)) {
            return true;
        }

        String authorization = request.getHeader(WebConst.TOKEN);
        String authorizationHash = StringUtils.isBlank(authorization) ? null : String.format("%08x", authorization.hashCode());
        String uri= request.getRequestURI();
        String method = request.getMethod();
        String ip = HttpUtils.getRemoteHost(request);
        String language = getLanguage(request);


        if (!isExcludePath(uri)) {
            log.info("http request:token hash={}, user={}", authorizationHash, "Anonymous user ignoring token");
            return true;
        }

        authentication(authorization, uri, method, ip, language);

        log.info("http request:token hash={}, user={}, method={}, uri={}, ip={}, language={}",
                authorizationHash, SecurityContext.getLoginUser(), method, uri, ip, language);

        return true;
    }


    @Override
    public void postHandleByHandlerMethod(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }


    @Override
    public void afterCompletionByHandlerMethod(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //clear thread security context user info
        SecurityContext.clear();
    }


    private void authentication(String authorization, String uri, String method, String ip, String language) throws Exception{

        if ( null == authorization || "null".equals(authorization)||"".equals(authorization)) {
            log.warn("login token is required. uri={}, method={}", uri, method);
            throw new ApiException(ErrorCode.REQUIRED_TOKEN, "Login token is required");
        }

        try {
            //parse jwt
            Claims claims = jwtRegisterBean.parseTokenClaims(authorization);
            LoginUser loginUser = LoginUser.claims2LoginUser(claims);
            loginUser.setClientIp(ip);
            loginUser.setLanguage(language);
            //set context data
            SecurityContext.setLoginUser(loginUser);
        } catch (ExpiredJwtException ex) {

            log.error("token expired. login token={},uri={},method={}", authorization, uri, method, ex);
            throw new ApiException(ErrorCode.EXPIRED_TOKEN, "token has expired");
        } catch (UnsupportedJwtException |
                MalformedJwtException |
                SignatureException |
                IllegalArgumentException ex) {

            log.error("token decode error. login token={},uri={},method={}", authorization, uri, method, ex);
            throw new ApiException(ErrorCode.ERROR_TOKEN, "token error");
        }

    }

    private boolean hasVerifyUserAnnotation(HandlerMethod handlerMethod) {
        // 配置该注解，说明不进行服务拦截
        VerifyUser annotation = handlerMethod.getBeanType().getAnnotation(VerifyUser.class);
        if (annotation == null) {
            annotation = handlerMethod.getMethodAnnotation(VerifyUser.class);
        }
        if (annotation != null) {
            return true;
        }
        return false;
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
