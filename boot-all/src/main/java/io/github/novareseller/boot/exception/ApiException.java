package io.github.novareseller.boot.exception;

import com.google.common.base.MoreObjects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bowen
 * @date 2021/04/29
 */
public class ApiException extends RuntimeException {

    private static final long serialVersionUID = 7918504628536752765L;

    private static final Logger log = LoggerFactory.getLogger(ApiException.class);
    private int retCode = 1000;
    private int errCode;


    public static <E extends Exception> void lambdaThrowException(Exception e) throws E {
        throw (E) e;
    }


    public ApiException(int errCode, String msg) {
        super(msg);
        this.errCode = errCode;
    }


    public int getRetCode() {
        return retCode;
    }

    public int getErrCode() {
        return errCode;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("retCode", retCode)
                .add("errCode", errCode)
                .add("msg", getMessage())
                .toString();
    }
}
