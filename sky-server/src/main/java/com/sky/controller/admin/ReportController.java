package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@Slf4j
@Tag(name = "数据统计相关接口")
@RequiredArgsConstructor
@RequestMapping("/admin/report")
public class ReportController {

    private final ReportService reportService;

    /**
     * 营业额统计
     * @param begin 起始时间
     * @param end 结束时间
     * @return .
     */
    @GetMapping("/turnoverStatistics")
    @Operation(description = "营业额统计")
    public Result<TurnoverReportVO> turnoverStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate end) {
        log.info("营业额统计：{} ~ {}", begin, end);
        return Result.success(reportService.getTurnoverStatistics(begin, end));
    }

    /**
     * 用户统计
     * @param begin 起始时间
     * @param end 结束时间
     * @return .
     */
    @GetMapping("/userStatistics")
    @Operation(description = "用户统计")
    public Result<UserReportVO> userStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate end) {
        log.info("用户统计：{} ~ {}", begin, end);
        return Result.success(reportService.getUserStatistics(begin, end));
    }
}
