package com.sky.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/*
 * @description: 时间字符串获取工具
 */
public class DateStringUtil {

    public static String getNowDateString(String pattern) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return now.format(formatter);
    }

}
