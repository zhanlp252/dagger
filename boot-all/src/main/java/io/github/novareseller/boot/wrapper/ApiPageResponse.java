package io.github.novareseller.boot.wrapper;

import lombok.Getter;
import lombok.Setter;

/**
 * @author: Bowen huang
 * @date: 2021/04/29
 */
@Getter
@Setter
public class ApiPageResponse<T> extends ApiResponse<T> {

    /**
     * total data size
     */
    private long total = 0;

    /**
     * every page size
     */
    private long size = 10;

    /**
     * current page number
     */
    private long current = 1;

}
