package com.sky.utils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TimeUtil {
    /**
     * 获取指定日期区间内的日期
     *
     * @param begin 开始日期
     * @param end   结束日期
     * @return 日期区间内的日期
     */
    public static List<String> getIntervalDates(LocalDate begin, LocalDate end) {
        List<String> list = new ArrayList<>();
        while (!begin.isAfter(end)) {
            list.add(begin.toString());
            begin = begin.plusDays(1);
        }
        return list;
    }
}
