package com.sky.service;

import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.vo.OrderSubmitVO;

public interface OrderService {

    /**
     * 提交用户订单
     *
     * @param dto .
     * @return .
     */
    public OrderSubmitVO submitOrder(OrdersSubmitDTO dto);

    /**
     * 支付订单
     *
     * @param paymentDTO .
     * @return .
     */
    String pay(OrdersPaymentDTO paymentDTO);

    /**
     *交易最后状态，判断是否成功
     *
     * @param tradeStatus .
     */
    void tradeStatus(String tradeStatus, String outTradeNo);

    /**
     * 查询订单支付状态
     * @param orderId .
     */
    Integer getPayStatus(Long orderId);
}
