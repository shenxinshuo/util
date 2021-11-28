package util;

/**
 * @author: 蚝烙迈落蚝
 * @date: 2021/11/26
 * @description: String工具类
 */
public class StringUtils {

    /**
     * 判断String是不是真的为空
     *
     * @param str 入参字符串
     * @return 是否为空
     */
    public static boolean isRealEmpty(String str) {
        return str == null || "".equals(str);
    }

    public static boolean isNotRealEmpty(String str) {
        return !isRealEmpty(str);
    }

}
