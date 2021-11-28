package util;

import java.util.List;

/**
 * @author: 蚝烙迈落蚝
 * @date: 2021/11/26
 * @description:
 */
public class ListUtils {

    /**
     * 判断List是不是真的为空
     *
     * @param list 入参字符串
     * @return 是否为空
     */
    public static boolean isRealEmpty(List list) {
        return list == null || list.size() == 0;
    }

    public static boolean isNotRealEmpty(List list) {
        return !isRealEmpty(list);
    }

}
