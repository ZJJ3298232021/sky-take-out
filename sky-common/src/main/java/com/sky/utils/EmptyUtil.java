package com.sky.utils;

import java.util.List;
import java.util.Objects;

public class EmptyUtil {
    public static boolean listEmpty(List list) {
        return Objects.isNull(list) || list.isEmpty();
    }

    public static boolean stringEmpty(String str) {
        return str==null || str.isEmpty();
    }

}
