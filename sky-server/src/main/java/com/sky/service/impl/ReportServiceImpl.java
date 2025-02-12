package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.utils.TimeUtil;
import com.sky.vo.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.sky.utils.NumberUtil.remain2Decimal;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    public final OrderMapper orderMapper;

    public final UserMapper userMapper;

    public final WorkspaceService workspaceService;


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
            totalOrderCount += count;

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
     * 销量排名前十
     *
     * @param begin 起始时间
     * @param end   结束时间
     * @return .
     */
    @Override
    public SalesTop10ReportVO getTop10Dishes(LocalDate begin, LocalDate end) {
        List<GoodsSalesDTO> top10Dishes = orderMapper.getTop10Dishes(begin.toString(), end.toString());

        return SalesTop10ReportVO
                .builder()
                .nameList(StringUtils.join(top10Dishes
                        .stream()
                        .map(GoodsSalesDTO::getName)
                        .collect(Collectors.toList()), ","))
                .numberList(StringUtils.join(top10Dishes
                        .stream()
                        .map(GoodsSalesDTO::getCopies)
                        .collect(Collectors.toList()), ","))
                .build();
    }

    /**
     * 导出数据
     *
     * @param response .
     */
    @Override
    public void exportData(HttpServletResponse response) {
        //获取30天前日期
        LocalDate start = LocalDate.now().minusDays(30);

        //30天数据概览
        BusinessDataVO business30Data = new BusinessDataVO();

        //30天订单总数
        int totalOrderCount = 0;

        //30天数据列表详细
        List<BusinessDataVO> business30List = new ArrayList<>();
        //30天日期
        List<String> date30List = TimeUtil.getIntervalDates(start, LocalDate.now());

        //获取30天数据
        while (!start.isAfter(LocalDate.now())) {
            business30List.add(workspaceService.getBusinessData(start.toString()));
            totalOrderCount += orderMapper.getOrderCount(Map.of("date", start.toString()));
            start = start.plusDays(1);
        }

        //根据每天数据计算30天数据
        business30Data.setValidOrderCount(
                business30List
                        .stream()
                        .mapToInt(BusinessDataVO::getValidOrderCount)
                        .sum()
        );
        business30Data.setTurnover(
                remain2Decimal(business30List
                        .stream()
                        .mapToDouble(BusinessDataVO::getTurnover)
                        .sum())
        );
        business30Data.setNewUsers(
                business30List
                        .stream()
                        .mapToInt(BusinessDataVO::getNewUsers)
                        .sum()
        );

        //计算客单价和订单完成率
        if (business30Data.getValidOrderCount() != 0 && totalOrderCount != 0) {
            business30Data.setUnitPrice(
                    remain2Decimal(business30Data.getTurnover() / business30Data.getValidOrderCount())
            );
            business30Data.setOrderCompletionRate(
                    remain2Decimal(business30Data.getValidOrderCount() / (double) totalOrderCount)
            );
        }

        //开始写入 easyexcel
        writeExcel(response, business30List, date30List, business30Data);
    }

    /**
     * 写入excel
     *
     * @param response       .
     * @param business30List 30天数据列表
     * @param date30List     30天日期列表
     * @param business30Data 30天数据概览
     */
    private void writeExcel(
            HttpServletResponse response,
            List<BusinessDataVO> business30List,
            List<String> date30List,
            BusinessDataVO business30Data) {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");
        try {
            if (in != null) {
                XSSFWorkbook excel = new XSSFWorkbook(in);
                //获取sheet
                XSSFSheet sheet = excel.getSheet("Sheet1");

                //设置日期
                sheet.getRow(1)
                        .getCell(1)
                        .setCellValue(
                                "时间:" + date30List.get(0) + "至" + date30List.get(date30List.size() - 1)
                        );

                //设置30天数据
                XSSFRow row3 = sheet.getRow(3);
                row3.getCell(2).setCellValue(business30Data.getTurnover());
                row3.getCell(4).setCellValue(business30Data.getOrderCompletionRate());
                row3.getCell(6).setCellValue(business30Data.getNewUsers());

                XSSFRow row = sheet.getRow(4);
                row.getCell(2).setCellValue(business30Data.getValidOrderCount());
                row.getCell(4).setCellValue(business30Data.getUnitPrice());

                //设置30天明细数据
                for (int i=7;i<30+7;i++) {
                    XSSFRow rowx = sheet.getRow(i);
                    rowx.getCell(1).setCellValue(date30List.get(i-7));
                    rowx.getCell(2).setCellValue(business30List.get(i-7).getTurnover());
                    rowx.getCell(3).setCellValue(business30List.get(i-7).getValidOrderCount());
                    rowx.getCell(4).setCellValue(business30List.get(i-7).getOrderCompletionRate());
                    rowx.getCell(5).setCellValue(business30List.get(i-7).getUnitPrice());
                    rowx.getCell(6).setCellValue(business30List.get(i-7).getNewUsers());
                }
                response.setContentType("application/vnd.ms-excel");
                response.setCharacterEncoding("utf-8");
                response.setHeader("Content-disposition", "attachment;filename=report.xlsx");
                excel.write(response.getOutputStream());
                in.close();
                excel.close();
            } else throw new RuntimeException("找不到模板文件");
        } catch (Exception e) {
            log.error("导出数据失败", e);
        }

    }
}
