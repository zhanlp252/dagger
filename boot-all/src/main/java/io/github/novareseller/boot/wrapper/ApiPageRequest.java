package io.github.novareseller.boot.wrapper;

import io.github.novareseller.security.utils.AbstractPrintable;
import lombok.Getter;
import lombok.Setter;

/**
 * @author: Bowen huang
 * @date: 2021/04/29
 */
@Getter
@Setter
public class ApiPageRequest extends AbstractPrintable {

    /**
     * every page size
     */
    private long size = 10;

    /**
     * current page number
     */
    private long current = 1;




}
