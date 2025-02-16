package com.sky.task;

import com.sky.constant.MessageConstant;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.utils.EmptyUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderStatusTask {

    private final OrderMapper orderMapper;

    /**
     * 处理超时订单
     */
    @Scheduled(cron = "0 * * * * ?")
    public void timeOutOrder(){
        log.info("处理超时订单");

        // 查询超时订单
        List<Orders> timeOutOrders = orderMapper.getTimeOutOrders();

        if (!EmptyUtil.listEmpty(timeOutOrders)){
            timeOutOrders.forEach(order -> {
                order.setStatus(Orders.CANCELLED);
                order.setCancelTime(LocalDateTime.now());
                order.setCancelReason(MessageConstant.ORDER_OUT_OF_TIME);
                orderMapper.update(order);
            });
        }
    }

    /**
     * 处理已完成订单
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void finishedOrder(){
        log.info("检查已完成订单");

        // 查询处于派送中的订单
        List<Orders> orders = orderMapper.getOrdersByStatus(Orders.DELIVERY_IN_PROGRESS);

        // 将长时间处于派送中的订单设置为已完成
        if (!EmptyUtil.listEmpty(orders)){
            orders.forEach(o -> {
                o.setStatus(Orders.COMPLETED);
                o.setDeliveryTime(LocalDateTime.now());
                orderMapper.update(o);
            });
        }
    }
}
