package io.github.novareseller.boot.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * @author: Bowen huang
 * @date: 2020/08/09
 */
@Target({ ElementType.FIELD,ElementType.METHOD,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = CustomValueValidator.class)
public @interface CustomValue {

    /**
     * Error message prompt
     * @return
     */
    String message() default "the parameter must be the specified value";

    /**
     * check by group
     * @return
     */
    Class<?>[] groups() default { };

    /**
     * payload
     * @return
     */
    Class<? extends Payload>[] payload() default { };

    /**
     * Specified string
     * @return
     */
    String[] strValues() default {};

    /**
     * Specified int
     * @return
     */
    int[] intValues() default {};
}
