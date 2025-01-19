package com.sky.service;

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
}
