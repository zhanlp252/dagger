package io.github.novareseller.boot.interceptor;

import io.github.novareseller.boot.utils.IpUtils;
import io.github.novareseller.log.constants.TraceConstant;
import io.github.novareseller.security.context.SecurityContext;
import io.github.novareseller.security.text.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
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
public class LogInterceptor extends HandlerInterceptorAdapter {


    private static final String TOKEN = "Authorization";

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
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authorization = request.getHeader(TOKEN);
        String authorizationHash = StringUtils.isBlank(authorization) ? null : String.format("%08x", authorization.hashCode());
        String uri= request.getRequestURI();
        String method = request.getMethod();
        String host = IpUtils.getRemoteHost(request);
        byte[] data = readRequestData(request);
        String text = data == null ? "" : new String(data, StandardCharsets.UTF_8);
        log.info("http request:token_hash={},method={}, uri={}, host={}, data={}", authorizationHash, method, uri, host, text);


        String traceId = StringUtils.defaultString(request.getHeader(TraceConstant.HTTP_HEADER_TRACE_ID), MDC.get(TraceConstant.LOG_B3_TRACEID));
        if (StringUtil.isNotEmpty(traceId)) {
            MDC.put(TraceConstant.LOG_TRACE_ID, authorizationHash);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        MDC.remove(TraceConstant.LOG_TRACE_ID);
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

    /*private byte[] readResponseData(HttpServletResponse response) throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream();
        try ( OutputStream out = response.getOutputStream() ) {
            IOUtils.copy(in, bout);
            try {
                in.reset();
            } catch ( IOException ex ) {
                log.error("Failed to reset input stream.", ex);
            }
        }
        return bout.toByteArray();
    }*/

}
