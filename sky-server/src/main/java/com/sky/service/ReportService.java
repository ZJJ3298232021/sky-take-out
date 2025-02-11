package com.sky.service;

import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import java.time.LocalDate;

/**
 * 数据统计相关接口
 */
public interface ReportService {

    /**
     * 营业额统计
     * @param begin 起始时间
     * @param end 结束时间
     * @return .
     */
    TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end);

    /**
     * 用户统计
     * @param begin 起始时间
     * @param end 结束时间
     * @return .
     */
    UserReportVO getUserStatistics(LocalDate begin, LocalDate end);
}
