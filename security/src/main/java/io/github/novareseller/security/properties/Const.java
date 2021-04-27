package io.github.novareseller.security.properties;

/**
 * @author: Bowen huang
 * @date: 2021/04/26
 */
public class Const {

    /**
     * ################################
     * default secret
     * ################################
     */
    public static final String JWT_SECURITY = "3a34e26a61c034d5ec8245d8677df7fc";


    /**
     * ################################
     * claims constant
     * ################################
     */
    public static final String LOGIN_ID = "uid";
    public static final String LOGIN_COUNTRY = "country";
    public static final String LOGIN_LANGUAGE = "language";
    public static final String LOGIN_TERMINAL = "terminal";
    public static final String LOGIN_TYPE = "type";


    /**
     * ################################
     * error code
     * ################################
     */
    public static final int JWT_ERRCODE_NULL = 4000;
    public static final int JWT_ERRCODE_EXPIRE = 4001;
    public static final int JWT_ERRCODE_FAIL = 4002;

}
