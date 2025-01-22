package com.sky.controller.user;

import com.sky.constant.PathConstant;
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

import java.io.IOException;

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
     * @param paymentDTO .
     * @return .
     * @throws IOException .
     */
    @PutMapping("/payment")
    @Operation(description = "支付订单")
    public Result<?> pay(@RequestBody OrdersPaymentDTO paymentDTO) {
        log.info("用户支付订单：{}", paymentDTO);
        return Result.success(orderService.pay(paymentDTO));
    }

    /**
     * 查询订单支付状态
     * @param orderId .
     * @return .
     */
    @GetMapping("/checkStatus")
    public Result<Integer> checkStatus(Long orderId) {
        log.info("查询订单支付状态：{}", orderId);
        Integer payStatus = orderService.getPayStatus(orderId);
        return Result.success(payStatus);
    }

    @GetMapping("/historyOrders")
    @Operation(description = "查询历史订单")
    public Result<PageResult<OrderVO>> historyOrders(OrdersPageQueryDTO dto) {
        log.info("查询历史订单：{}", dto);
        PageResult<OrderVO> pageResult = orderService.historyOrders(dto);
        return Result.success(pageResult);
    }
}
