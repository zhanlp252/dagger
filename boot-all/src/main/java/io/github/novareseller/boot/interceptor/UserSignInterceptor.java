package io.github.novareseller.boot.interceptor;

import com.github.lianjiatech.retrofit.spring.boot.interceptor.BasePathMatchInterceptor;
import io.github.novareseller.boot.constant.WebConst;
import io.github.novareseller.security.context.SecurityContext;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author: Bowen huang
 * @date: 2021/05/17
 */
@Component
@Slf4j
public class UserSignInterceptor extends BasePathMatchInterceptor {


    @Override
    protected Response doIntercept(Chain chain) throws IOException {

        Request request = chain.request();

        Request newReq = request.newBuilder()
                .addHeader(WebConst.TOKEN, SecurityContext.getLoginUser().getToken())
                .build();
        return chain.proceed(newReq);
    }
}
