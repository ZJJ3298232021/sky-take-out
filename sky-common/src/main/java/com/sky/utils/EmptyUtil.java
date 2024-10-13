package com.sky.utils;

import java.util.List;

public class EmptyUtil {
    public static boolean listEmpty(List list) {
        return list==null || list.isEmpty();
    }

    public static boolean stringEmpty(String str) {
        return str==null || str.isEmpty();
    }

}
