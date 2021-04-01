package utils;

import com.alibaba.fastjson.JSONObject;
import interceptor.OkHttpTraceIdInterceptor;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * @description:OkHttp工具类
 * @author: verity zhan
 * @time: 2021/3/30 10:12
 */
public class OkHttpUtil {
    private static OkHttpClient client = new OkHttpClient.Builder()
            .addNetworkInterceptor(new OkHttpTraceIdInterceptor())
            .build();

    /**
     * GET请求
     *
     * @param url 请求地址
     * @return
     */
    public static String doGet(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        final Call call = client.newCall(request);
        Response response = null;
        try {
            response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return JSONObject.toJSONString(response.body());
    }
}

