package io.github.novareseller.boot.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


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

}
