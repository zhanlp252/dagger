package io.github.novareseller.boot.interceptor;

import io.github.novareseller.boot.constants.TraceConstant;
import io.github.novareseller.security.text.StringUtil;
import org.slf4j.MDC;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**登陆拦截器，为所有请求添加一个traceId
 * @author Bowen Huang
 * @Date 2021/3/19 23:13
 */
public class LogInterceptor extends HandlerInterceptorAdapter {


    /**
     *
     *
     * @description: 先从request header中获取traceId
     * 从request header中获取不到traceId则说明不是第三方调用，直接生成一个新的traceId
     * 将生成的traceId存入MDC中
     * @return:
     * @author: verity zhan
     * @time: 2021/3/30 10:14
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String traceId = request.getHeader(TraceConstant.HTTP_HEADER_TRACE_ID);
        if (StringUtil.isNotEmpty(traceId)) {
            MDC.put(TraceConstant.LOG_TRACE_ID, traceId);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
            throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        //调用结束后删除
        MDC.remove(TraceConstant.LOG_TRACE_ID);
    }

}
