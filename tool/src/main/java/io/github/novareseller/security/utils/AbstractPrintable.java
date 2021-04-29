package io.github.novareseller.security.utils;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

/**
 * @author: Bowen huang
 * @date: 2021/04/29
 */
public abstract class AbstractPrintable implements Serializable, Cloneable {
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
