package io.github.novareseller.boot.interceptor;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.github.lianjiatech.retrofit.spring.boot.interceptor.BasePathMatchInterceptor;
import io.github.novareseller.boot.constant.WebConst;
import io.github.novareseller.security.helper.ClientAuthorizationHelper;
import io.github.novareseller.tool.text.StringUtil;
import io.github.novareseller.tool.utils.SystemClock;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author: Bowen huang
 * @date: 2021/05/17
 */
@Component
@Slf4j
public class ClientSignInterceptor extends BasePathMatchInterceptor {

    /**
     * 客户端id
     */
    @Setter
    private String clientId;

    /**
     * 密钥
     */
    @Setter
    private String clientSecret;

    @Override
    protected Response doIntercept(Chain chain) throws IOException {

        Request request = chain.request();
        RequestBody requestBody = request.body();

        String authorization = StringUtil.EMPTY;
        long ts = SystemClock.now();
        try {
            if ( requestBody != null ) {
                Buffer buffer = new Buffer();
                requestBody.writeTo(buffer);
                byte[] data = buffer.readByteArray();
                authorization = ClientAuthorizationHelper.createAuthorization(ts, clientId, clientSecret, data);
            } else {
                authorization = ClientAuthorizationHelper.createAuthorization(ts, clientId, clientSecret, new byte[0]);
            }
        } catch (Exception e) {
            log.error("Client Sign Exception, ex={}", ExceptionUtil.stacktraceToString(e));
        }

        Request newReq = request.newBuilder()
                .addHeader(WebConst.HEADER_CLIENT_AUTHORIZATION, authorization)
                .build();
        return chain.proceed(newReq);
    }
}
