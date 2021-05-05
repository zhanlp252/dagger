package io.github.novareseller.log.context;


import com.alibaba.ttl.TransmittableThreadLocal;

/**
 * Log上下文
 *
 * @author bowen
 * @date 2021/05/05
 */
public class LogContext {

    private static boolean enableInvokeTimePrint = false;

    private static boolean hasLogMDC;

    private static final TransmittableThreadLocal<String> traceIdTL = new TransmittableThreadLocal<>();

    private static final TransmittableThreadLocal<String> preIvkAppTL = new TransmittableThreadLocal<>();

    private static final TransmittableThreadLocal<String> preIvkHostTL = new TransmittableThreadLocal<>();

    private static final TransmittableThreadLocal<String> preIpTL = new TransmittableThreadLocal<>();

    public static void putTraceId(String traceId) {
        traceIdTL.set(traceId);
    }

    public static String getTraceId() {
        return traceIdTL.get();
    }

    public static void removeTraceId() {
        traceIdTL.remove();
    }

    public static void putSpanId(String spanId) {
        SpanIdGenerator.putSpanId(spanId);
    }

    public static String getSpanId() {
        return SpanIdGenerator.getSpanId();
    }

    public static void removeSpanId() {
        SpanIdGenerator.removeSpanId();
    }

    public static String getPreIvkApp() {
        return preIvkAppTL.get();
    }

    public static void putPreIvkApp(String preIvkApp) {
        preIvkAppTL.set(preIvkApp);
    }

    public static void removePreIvkApp() {
        preIvkAppTL.remove();
    }

    public static String getPreIvkHost(){
        return preIvkHostTL.get();
    }

    public static void putPreIvkHost(String preIvkHost){
        preIvkHostTL.set(preIvkHost);
    }

    public static void removePreIvkHost(){
        preIvkHostTL.remove();
    }

    public static String getPreIp() {
        return preIpTL.get();
    }

    public static void putPreIp(String preIp) {
        preIpTL.set(preIp);
    }

    public static void removePreIp() {
        preIpTL.remove();
    }

    public static boolean hasLogMDC() {
        return hasLogMDC;
    }

    public static void setHasLogMDC(boolean hasLogMDC) {
        LogContext.hasLogMDC = hasLogMDC;
    }

    public static boolean enableInvokeTimePrint() {
        return enableInvokeTimePrint;
    }

    public static void setEnableInvokeTimePrint(boolean enableInvokeTimePrint) {
        LogContext.enableInvokeTimePrint = enableInvokeTimePrint;
    }
}
