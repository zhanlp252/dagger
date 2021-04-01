package io.github.lovelybowen.tool.utils;


/**
 * @author: Bowen huang
 * @date: 2020/06/21
 */
public class NumberUtil {


    /**
     * <p>Convert a <code>String</code> to an <code>int</code>, returning a
     * default value if the conversion fails.</p>
     *
     * <p>If the string is <code>null</code>, the default value is returned.</p>
     *
     * <pre>
     *   NumberUtil.toInt(null, 1) = 1
     *   NumberUtil.toInt("", 1)   = 1
     *   NumberUtil.toInt("1", 0)  = 1
     * </pre>
     *
     * @param defaultValue the default value
     * @return the int represented by the string, or the default if conversion fails
     */
    public static int toInt(final Integer integer, final int defaultValue) {
        if (integer == null) {
            return defaultValue;
        }
        try {
            return Integer.valueOf(integer);
        } catch (final NumberFormatException nfe) {
            return defaultValue;
        }
    }
}
