package io.github.novareseller.log.enhance.logback;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

import io.github.novareseller.log.context.AspectLogContext;
import io.github.novareseller.log.context.LogContext;
import org.apache.commons.lang3.StringUtils;

/**
 * 基于日志适配方式的logback的convert
 *
 * @author bowen
 * @date 2021/05/05
 */
public class AspectLogbackConverter extends ClassicConverter {
    @Override
    public String convert(ILoggingEvent event) {
        //只有在MDC没有设置的情况下才加到message里
        if (!LogContext.hasLogMDC()) {
            String logValue = AspectLogContext.getLogValue();
            if (StringUtils.isBlank(logValue)) {
                return event.getFormattedMessage();
            } else {
                return logValue + " " + event.getFormattedMessage();
            }
        } else {
            return event.getFormattedMessage();
        }
    }
}
