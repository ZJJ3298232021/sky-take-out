package com.sky.service;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

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
    Integer tradeStatus(String tradeStatus, String outTradeNo);

    /**
     * 查询订单支付状态
     * @param orderId .
     */
    Integer getPayStatus(Long orderId);

    /**
     * 历史订单分页查询
     * @param dto .
     * @return .
     */
    PageResult<OrderVO> historyOrders(OrdersPageQueryDTO dto);

    /**
     * 订单详情
     * @param id 订单ID
     * @return .
     */
    OrderVO orderDetail(Long id);

    /**
     * 取消订单
     * @param id .
     */
    void cancelOrder(Long id);
}
