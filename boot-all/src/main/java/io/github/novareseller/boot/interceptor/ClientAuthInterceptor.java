package io.github.novareseller.boot.interceptor;

import io.github.novareseller.boot.constant.ErrorCode;
import io.github.novareseller.boot.constant.WebConst;
import io.github.novareseller.boot.utils.HttpUtils;
import io.github.novareseller.boot.utils.JsonUtils;
import io.github.novareseller.boot.utils.ResponseUtils;
import io.github.novareseller.boot.utils.SpringUtils;
import io.github.novareseller.boot.wrapper.ApiResponse;
import io.github.novareseller.boot.wrapper.MultipleReadHttpRequestWrapper;
import io.github.novareseller.security.annotation.VerifyClient;
import io.github.novareseller.security.helper.ClientAuthorizationHelper;
import io.github.novareseller.security.model.ClientInfo;
import io.github.novareseller.security.spi.ClientInfoHolder;
import io.github.novareseller.tool.utils.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;

/**
 * @author Bowen Huang
 * @Date 2021/5/17 0:02
 */
@Slf4j
public class ClientAuthInterceptor extends AbsWebHandlerMethodInterceptor {


    @Override
    public boolean preHandleByHandlerMethod(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (!hasVerifyClientAnnotation((HandlerMethod) handler)) {
            return true;
        }

        String host = HttpUtils.getRemoteHost(request);
        String region = HttpUtils.getClientRegion(request);
        String uri = request.getRequestURI();
        String method = request.getMethod();

        byte[] data = readRequestData(request);
        String authorization = request.getHeader(WebConst.HEADER_CLIENT_AUTHORIZATION);
        if ( Validator.isNullOrEmpty(authorization) ) {
            log.error("Authorization required: uri={}, host={}", uri, host);
            responseError(response, "Authorization required.");
            return false;
        }

        String[] ss = authorization.split(";");
        if ( ss.length != 4 ) {
            log.error("Invalid authorization: uri={}, host={}, authorization={}", uri, host, authorization);
            responseError(response, "Invalid authorization format");
            return false;
        }

        long timestamp;
        int clientId;
        try {
            timestamp = Long.parseLong(ss[1]);
            clientId = Integer.parseInt(ss[2]);
        } catch ( Exception ex ) {
            log.error("Invalid authorization format: uri={}, host={}, authorization={}, timestamp={}, appId={}", uri, host, authorization, ss[1], ss[2]);
            responseError(response, "Invalid authorization format");
            return false;
        }

        ClientInfoHolder holder = SpringUtils.getBean(ClientInfoHolder.class);
        ClientInfo clientInfo = holder.findClientInfo(clientId);
        if ( clientInfo == null ) {
            log.error("Invalid appid: uri={}, host={}, authorization={}, appId={}", uri, host, authorization, clientId);
            responseError(response, "Invalid appid");
            return false;
        }

        if ( System.currentTimeMillis() - timestamp > 300 * 1000L ) {
            log.error("Authorization expired: uri={}, host={}, authorization={}", uri, host, authorization);
            responseError(response, "Authorization expired");
            return false;
        }

        boolean success = ClientAuthorizationHelper.verifyAuthorization(String.valueOf(clientId), clientInfo.getSecretKey(), data, authorization);
        if ( !success ) {
            log.error("Authorization failed: uri={}, host={}, authorization={}", uri, host, authorization);
            responseError(response, "Invalid authorization");
            return false;
        }

        request.setAttribute("appid", clientInfo.getId());

        String id = String.format("%08x", request.hashCode());
        log.info("API request[{}]: method={}, uri={}, host={}, region={}, authorization={}, content-length={}", id, method, uri, host, region, authorization, data.length);
        return true;
    }

    @Override
    public void postHandleByHandlerMethod(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletionByHandlerMethod(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (hasVerifyClientAnnotation((HandlerMethod)handler)) {
            if ( request instanceof MultipleReadHttpRequestWrapper) {
                String id = String.format("%08x", request.hashCode());
                log.info("API completed[{}]: status={}, contentType={}", id, response.getStatus(), response.getContentType());
            }
        }
    }

    private boolean hasVerifyClientAnnotation(HandlerMethod handlerMethod) {
        // 配置该注解，说明不进行服务拦截
        VerifyClient annotation = handlerMethod.getBeanType().getAnnotation(VerifyClient.class);
        if (annotation == null) {
            annotation = handlerMethod.getMethodAnnotation(VerifyClient.class);
        }
        if (annotation != null) {
            return true;
        }
        return false;
    }

    private void responseError(HttpServletResponse response, String message) throws Exception {
        ApiResponse<?> error = ResponseUtils.error(ErrorCode.ERR_UNAUTHORIZED_REQUEST, message);
        String json = JsonUtils.json(error);
        byte[] data = json.getBytes(StandardCharsets.UTF_8);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        response.setContentLength(data.length);
        response.getOutputStream().write(data);
    }
}
