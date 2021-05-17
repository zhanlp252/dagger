package io.github.novareseller.boot.utils;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import io.github.novareseller.tool.utils.Validator;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

/**
 * @author: Bowen huang
 * @date: 2021/04/28
 */
public class HttpUtils {

    private static ImmutableList<String> ipHeaderNameList = ImmutableList.<String>builder()
            .add("x-forwarded-for")
            .add("CF-Connecting-IP")
            .add("Proxy-Client-IP")
            .add("WL-Proxy-Client-IP")
            .add("X-Forwarded-For")
            .add("HTTP_X_REAL_IP")
            .add("HTTP_CLIENT_IP")
            .add("HTTP_X_FORWARDED_FOR")

            .build();


    private static boolean isInvalidClientIp(String clientIp) {
        if (Strings.isNullOrEmpty(clientIp)) {
            return false;
        }

        if ("unknown".equalsIgnoreCase(clientIp)) {
            return false;
        }

        return true;
    }

    /**
     * Get client real ip address
     * First get from http header,if can't, get from remote_addr
     *
     * @param request
     * @return
     */
    public static String getRemoteHost(HttpServletRequest request) {

        /*
           Get real client ip,support CloudFlare,Aliyun,AWS proxy
         */
        String remoteHost = null;
        for (String headerName : ipHeaderNameList) {
            remoteHost = request.getHeader(headerName);
            if (isInvalidClientIp(remoteHost)) {
                break;
            }
        }

        //CAN NOT get from header,get from request
        if (!isInvalidClientIp(remoteHost)) {
            remoteHost = request.getRemoteAddr();
        }

        //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (!Strings.isNullOrEmpty(remoteHost) && remoteHost.length() > 15) { //"***.***.***.***".length() = 15
            if (remoteHost.indexOf(",") > 0) {
                remoteHost = remoteHost.substring(0, remoteHost.indexOf(","));
            }
        }

        return Strings.nullToEmpty(remoteHost);
    }

    public static String getClientRegion(HttpServletRequest request) {
        return Strings.nullToEmpty(request.getHeader("CF-IPCountry"));
    }

    /**
     * Get client ip address from header
     *
     * @param request
     * @return
     */
    public static String getClientAddr(HttpServletRequest request, Set<String> headers) {
        /*
           Get real client ip,support CloudFlare,Aliyun,AWS proxy
         */
        String remoteAddr = null;
        for (String headerName : headers) {
            remoteAddr = request.getHeader(headerName);
            if (isInvalidClientIp(remoteAddr)) {
                break;
            }
        }

        //CAN NOT get from header,get from request
        if (!isInvalidClientIp(remoteAddr)) {
            remoteAddr = request.getRemoteAddr();
        }

        //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割(支持IPv4和IPv6)
        if (!Strings.isNullOrEmpty(remoteAddr)) {
            if (remoteAddr.indexOf(",") > 0) {
                remoteAddr = remoteAddr.substring(0, remoteAddr.indexOf(","));
            }
        }

        return Strings.nullToEmpty(remoteAddr);
    }

    public static String getLastProxyAddr(HttpServletRequest request, List<String> headers) {
        String remoteAddr = "";

        if (!Validator.isNullOrEmpty(headers)) {
            for (String headerName : headers) {
                remoteAddr = request.getHeader(headerName);
                if (isInvalidClientIp(remoteAddr)) {
                    break;
                }
            }
        }

        if (Strings.isNullOrEmpty(remoteAddr)) {
            remoteAddr = Strings.nullToEmpty(request.getRemoteAddr());
            if (!Strings.isNullOrEmpty(remoteAddr)) {
                return remoteAddr;
            }
        }

        if (!Strings.isNullOrEmpty(remoteAddr)) {
            int pos = Strings.nullToEmpty(remoteAddr).lastIndexOf(",");
            if (pos > 0) {
                remoteAddr = remoteAddr.substring(pos);
            }
        }

        return remoteAddr;
    }

}
