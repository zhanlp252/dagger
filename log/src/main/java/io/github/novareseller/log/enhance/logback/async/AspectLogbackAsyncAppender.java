package io.github.novareseller.log.enhance.logback.async;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import cn.hutool.core.util.ReflectUtil;
import io.github.novareseller.log.constant.LogConstants;
import io.github.novareseller.log.context.AspectLogContext;
import io.github.novareseller.log.context.LogContext;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Logback的异步日志增强appender
 *
 * @author bowen
 * @date 2021/05/05
 */
public class AspectLogbackAsyncAppender extends AsyncAppender {

    private Field field;

    private static final Cache<LoggingEvent, Integer> lruCache = CacheUtil.newLRUCache(2048);

    @Override
    protected void append(ILoggingEvent eventObject) {
        if(eventObject instanceof LoggingEvent){
            LoggingEvent loggingEvent = (LoggingEvent)eventObject;

            if (!lruCache.containsKey(loggingEvent)){
                String resultLog;
                final String logValue = AspectLogContext.getLogValue();

                if (LogContext.hasLogMDC() && StringUtils.isNotBlank(logValue)){
                    Map<String, String> mdcMap = new HashMap<>();
                    mdcMap.put(LogConstants.MDC_KEY, logValue);
                    loggingEvent.setMDCPropertyMap(mdcMap);
                }

                if (!LogContext.hasLogMDC() && StringUtils.isNotBlank(logValue)) {
                    resultLog = String.format("%s %s", logValue,loggingEvent.getFormattedMessage());
                    if(field == null){
                        field = ReflectUtil.getField(LoggingEvent.class,"formattedMessage");
                        field.setAccessible(true);
                    }

                    try {
                        field.set(loggingEvent,resultLog);
                    } catch (IllegalAccessException e) {
                    }
                }

                eventObject = loggingEvent;
                lruCache.put(loggingEvent, Integer.MIN_VALUE);
            }
        }
        super.append(eventObject);
    }
}
