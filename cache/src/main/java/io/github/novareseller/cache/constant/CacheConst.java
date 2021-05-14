package io.github.novareseller.cache.constant;

/**
 * @author: Bowen huang
 * @date: 2021/05/14
 */
public class CacheConst {

    public static final String SENTINEL_MODE ="sentinel";
    public static final String CLUSTER_MODE ="cluster";
    public static final String SHARDED_MODE ="sharded";
    public static final String SINGLE_MODE = "single";

    public static final String COMMA = ",";

    public static final int MAX_ATTEMPTS = 3;

    public static final int CONNECT_TIMEOUT = 5000;
}
