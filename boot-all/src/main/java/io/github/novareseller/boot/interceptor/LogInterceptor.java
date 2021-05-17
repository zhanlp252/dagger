package io.github.novareseller.boot.interceptor;

import com.alibaba.ttl.TransmittableThreadLocal;
import io.github.novareseller.boot.utils.HttpUtils;
import io.github.novareseller.log.constant.LogConstants;
import io.github.novareseller.log.context.LogContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;

/**
 * 登陆拦截器，为所有请求添加一个traceId
 * @author Bowen Huang
 * @Date 2021/3/19 23:13
 */
@Slf4j
public class LogInterceptor extends AbsWebHandlerMethodInterceptor {


    private final TransmittableThreadLocal<StopWatch> invokeTimeTL = new TransmittableThreadLocal<>();


    @Override
    public boolean preHandleByHandlerMethod(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String uri= request.getRequestURI();
        String method = request.getMethod();
        String ip = HttpUtils.getRemoteHost(request);
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

        log.info("http request:traceId={}, method={}, uri={}, ip={}, language={}, data={}",
                LogContext.getTraceId(), method, uri, ip, language, text);

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
    }



}
