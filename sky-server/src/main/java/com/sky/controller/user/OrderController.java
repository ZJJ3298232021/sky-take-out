package com.sky.controller.user;

import com.sky.constant.PathConstant;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
