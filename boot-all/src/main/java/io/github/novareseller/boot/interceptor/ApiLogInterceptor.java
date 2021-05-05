package io.github.novareseller.boot.interceptor;

import com.alibaba.ttl.TransmittableThreadLocal;
import io.github.novareseller.boot.constant.WebConst;
import io.github.novareseller.boot.utils.IpUtils;
import io.github.novareseller.log.constant.LogConstants;
import io.github.novareseller.log.context.LogContext;
import io.github.novareseller.security.context.SecurityContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.ModelAndView;
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
public class ApiLogInterceptor extends AbsWebHandlerMethodInterceptor {


    private final TransmittableThreadLocal<StopWatch> invokeTimeTL = new TransmittableThreadLocal<>();


    @Override
    public boolean preHandleByHandlerMethod(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String authorization = request.getHeader(WebConst.TOKEN);
        String authorizationHash = StringUtils.isBlank(authorization) ? null : String.format("%08x", authorization.hashCode());
        String uri= request.getRequestURI();
        String method = request.getMethod();
        String host = IpUtils.getRemoteHost(request);
        byte[] data = readRequestData(request);
        String text = data == null ? "" : new String(data, StandardCharsets.UTF_8);

        HttpLogHandler.loadInstance().preHandle(request, response, null);
        //把traceId放入response的header，为了方便有些人有这样的需求，从前端拿整条链路的traceId
        response.addHeader(LogConstants.LOG_TRACE_KEY, LogContext.getTraceId());

        log.info("http request:token hash={}, user={}, method={}, uri={}, host={}, data={}",
                authorizationHash, SecurityContext.getLoginUser(), method, uri, host, text);

        if (LogContext.enableInvokeTimePrint()) {
            StopWatch stopWatch = new StopWatch();
            invokeTimeTL.set(stopWatch);
            stopWatch.start();
        }

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
            log.info("End the call of URL[{}]. Time-consuming: {} milliseconds", request.getRequestURI(), stopWatch.getTime());
            invokeTimeTL.remove();
        }

        //clear thread log context var
        HttpLogHandler.loadInstance().afterCompletion(request, response, handler);

        //clear thread security context user info
        SecurityContext.clear();
    }



    private byte[] readRequestData(HttpServletRequest request) throws IOException {
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
