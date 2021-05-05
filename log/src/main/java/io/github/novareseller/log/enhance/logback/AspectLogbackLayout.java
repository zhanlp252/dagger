package io.github.novareseller.log.enhance.logback;

import ch.qos.logback.classic.PatternLayout;

/**
 * 基于日志适配方式logback的自定义layout
 *
 * @author bowen
 * @date 2021/05/05
 */
public class AspectLogbackLayout extends PatternLayout {
    static {
        defaultConverterMap.put("m", AspectLogbackConverter.class.getName());
        defaultConverterMap.put("msg", AspectLogbackConverter.class.getName());
        defaultConverterMap.put("message", AspectLogbackConverter.class.getName());
        defaultConverterMap.put("X", AspectLogbackMDCConverter.class.getName());
        defaultConverterMap.put("mdc", AspectLogbackMDCConverter.class.getName());
    }
}
