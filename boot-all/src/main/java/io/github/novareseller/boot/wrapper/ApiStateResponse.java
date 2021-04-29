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
public class ApiStateResponse extends AbstractPrintable {

    protected int retCode = 0;
    protected int errCode = 0;
    protected String message = "";

    public boolean isSuccessful() {
        return (errCode == 0);
    }
}
