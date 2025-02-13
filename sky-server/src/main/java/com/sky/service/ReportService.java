package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import jakarta.servlet.http.HttpServletResponse;

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

    /**
     * 订单统计
     * @param begin 起始时间
     * @param end 结束时间
     * @return .
     */
    OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end);

    /**
     * 销量排名
     * @param begin 起始时间
     * @param end 结束时间
     * @return .
     */
    SalesTop10ReportVO getTop10Dishes(LocalDate begin, LocalDate end);

    /**
     * 导出数据
     * @param response .
     */
    void exportData(HttpServletResponse response);
}
