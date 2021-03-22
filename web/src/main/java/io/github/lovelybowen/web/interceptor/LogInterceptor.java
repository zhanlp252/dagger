package io.github.lovelybowen.web.interceptor;

import org.slf4j.MDC;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import io.github.lovelybowen.log.constant.TraceConstant;
import io.github.lovelybowen.tool.text.StringUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Bowen Huang
 * @Date 2021/3/19 23:13
 */
public class LogInterceptor extends HandlerInterceptorAdapter {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String traceId = request.getHeader(TraceConstant.HTTP_HEADER_TRACE_ID);
        if (StringUtil.isNotEmpty(traceId)) {
            MDC.put(TraceConstant.LOG_TRACE_ID, traceId);
        }
        return true;
    }

}
