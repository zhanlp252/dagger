package io.github.novareseller.boot.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.github.novareseller.boot.wrapper.ApiPageResponse;
import io.github.novareseller.boot.wrapper.ApiResponse;

import java.util.List;

/**
 * @author: Bowen huang
 * @date: 2021/04/29
 */
public class ResponseUtils {


    public static <T> ApiResponse<T> ok(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setData(data);
        return response;
    }

    public static <T> ApiResponse<T> error(int errCode, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setErrCode(errCode);
        response.setMessage(message);
        response.setRetCode(1000);
        return response;
    }

    public static <T> ApiResponse<T> error(int errCode, T data, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setErrCode(errCode);
        response.setMessage(message);
        response.setRetCode(1000);
        response.setData(data);
        return response;
    }

    public static <T> ApiPageResponse<List<T>> page(IPage<T> iPage) {
        ApiPageResponse<List<T>> response = new ApiPageResponse<>();
        response.setRetCode(1000);
        response.setData(iPage.getRecords());
        response.setCurrent(iPage.getCurrent());
        response.setSize(iPage.getSize());
        response.setTotal(iPage.getTotal());
        return response;
    }

}
