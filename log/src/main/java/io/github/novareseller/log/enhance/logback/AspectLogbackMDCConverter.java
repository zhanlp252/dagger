package io.github.novareseller.log.enhance.logback;

import ch.qos.logback.classic.pattern.MDCConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import cn.hutool.core.util.ReflectUtil;
import io.github.novareseller.log.constant.LogConstants;
import io.github.novareseller.log.context.AspectLogContext;
import io.github.novareseller.log.context.LogContext;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;

/**
 * Logback的MDC转换器，这个类主要覆盖了start方向，目的是在pattern里如果配置了mdc的话，把全局mdc变量设为true
 *
 * @author bowen
 * @date 2021/05/05
 */
public class AspectLogbackMDCConverter extends MDCConverter {

    private Field keyField;

    @Override
    public void start() {
        super.start();
        if (keyField == null) {
            keyField = ReflectUtil.getField(this.getClass(), "key");
        }
        String keyValue = (String) ReflectUtil.getFieldValue(this, keyField);
        if (StringUtils.isNotEmpty(keyValue) & keyValue.equals(LogConstants.MDC_KEY)) {
            LogContext.setHasLogMDC(true);
        }
    }

    @Override
    public String convert(ILoggingEvent event) {
        String value = super.convert(event);
        if (StringUtils.isBlank(value)){
            value = AspectLogContext.getLogValue();
        }

        if (StringUtils.isBlank(value)){
            value = "";
        }
        return value;
    }
}
