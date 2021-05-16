package io.github.novareseller.boot.interceptor;

import com.sun.deploy.ui.AppInfo;
import io.github.novareseller.boot.constant.WebConst;
import io.github.novareseller.boot.utils.IpUtils;
import io.github.novareseller.boot.utils.ResponseUtils;
import io.github.novareseller.boot.wrapper.ApiResponse;
import io.github.novareseller.boot.wrapper.MultipleReadHttpRequestWrapper;
import io.github.novareseller.tool.utils.Validator;
import io.micrometer.core.instrument.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
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
        String host = IpUtils.getRemoteHost(request);
        String region = HttpTools.getClientRegion(request);
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
        int appId;
        String algorithm = ss[0];
        String signature = ss[3];
        try {
            timestamp = Long.parseLong(ss[1]);
            appId = Integer.parseInt(ss[2]);
        } catch ( Exception ex ) {
            log.error("Invalid authorization format: uri={}, host={}, authorization={}, timestamp={}, appid={}", uri, host, authorization, ss[1], ss[2]);
            responseError(response, "Invalid authorization format");
            return false;
        }

        long start = System.currentTimeMillis();
        AppService service = ApplicationUtils.getBean(AppService.class);
        AppInfo app = service.findAppInfo(appId);
        if ( app == null ) {
            log.error("Invalid appid: uri={}, host={}, authorization={}, appid={}", uri, host, authorization, appId);
            responseError(response, "Invalid appid", System.currentTimeMillis() - start);
            return false;
        }

        long expiration = ConfigTools3.getLong("payment.authorization.expiration.seconds", 300L) * 1000L;
        if ( System.currentTimeMillis() - timestamp > expiration ) {
            logger.error("Authorization expired: uri={}, host={}, authorization={}", uri, host, authorization);
            responseError(response, "Authorization expired", System.currentTimeMillis() - start);
            return false;
        }

        boolean success = PaymentAuthorizationHelper.verifyAuthorization(String.valueOf(appId), app.getSecretKey(), data, authorization);
        if ( !success ) {
            logger.error("Authorization failed: uri={}, host={}, authorization={}", uri, host, authorization);
            responseError(response, "Invalid authorization", System.currentTimeMillis() - start);
            return false;
        }

        request.setAttribute("appid", app.getId());

        String id = String.format("%08x", request.hashCode());
        logger.info("API request[{}]: method={}, uri={}, host={}, region={}, authorization={}, content-length={}", id, method, uri, host, region, authorization, data.length);
        return true;
    }

    @Override
    public void postHandleByHandlerMethod(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletionByHandlerMethod(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if ( request instanceof MultipleReadHttpRequestWrapper) {
            String id = String.format("%08x", request.hashCode());
            log.info("API completed[{}]: status={}, contentType={}", id, response.getStatus(), response.getContentType());
        }
    }


    private void responseError(HttpServletResponse response, String message) throws Exception {
        ApiResponse<?> error = ResponseUtils.error(WebConst.ERR_UNAUTHORIZED_REQUEST, message);
        String json = JsonUtils.json(error);
        byte[] data = json.getBytes(StandardCharsets.UTF_8);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        response.setContentLength(data.length);
        response.getOutputStream().write(data);
    }
}
