package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * 工作台
 */
@RestController
@RequestMapping("/admin/workspace")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "工作台相关接口")
public class WorkSpaceController {

    private final WorkspaceService workspaceService;

    /**
     * 工作台今日数据查询
     *
     * @return .
     */
    @GetMapping("/businessData")
    @Operation(description = "工作台今日数据查询")
    public Result<BusinessDataVO> businessData() {
        log.info("查询今日运营数据");
        BusinessDataVO businessDataVO = workspaceService.getBusinessData(LocalDate.now().toString());
        return Result.success(businessDataVO);
    }

    /**
     * 查询订单管理数据
     *
     * @return .
     */
    @GetMapping("/overviewOrders")
    @Operation(description = "查询订单管理数据")
    public Result<OrderOverViewVO> orderOverView() {
        log.info("查询订单管理数据");
        return Result.success(workspaceService.getOrderOverView());
    }

    /**
     * 查询菜品总览
     *
     * @return .
     */
    @GetMapping("/overviewDishes")
    @Operation(description = "查询菜品总览")
    public Result<DishOverViewVO> dishOverView() {
        log.info("查询菜品总览");
        return Result.success(workspaceService.getDishOverView());
    }

    /**
     * 查询套餐总览
     *
     * @return .
     */
    @GetMapping("/overviewSetmeals")
    @Operation(description = "查询套餐总览")
    public Result<SetmealOverViewVO> setmealOverView() {
        log.info("查询套餐总览");
        return Result.success(workspaceService.getSetmealOverView());
    }
}
