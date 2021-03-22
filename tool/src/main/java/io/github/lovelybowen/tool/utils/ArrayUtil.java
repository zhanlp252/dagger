package io.github.lovelybowen.tool.utils;

/**
 * @Auther: huangxingyao
 * @Date: 19-2-24
 * @Description:
 */
public class ArrayUtil {

    /**
     * 不为空返回true
     *
     * @param arrary
     * @param <T>
     * @return
     */
    public static <T> boolean isEmpty(T[] arrary) {
        return arrary == null || arrary.length == 0;
    }
}
