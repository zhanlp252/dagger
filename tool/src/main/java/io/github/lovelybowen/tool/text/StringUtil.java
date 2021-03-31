package io.github.lovelybowen.tool.text;


import org.apache.commons.lang3.StringUtils;

/**
 * @author: Bowen huang
 * @date: 2021/03/18
 */
public class StringUtil extends org.apache.commons.lang3.StringUtils {

    public static String[] toStrArray(String split, String str) {
        if (StringUtils.isEmpty(str)) {
            return new String[]{};
        }
        return str.split(split);
    }

    /**
     * * 判断一个对象是否为空
     *
     * @param object Object
     * @return true：为空 false：非空
     */
    public static boolean isNull(Object object)
    {
        return object == null;
    }

    public static String humpToUnderline(String para) {
        para = lowerFirst(para);
        StringBuilder sb = new StringBuilder(para);
        int temp = 0;
        for (int i = 0; i < para.length(); i++) {
            if (Character.isUpperCase(para.charAt(i))) {
                sb.insert(i + temp, "_");
                temp += 1;
            }
        }
        return sb.toString().toLowerCase();
    }

    public static String lowerFirst(String str) {
        char firstChar = str.charAt(0);
        if (firstChar >= 'A' && firstChar <= 'Z') {
            char[] arr = str.toCharArray();
            arr[0] += ('a' - 'A');
            return new String(arr);
        }
        return str;
    }

    public static String removeSuffix(CharSequence str, CharSequence suffix) {
        if (StringUtils.isEmpty(str) || StringUtils.isEmpty(suffix)) {
            return StringPool.EMPTY;
        }

        final String str2 = str.toString();
        if (str2.endsWith(suffix.toString())) {
            return sub(str2, 0, str2.length() - suffix.length());
        }
        return str2;
    }

    public static String sub(CharSequence str, int fromIndex, int toIndex) {
        if (StringUtils.isEmpty(str)) {
            return StringPool.EMPTY;
        }
        int len = str.length();

        if (fromIndex < 0) {
            fromIndex = len + fromIndex;
            if (fromIndex < 0) {
                fromIndex = 0;
            }
        } else if (fromIndex > len) {
            fromIndex = len;
        }

        if (toIndex < 0) {
            toIndex = len + toIndex;
            if (toIndex < 0) {
                toIndex = len;
            }
        } else if (toIndex > len) {
            toIndex = len;
        }

        if (toIndex < fromIndex) {
            int tmp = fromIndex;
            fromIndex = toIndex;
            toIndex = tmp;
        }

        if (fromIndex == toIndex) {
            return StringPool.EMPTY;
        }

        return str.toString().substring(fromIndex, toIndex);
    }

}
