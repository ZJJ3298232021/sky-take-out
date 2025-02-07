package com.sky.controller.admin;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@Tag(name = "订单管理")
@RequiredArgsConstructor
@RequestMapping("/admin/order")
public class OrderController {

    private final OrderService orderService;

    /**
     * 订单条件搜索
     *
     * @param dto .
     * @return .
     */
    @Operation(description = "订单条件搜索")
    @GetMapping("conditionSearch")
    public Result<PageResult<Orders>> conditionSearch(OrdersPageQueryDTO dto) {
        log.info("订单条件搜索：{}", dto);
        PageResult<Orders> orders = orderService.conditionSearch(dto);
        return Result.success(orders);
    }

    /**
     * 各个状态订单数量信息
     *
     * @return .
     */
    @GetMapping("/statistics")
    @Operation(description = "各个状态订单数量信息")
    public Result<OrderStatisticsVO> statistics() {
        log.info("各个状态订单数量信息");
        return Result.success(orderService.statistics());
    }

    /**
     * 查询订单详情
     *
     * @param id 订单ID
     * @return .
     */
    @GetMapping("/details/{id}")
    @Operation(description = "订单详情")
    public Result<OrderVO> orderDetails(@PathVariable Long id) {
        log.info("订单详情：{}", id);
        return Result.success(orderService.orderDetail(id));
    }

    /**
     * 接单
     *
     * @param dto .
     * @return .
     */
    @PutMapping("/confirm")
    @Operation(description = "接单")
    public Result<?> confirmOrder(@RequestBody OrdersConfirmDTO dto) {
        log.info("接单：{}", dto);
        orderService.confirmOrder(dto.getId());
        return Result.success();
    }

    /**
     * 拒单
     *
     * @param dto .
     * @return .
     */
    @PutMapping("/rejection")
    @Operation(description = "拒单")
    public Result<?> rejectOrder(@RequestBody OrdersRejectionDTO dto) {
        log.info("拒单：{}", dto);
        orderService.rejectOrder(dto);
        return Result.success();
    }

    /**
     * 取消订单
     *
     * @param dto .
     * @return .
     */
    @PutMapping("/cancel")
    @Operation(description = "取消订单")
    public Result<?> cancelOrder(@RequestBody OrdersCancelDTO dto) {
        log.info("取消订单：{}", dto);
        orderService.cancelOrder(dto);
        return Result.success();
    }

    /**
     * 派送订单
     *
     * @param id .
     * @return .
     */
    @PutMapping("/delivery/{id}")
    @Operation(description = "派送订单")
    public Result<?> deliverOrder(@PathVariable Long id) {
        log.info("派送订单：{}", id);
        orderService.deliverOrder(id);
        return Result.success();
    }

    @PutMapping("/complete/{id}")
    @Operation(description = "完成订单")
    public Result<?> completeOrder(@PathVariable Long id) {
        log.info("完成订单：{}", id);
        orderService.completeOrder(id);
        return Result.success();
    }
}
