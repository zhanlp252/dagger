package io.github.novareseller.boot.interceptor;

import io.github.novareseller.boot.constant.WebConst;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;


/**
 * @author: Bowen huang
 * @date: 2021/05/04
 */
@Slf4j
public abstract class AbsWebHandlerMethodInterceptor implements HandlerInterceptor {

    public abstract boolean preHandleByHandlerMethod(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception;

    public abstract void postHandleByHandlerMethod(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception;

    public abstract void afterCompletionByHandlerMethod(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            return preHandleByHandlerMethod(request, response, handler);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (handler instanceof HandlerMethod) {
            postHandleByHandlerMethod(request, response, handler, modelAndView);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (handler instanceof HandlerMethod) {
            afterCompletionByHandlerMethod(request, response, handler, ex);
        }
    }


    protected byte[] readRequestData(HttpServletRequest request) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try ( InputStream in = request.getInputStream() ) {
            IOUtils.copy(in, bout);
            try {
                in.reset();
            } catch ( IOException ex ) {
                log.error("Failed to reset input stream.", ex);
            }
        }
        return bout.toByteArray();
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
