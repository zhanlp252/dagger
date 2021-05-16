package io.github.novareseller.boot.interceptor;

import com.alibaba.ttl.TransmittableThreadLocal;
import io.github.novareseller.boot.constant.ErrorCode;
import io.github.novareseller.boot.constant.WebConst;
import io.github.novareseller.boot.exception.ApiException;
import io.github.novareseller.boot.utils.IpUtils;
import io.github.novareseller.log.constant.LogConstants;
import io.github.novareseller.log.context.LogContext;
import io.github.novareseller.security.annotation.IgnoreClientToken;
import io.github.novareseller.security.annotation.IgnoreUserToken;
import io.github.novareseller.security.config.JwtRegisterBean;
import io.github.novareseller.security.context.LoginUser;
import io.github.novareseller.security.context.SecurityContext;
import io.github.novareseller.tool.utils.Validator;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

/**
 * 登陆拦截器，为所有请求添加一个traceId
 * @author Bowen Huang
 * @Date 2021/3/19 23:13
 */
@Slf4j
public class UserAuthInterceptor extends AbsWebHandlerMethodInterceptor {


    @Autowired
    private JwtRegisterBean jwtRegisterBean;

    private final TransmittableThreadLocal<StopWatch> invokeTimeTL = new TransmittableThreadLocal<>();

    private static final AntPathMatcher MATCHER = new AntPathMatcher();

    private List<String> excludePathPatterns;

    public UserAuthInterceptor(List<String> excludePathPatterns) {
        this.excludePathPatterns = excludePathPatterns;
    }


    @Override
    public boolean preHandleByHandlerMethod(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String authorization = request.getHeader(WebConst.TOKEN);
        String authorizationHash = StringUtils.isBlank(authorization) ? null : String.format("%08x", authorization.hashCode());
        String uri= request.getRequestURI();
        String method = request.getMethod();
        String ip = IpUtils.getRemoteHost(request);
        String language = getLanguage(request);
        byte[] data = readRequestData(request);
        String text = data == null ? "" : new String(data, StandardCharsets.UTF_8);

        HttpLogHandler.loadInstance().preHandle(request, response, null);
        //把traceId放入response的header，为了方便有些人有这样的需求，从前端拿整条链路的traceId
        response.addHeader(LogConstants.LOG_TRACE_KEY, LogContext.getTraceId());

        if (LogContext.enableInvokeTimePrint()) {
            StopWatch stopWatch = new StopWatch();
            invokeTimeTL.set(stopWatch);
            stopWatch.start();
        }

        if (ignoreUserToken( (HandlerMethod) handler ) || !isExcludePath(uri)) {
            log.info("http request:token hash={}, user={}, method={}, uri={}, ip={}, language={}, data={}",
                    authorizationHash, "Anonymous user ignoring token", method, uri, ip, language, text);
            return true;
        }

        authentication(authorization, uri, method, ip, language);

        log.info("http request:token hash={}, user={}, method={}, uri={}, ip={}, language={}, data={}",
                authorizationHash, SecurityContext.getLoginUser(), method, uri, ip, language, text);

        return true;
    }


    @Override
    public void postHandleByHandlerMethod(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }


    @Override
    public void afterCompletionByHandlerMethod(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (LogContext.enableInvokeTimePrint()) {
            StopWatch stopWatch = invokeTimeTL.get();
            stopWatch.stop();
            log.info("End the call of URI[{}]. Time-consuming: {} milliseconds", request.getRequestURI(), stopWatch.getTime());
            invokeTimeTL.remove();
        }

        //clear thread log context var
        HttpLogHandler.loadInstance().afterCompletion(request, response, handler);

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




    private boolean ignoreUserToken(HandlerMethod handlerMethod) {
        // 配置该注解，说明不进行服务拦截
        IgnoreUserToken annotation = handlerMethod.getBeanType().getAnnotation(IgnoreUserToken.class);
        if (annotation == null) {
            annotation = handlerMethod.getMethodAnnotation(IgnoreUserToken.class);
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

}
