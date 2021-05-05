package io.github.novareseller.log.annotation;



import io.github.novareseller.log.convert.AspectLogConvert;

import java.lang.annotation.*;

/**
 * Log的自定义注解的切面类
 *
 * @author bowen
 * @date 2021/05/05
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface LogAspect {

    String[] value() default {};

    String joint() default ",";

    String pattern() default "[{}]";

    Class<? extends AspectLogConvert> convert() default AspectLogConvert.class;

}
