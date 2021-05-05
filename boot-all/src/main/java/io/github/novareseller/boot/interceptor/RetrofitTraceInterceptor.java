package io.github.novareseller.boot.interceptor;

import cn.hutool.core.net.NetUtil;
import com.github.lianjiatech.retrofit.spring.boot.interceptor.BaseGlobalInterceptor;
import io.github.novareseller.log.constant.LogConstants;
import io.github.novareseller.log.context.LogContext;
import io.github.novareseller.log.context.SpanIdGenerator;
import io.github.novareseller.tool.utils.Validator;
import lombok.Getter;
import lombok.Setter;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author Bowen Huang
 * @Date 2021/5/5 17:45
 */
@Component
public class RetrofitTraceInterceptor extends BaseGlobalInterceptor {

    @Getter
    @Setter
    @Value("${spring.application.name}")
    private String appName;

    @Override
    protected Response doIntercept(Chain chain) throws IOException {
        String localHostName = NetUtil.getLocalHostName();
        String preIp = LogContext.getPreIp();

        Request request = chain.request();
        Request newReq = request.newBuilder()
                .addHeader(LogConstants.LOG_TRACE_KEY, LogContext.getTraceId())
                .addHeader(LogConstants.PRE_IVK_APP_KEY, appName)
                .addHeader(LogConstants.PRE_IVK_APP_HOST, Validator.isNullOrEmpty(localHostName) ? LogConstants.UNKNOWN : localHostName )
                .addHeader(LogConstants.PRE_IP_KEY, Validator.isNullOrEmpty(preIp) ? LogConstants.UNKNOWN : preIp)
                .addHeader(LogConstants.LOG_SPAN_ID_KEY, SpanIdGenerator.generateNextSpanId())
                .build();
        return chain.proceed(newReq);
    }
}
