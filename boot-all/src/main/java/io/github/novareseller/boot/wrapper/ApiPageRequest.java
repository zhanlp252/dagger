package io.github.novareseller.boot.wrapper;

import io.github.novareseller.tool.utils.AbstractPrintable;
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
     *  current page page number
     */
    private long page = 1;

    /**
     * every page size
     */
    private long limit = 10;






}
