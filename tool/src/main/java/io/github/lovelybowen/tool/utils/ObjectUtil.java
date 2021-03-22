package io.github.lovelybowen.tool.utils;

import org.apache.commons.lang3.Validate;

/**
 * @author: Bowen huang
 * @date: 2021/03/18
 */
public class ObjectUtil {


    /**
     * Don't let anyone instantiate this class.
     */
    private ObjectUtil() {
        throw new AssertionError("No " + getClass().getName() + " instances for you!");
    }


    public static <T> T defaultIfNullOrEmpty(final T object, final T defaultValue) {
        return !Validator.isNullOrEmpty(object) ? object : defaultValue;
    }


    public static boolean isArray(Object object) {
        Validate.notNull(object, "object can't be null!");
        return object.getClass().isArray();
    }


}
