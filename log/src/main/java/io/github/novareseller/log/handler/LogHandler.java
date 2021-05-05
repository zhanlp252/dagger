package io.github.novareseller.log.handler;


import io.github.novareseller.log.constant.LogConstants;
import io.github.novareseller.log.context.AspectLogContext;
import io.github.novareseller.log.context.LogContext;
import io.github.novareseller.log.context.LogLabelGenerator;
import io.github.novareseller.log.id.LogIdGeneratorLoader;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * Log的RPC处理逻辑的封装类
 *
 * @author bowen
 * @date 2021/05/05
 */
public class LogHandler {

    protected static final Logger log = LoggerFactory.getLogger(LogHandler.class);

    public void processProviderSide(LogLabelBean labelBean) {
        if (StringUtils.isBlank(labelBean.getPreIvkApp())) {
            labelBean.setPreIvkApp(LogConstants.UNKNOWN);
        }
        LogContext.putPreIvkApp(labelBean.getPreIvkApp());

        if (StringUtils.isBlank(labelBean.getPreIvkHost())) {
            labelBean.setPreIvkHost(LogConstants.UNKNOWN);
        }
        LogContext.putPreIvkHost(labelBean.getPreIvkHost());

        if (StringUtils.isBlank(labelBean.getPreIp())) {
            labelBean.setPreIp(LogConstants.UNKNOWN);
        }
        LogContext.putPreIp(labelBean.getPreIp());

        //如果从隐式传参里没有获取到，则重新生成一个traceId
        if (StringUtils.isBlank(labelBean.getTraceId())) {
            labelBean.setTraceId(LogIdGeneratorLoader.getIdGenerator().generateTraceId());
            log.debug("Maybe the previous node [{}] did not pass the traceId correctly, regenerate the traceId[{}]",
                    labelBean.getPreIvkApp(), labelBean.getTraceId());
        }

        //往Log上下文里放当前获取到的spanId，如果spanId为空，会放入初始值
        LogContext.putSpanId(labelBean.getSpanId());

        //往Log上下文里放一个当前的traceId
        LogContext.putTraceId(labelBean.getTraceId());

        //生成日志标签
        String logLabel = LogLabelGenerator.generateLogLabel(labelBean.getPreIvkApp(),
                labelBean.getPreIvkHost(),
                labelBean.getPreIp(),
                labelBean.getTraceId(),
                LogContext.getSpanId());

        //往日志切面器里放一个日志前缀
        AspectLogContext.putLogValue(logLabel);

        //如果有MDC，则往MDC中放入日志标签
        if (LogContext.hasLogMDC()) {
            MDC.put(LogConstants.MDC_KEY, logLabel);
        }
    }

    public void cleanThreadLocal() {
        //移除ThreadLocal里的数据
        LogContext.removePreIvkApp();
        LogContext.removePreIvkHost();
        LogContext.removePreIp();
        LogContext.removeTraceId();
        LogContext.removeSpanId();
        AspectLogContext.remove();
        if (LogContext.hasLogMDC()) {
            MDC.remove(LogConstants.MDC_KEY);
        }
    }
}
