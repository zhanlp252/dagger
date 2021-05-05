package io.github.novareseller.log.context;

/**
 * Log的日志标签生成器
 *
 * @author bowen
 * @date 2021/05/05
 */
public class LogLabelGenerator {

    public static String labelPattern = "<$spanId><$traceId>";

    public static String generateLogLabel(String preApp, String preHost, String preIp, String traceId, String spanId){
        return labelPattern.replace("$preApp",preApp)
                .replace("$preHost",preHost)
                .replace("$preIp",preIp)
                .replace("$traceId",traceId)
                .replace("$spanId",spanId);
    }

    public static String getLabelPattern() {
        return labelPattern;
    }

    public static void setLabelPattern(String labelPattern) {
        LogLabelGenerator.labelPattern = labelPattern;
    }
}
