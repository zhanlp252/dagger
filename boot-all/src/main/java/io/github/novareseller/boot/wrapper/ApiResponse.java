package io.github.novareseller.boot.wrapper;

import lombok.Getter;
import lombok.Setter;

/**
 * @author: Bowen huang
 * @date: 2021/04/29
 */
@Getter
@Setter
public class ApiResponse<T> extends ApiStateResponse {

    protected T data;

}
