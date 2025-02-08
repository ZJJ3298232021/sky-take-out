package com.sky.service;

import com.sky.dto.*;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {

    /**
     * 提交用户订单
     *
     * @param dto .
     * @return .
     */
    OrderSubmitVO submitOrder(OrdersSubmitDTO dto);

    /**
     * 支付订单
     *
     * @param paymentDTO .
     * @return .
     */
    String pay(OrdersPaymentDTO paymentDTO);

    /**
     * 交易最后状态，判断是否成功
     *
     * @param tradeStatus .
     */
    Integer tradeStatus(String tradeStatus, String outTradeNo);

    /**
     * 查询订单支付状态
     *
     * @param orderId .
     */
    Integer getPayStatus(Long orderId);

    /**
     * 历史订单分页查询
     *
     * @param dto .
     * @return .
     */
    PageResult<OrderVO> historyOrders(OrdersPageQueryDTO dto);

    /**
     * 订单详情
     *
     * @param id 订单ID
     * @return .
     */
    OrderVO orderDetail(Long id);

    /**
     * 取消订单
     *
     * @param dto .
     */
    void cancelOrder(OrdersCancelDTO dto);

    /**
     * 再来一单
     *
     * @param id 订单ID
     */
    void oneMoreOrder(Long id);

    /**
     * 条件搜索订单
     *
     * @param dto .
     * @return .
     */
    PageResult<Orders> conditionSearch(OrdersPageQueryDTO dto);

    /**
     * 统计订单数据
     *
     * @return .
     */
    OrderStatisticsVO statistics();

    /**
     * 接单
     *
     * @param id .
     */
    void confirmOrder(Long id);

    /**
     * 拒单
     *
     * @param dto .
     */
    void rejectOrder(OrdersRejectionDTO dto);

    /**
     * 派送订单
     *
     * @param id .
     */
    void deliverOrder(Long id);

    /**
     * 完成订单
     *
     * @param id .
     */
    void completeOrder(Long id);

    /**
     * 催单
     * @param id .
     */
    void reminder(Long id);
}
