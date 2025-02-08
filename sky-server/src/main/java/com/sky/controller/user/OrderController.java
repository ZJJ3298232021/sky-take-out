package com.sky.controller.user;

import com.sky.constant.PathConstant;
import com.sky.constant.TradeConstant;
import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController("userOrderController")
@RequestMapping(PathConstant.USER_ORDER)
@Slf4j
@Tag(name = "C端-用户订单接口")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 提交订单
     *
     * @param dto .
     * @return .
     */
    @PostMapping("/submit")
    @Operation(description = "提交订单")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO dto) {
        log.info("用户提交订单：{}", dto);
        OrderSubmitVO orderSubmitVO = orderService.submitOrder(dto);
        return Result.success(orderSubmitVO);
    }

    /**
     * 支付订单
     *
     * @param paymentDTO .
     * @return .
     */
    @PutMapping("/payment")
    @Operation(description = "支付订单")
    public Result<?> pay(@RequestBody OrdersPaymentDTO paymentDTO) {
        log.info("用户支付订单：{}", paymentDTO);
        return Result.success(orderService.pay(paymentDTO));
    }

    /**
     * 查询订单支付状态
     *
     * @param orderId .
     * @return .
     */
    @GetMapping("/checkStatus")
    public Result<Integer> checkStatus(Long orderId) {
        log.info("查询订单支付状态：{}", orderId);
        Integer payStatus = orderService.getPayStatus(orderId);
        return Result.success(payStatus);
    }

    /**
     * 历史订单
     *
     * @param dto .
     * @return .
     */
    @GetMapping("/historyOrders")
    @Operation(description = "查询历史订单")
    public Result<PageResult<OrderVO>> historyOrders(OrdersPageQueryDTO dto) {
        log.info("查询历史订单：{}", dto);
        PageResult<OrderVO> pageResult = orderService.historyOrders(dto);
        return Result.success(pageResult);
    }

    /**
     * 订单详情
     *
     * @param id .
     * @return .
     */
    @GetMapping("/orderDetail/{id}")
    @Operation(description = "查询订单详情")
    public Result<OrderVO> orderDetail(@PathVariable Long id) {
        log.info("查询订单详情：{}", id);
        OrderVO orderVO = orderService.orderDetail(id);
        return Result.success(orderVO);
    }

    /**
     * 取消订单
     *
     * @param id .
     * @return .
     */
    @PutMapping("/cancel/{id}")
    @Operation(description = "取消订单")
    public Result<?> cancelOrder(@PathVariable Long id) {
        log.info("取消订单：{}", id);
        orderService.cancelOrder(new OrdersCancelDTO(id, TradeConstant.CANCELED));
        return Result.success();
    }

    /**
     * 再来一单
     *
     * @param id .
     * @return .
     */
    @PostMapping("/repetition/{id}")
    @Operation(description = "再来一单")
    public Result<?> oneMoreOrder(@PathVariable Long id) {
        log.info("再来一单：{}", id);
        orderService.oneMoreOrder(id);
        return Result.success();
    }

    /**
     * 订单催单
     * @param id 订单id
     * @return .
     */
    @GetMapping("/reminder/{id}")
    @Operation(description = "订单催单")
    public Result<?> orderReminder(@PathVariable Long id) {
        log.info("订单催单：{}", id);
        orderService.reminder(id);
        return Result.success();
    }
}
