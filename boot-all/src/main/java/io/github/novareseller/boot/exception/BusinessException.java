package io.github.novareseller.boot.exception;

import com.google.common.base.MoreObjects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BusinessException extends Exception {

    private static final long serialVersionUID = 7918504628536752776L;

    private static final Logger log = LoggerFactory.getLogger(BusinessException.class);
    private int retCode;
    private int errCode;

    @SuppressWarnings("unchecked")
    public static <E extends Exception> void lambdaThrowException(Exception e) throws E {
        throw (E) e;
    }

    public BusinessException(int retCode, int errCode, String msg) {
        super(msg);
        this.retCode = retCode;
        this.errCode = errCode;
    }
    public BusinessException(int errCode, String msg) {
        super(msg);
        this.errCode = errCode;
    }

    public BusinessException(int retCode, int errCode) {
        this.retCode = retCode;
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

    public String getMutiLanguageMessage(String language) {
        /*return ConfigTools3.getString("tve.member.bill.error."+errCode + "."+language ,
                ConfigTools3.getString("tve.member.bill.error."+errCode + ".en"));*/
        return null;
    }

    /**
     *根据语言获取配置信息，如果是英语，则直接取英语，取不到则默认
     * 如果是非英语，娶不到则取英语，再取不到，则默认
     */
    public static  String getMutiLanguageMessage(String language,String module,int errorCode) {
       return getMutiLanguageMessage(language,module,errorCode,null);
    }

    /**
     *根据语言获取配置信息，如果是英语，则直接取英语，取不到则默认
     * 如果是非英语，娶不到则取英语，再取不到，则默认
     */
    public static  String getMutiLanguageMessage(String language,String module,int errorCode,String defaultMessage) {
       /* language = language.toLowerCase();
        String message = ConfigTools3.getString(String.format("tve.member.%s.error.%d.%s",module,errorCode,language));
        if(!language.equals("en") && StringUtils.isEmpty(message)){
            message = ConfigTools3.getString(String.format("tve.member.%s.error.%d.en",module,errorCode));
        }
        return StringUtils.isEmpty(message)?defaultMessage:message;*/
        return null;
    }
}
