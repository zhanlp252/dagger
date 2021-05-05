package io.github.novareseller.log.context;


import com.alibaba.ttl.TransmittableThreadLocal;

/**
 * 日志切面的上下文，用于管理当前线程以及子线程的的增强内容
 *
 * @author bowen
 * @date 2021/05/05
 */
public class AspectLogContext {

    private static TransmittableThreadLocal<String> logValueTL = new TransmittableThreadLocal<>();

    public static void putLogValue(String logValue) {
        logValueTL.set(logValue);
    }

    public static String getLogValue() {
        String result = logValueTL.get();
        return result;
    }

    public static void remove() {
        logValueTL.remove();
    }

}
