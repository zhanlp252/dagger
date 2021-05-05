package io.github.novareseller.log.aop;

import cn.hutool.json.JSONUtil;
import com.google.common.collect.Maps;
import io.github.novareseller.log.annotation.LogAspect;
import io.github.novareseller.log.constant.LogConstants;
import io.github.novareseller.log.context.AspectLogContext;
import io.github.novareseller.log.context.LogContext;
import io.github.novareseller.log.convert.AspectLogConvert;
import io.github.novareseller.tool.date.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Map;

/**
 * 自定义埋点注解切面，用于拦截@AspectLogAop
 *
 * @author bowen
 * @date 2021/05/05
 */
@Aspect
public class AspectLogAop {

    private static final Logger log = LoggerFactory.getLogger(AspectLogAop.class);

    @Pointcut("@annotation(io.github.novareseller.log.annotation.LogAspect)")
    public void cut() {
    }

    @Around("cut()")
    public Object around(ProceedingJoinPoint jp) throws Throwable {
        Object[] args = jp.getArgs();
        MethodSignature signature = (MethodSignature) jp.getSignature();
        Method method = signature.getMethod();
        String[] parameterNames = signature.getParameterNames();
        Map<String, Object> paramNameValueMap = Maps.newHashMap();
        for (int i = 0; i < parameterNames.length; i++) {
            paramNameValueMap.put(parameterNames[i], args[i]);
        }

        LogAspect logAspect = method.getAnnotation(LogAspect.class);
        String[] aspectExpressions = logAspect.value();
        String pattern = logAspect.pattern().replaceAll("\\{\\}", "{0}");
        String joint = logAspect.joint();
        Class<? extends AspectLogConvert> convertClazz = logAspect.convert();

        StringBuilder sb = new StringBuilder();
        if (!convertClazz.equals(AspectLogConvert.class)) {
            AspectLogConvert convert = convertClazz.newInstance();
            try {
                sb.append(convert.convert(args));
            } catch (Throwable t) {
                log.error("[AspectLog]some errors happens in AspectLog's convert", t);
            }
        } else {
            for (String aspectExpression : aspectExpressions) {
                String aspLogValueItem = getExpressionValue(aspectExpression, paramNameValueMap);
                if (StringUtils.isNotBlank(aspLogValueItem)) {
                    sb.append(String.format("%s:%s", aspectExpression, aspLogValueItem));
                    sb.append(joint);
                }
            }
        }

        String aspLogValue = sb.toString();
        if (StringUtils.isNotBlank(aspLogValue)) {
            aspLogValue = aspLogValue.substring(0, aspLogValue.length() - joint.length());

            aspLogValue = MessageFormat.format(pattern, aspLogValue);

            //拿到之前的标签
            String currentLabel = AspectLogContext.getLogValue();

            if (LogContext.hasLogMDC()) {
                MDC.put(LogConstants.MDC_KEY, currentLabel + aspLogValue);
            } else {
                AspectLogContext.putLogValue(currentLabel + aspLogValue);
            }
        }

        try {
            return jp.proceed();
        } finally {
            AspectLogContext.remove();
        }
    }

    private String getExpressionValue(String expression, Object o) {
        String[] expressionItems = expression.split("\\.");
        for (String item : expressionItems) {
            if (String.class.isAssignableFrom(o.getClass())) {
                return (String) o;
            } else if (Integer.class.isAssignableFrom(o.getClass())) {
                return ((Integer) o).toString();
            } else if (Long.class.isAssignableFrom(o.getClass())) {
                return ((Long) o).toString();
            } else if (Double.class.isAssignableFrom(o.getClass())) {
                return ((Double) o).toString();
            } else if (BigDecimal.class.isAssignableFrom(o.getClass())) {
                return ((BigDecimal) o).toPlainString();
            } else if (Date.class.isAssignableFrom(o.getClass())) {
                return DateUtil.formatDate((Date) o);
            } else if (Map.class.isAssignableFrom(o.getClass())) {
                Object v = ((Map) o).get(item);
                if (v == null) {
                    return null;
                }
                if (expression.equals(getRemainExpression(expression, item))) {
                    v = JSONUtil.toJsonStr(v);
                }
                return getExpressionValue(getRemainExpression(expression, item), v);
            } else {
                try {
                    Object v = MethodUtils.invokeMethod(o, "get" + item.substring(0, 1).toUpperCase() + item.substring(1));
                    if (v == null) {
                        return null;
                    }
                    if (expression.equals(getRemainExpression(expression, item))) {
                        v = JSONUtil.toJsonStr(v);
                    }
                    return getExpressionValue(getRemainExpression(expression, item), v);
                } catch (Exception e) {
                    return null;
                }
            }
        }
        return null;
    }


    private String getRemainExpression(String expression, String expressionItem) {
        if (expression.equals(expressionItem)) {
            return expressionItem;
        } else {
            return expression.substring(expressionItem.length() + 1);
        }
    }

    private boolean isRemainExpression(String expression, String expressionItem) {
        if (expression.equals(expressionItem)) {
            return true;
        } else {
            return false;
        }
    }


}
