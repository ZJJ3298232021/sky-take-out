package com.sky.utils;

public class NumberUtil {
    /**
     * 保留两位小数
     * @param num .
     * @return .
     */
    public static double remain2Decimal(double num) {
        return  (double) Math.round(num * 100) / 100;
    }
}
