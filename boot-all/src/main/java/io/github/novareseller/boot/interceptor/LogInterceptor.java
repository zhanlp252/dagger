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
public class LogInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private JwtRegisterBean jwtRegisterBean;

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
        addLog(request);
        String address = IpUtils.getRemoteHost(request);
        log.info("Request: url={}, address={}", request.getRequestURI(), address);

        String authorization = request.getHeader(TOKEN);
        log.info("user authorization={}", authorization);
        if ( null == authorization || "null".equals(authorization)||"".equals(authorization)) {
            log.warn("Login token is required. url={}", request.getRequestURI());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        try {
            //parse jwt
            Claims claims = jwtRegisterBean.parseTokenClaims(authorization);
            log.info("LoginUser={}", LoginUser.claims2LoginUser(claims));
        } catch(Exception ex){
            log.error("token decode error. loginToken={},url={},method={}",authorization, request.getRequestURI(), request.getMethod(),ex);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        return true;
    }


    private void addLog(HttpServletRequest request) throws Exception {
        String uri= request.getRequestURI();
        String method = request.getMethod();
        String host = IpUtils.getRemoteHost(request);
        byte[] data = readRequestData(request);
        String text = data == null ? "" : new String(data, StandardCharsets.UTF_8);
        log.info("http request: method={}, uri={}, host={}, data={}", method, uri, host, text);
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
