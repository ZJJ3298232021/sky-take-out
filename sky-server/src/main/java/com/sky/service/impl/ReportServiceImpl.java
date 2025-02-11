package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.utils.TimeUtil;
import com.sky.vo.OrderReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    public final OrderMapper orderMapper;

    public final UserMapper userMapper;

    /**
     * 营业额统计
     *
     * @param begin 起始时间
     * @param end   结束时间
     * @return .
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        //select COUNT(*) total, DATE_FORMAT(order_time, '%Y-%m-%d') time
        //from orders
        //where DATE_FORMAT(order_time, '%Y-%m-%d') in ('2025-02-07','2025-02-06') group by time ;

        //select COUNT(*) from orders where DATE_FORMAT(order_time, '%Y-%m-%d') = #{date} and status = 5;

        //获取指定日期区间内的日期
        List<String> intervalDates = TimeUtil.getIntervalDates(begin, end);
        List<String> turnoverList = new ArrayList<>();

        //遍历日期集合，获取每个日期的营业额
        intervalDates.forEach(date -> {
            Float turnover = orderMapper.getTurnover(date);

            //如果turnover为空，则设置为0
            turnoverList.add(Objects.isNull(turnover) ? "0" : String.valueOf(turnover));
        });

        return TurnoverReportVO
                .builder()
                .turnoverList(StringUtils.join(turnoverList, ","))
                .dateList(StringUtils.join(intervalDates, ","))
                .build();
    }

    /**
     * 用户统计
     *
     * @param begin 起始时间
     * @param end   结束时间
     * @return .
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {

        //select COUNT(*) from user where DATE(create_time) < DATE_ADD('2025-02-01', INTERVAL 1 DAY );

        //select COUNT(*) from user where DATE(create_time) = #{date}

        List<String> intervalDates = TimeUtil.getIntervalDates(begin, end);
        List<String> newUserList = new ArrayList<>();
        List<String> totalUserList = new ArrayList<>();

        //遍历日期集合，获取每个日期的新增用户和总用户
        intervalDates.forEach(date -> {
            Integer totalUser = userMapper.getTotalUser(date);
            Integer newUser = userMapper.getNewUser(date);
            totalUserList.add(Objects.isNull(totalUser) ? "0" : String.valueOf(totalUser));
            newUserList.add(Objects.isNull(newUser) ? "0" : String.valueOf(newUser));
        });

        return UserReportVO
                .builder()
                .newUserList(StringUtils.join(newUserList, ","))
                .dateList(StringUtils.join(intervalDates, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .build();
    }

    /**
     * 订单统计
     *
     * @param begin 起始时间
     * @param end   结束时间
     * @return .
     */
    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {


        List<String> intervalDates = TimeUtil.getIntervalDates(begin, end);
        List<String> orderCountList = new ArrayList<>();
        List<String> validOrderCountList = new ArrayList<>();
        int totalOrderCount = 0;
        int validOrderCount = 0;
        double orderCompletionRate;

        //遍历日期集合，获取每个日期的订单数量和有效订单数量
        for (String date : intervalDates) {

            HashMap<String, String> map = new HashMap<>();
            map.put("date", date);

            //获取订单数量
            Integer count = orderMapper.getOrderCount(map);
            count = Objects.isNull(count) ? 0 : count;
            orderCountList.add(String.valueOf(count));
            totalOrderCount+=count;

            //获取有效订单数量
            map.put("status", String.valueOf(Orders.COMPLETED));
            Integer count1 = orderMapper.getOrderCount(map);
            count1 = Objects.isNull(count1) ? 0 : count1;
            validOrderCountList.add(String.valueOf(count1));
            validOrderCount += count1;

        }

        //计算订单完成率
        orderCompletionRate = validOrderCount / (double) totalOrderCount;

        return OrderReportVO
                .builder()
                .orderCompletionRate(orderCompletionRate)
                .validOrderCount(validOrderCount)
                .totalOrderCount(totalOrderCount)
                .dateList(StringUtils.join(intervalDates, ","))
                .orderCountList(StringUtils.join(orderCountList, ","))
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .build();
    }


    /**
     * 将List<String>转换为格式化的String
     *
     * @param list .
     * @return .
     */
    @Deprecated
    private String listToString(List<String> list) {
        StringBuilder sb = new StringBuilder();
        for (String s : list) {
            sb.append(s).append(",");
        }
        return sb.toString();
    }
}
