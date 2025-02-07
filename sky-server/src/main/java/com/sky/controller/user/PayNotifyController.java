package com.sky.controller.user;

import com.sky.service.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notify")
@Tag(name = "用户端-支付回调接口")
@Slf4j
@RequiredArgsConstructor
public class PayNotifyController {

    private final OrderService orderService;

    /**
     * 支付成功回调
     *
     * @param request .
     * @return .
     */
    //TODO 参数封装
    @PostMapping
    public String payNotify(HttpServletRequest request) {
        orderService.tradeStatus(request.getParameter("trade_status"), request.getParameter("out_trade_no"));
        return "success";
    }
}
