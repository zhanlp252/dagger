package io.github.novareseller.boot.interceptor;

import io.github.novareseller.boot.utils.IpUtils;
import io.github.novareseller.log.constants.TraceConstant;
import io.github.novareseller.security.config.JwtRegisterBean;
import io.github.novareseller.security.context.LoginUser;
import io.github.novareseller.security.text.StringUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**登陆拦截器，为所有请求添加一个traceId
 * @author Bowen Huang
 * @Date 2021/3/19 23:13
 */
@Slf4j
public class TraceInterceptor extends HandlerInterceptorAdapter {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String traceId = StringUtils.defaultString(request.getHeader(TraceConstant.HTTP_HEADER_TRACE_ID), MDC.get(TraceConstant.LOG_B3_TRACEID));
        if (StringUtil.isNotEmpty(traceId)) {
            MDC.put(TraceConstant.LOG_TRACE_ID, traceId);
        }
        return true;
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        MDC.remove(TraceConstant.LOG_TRACE_ID);
    }

}
