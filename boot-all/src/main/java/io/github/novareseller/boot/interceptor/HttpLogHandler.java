package io.github.novareseller.boot.interceptor;


import io.github.novareseller.boot.utils.IpUtils;
import io.github.novareseller.log.constant.LogConstants;
import io.github.novareseller.log.handler.LogLabelBean;
import io.github.novareseller.log.handler.LogHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * http interface
 *
 * @author: Bowen huang
 * @date: 2021/05/04
 */
public class HttpLogHandler extends LogHandler {

    private static volatile HttpLogHandler httpLogHandler;

    public static HttpLogHandler loadInstance() {
        if (httpLogHandler == null) {
            synchronized (HttpLogHandler.class) {
                if (httpLogHandler == null) {
                    httpLogHandler = new HttpLogHandler();
                }
            }
        }
        return httpLogHandler;
    }

    public void preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String traceId = request.getHeader(LogConstants.LOG_TRACE_KEY);
        String spanId = request.getHeader(LogConstants.LOG_SPAN_ID_KEY);
        String preIvkApp = request.getHeader(LogConstants.PRE_IVK_APP_KEY);
        String preIvkHost = request.getHeader(LogConstants.PRE_IVK_APP_HOST);
        String preIp = IpUtils.getRemoteHost(request);

        LogLabelBean labelBean = new LogLabelBean(preIvkApp, preIvkHost, preIp, traceId, spanId);

        processProviderSide(labelBean);
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler) {
        cleanThreadLocal();
    }
}
