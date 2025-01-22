package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderMapper {

    /**
     * 插入订单数据,得到生成ID
     *
     * @param orders .
     */
    void insert(Orders orders);

    /**
     * 查询订单(有参数皆为条件）
     * @param orders .
     * @return .
     */
    List<Orders> getOrders(Orders orders);

    /**
     * 更新订单信息
     * @param order .
     */
    void update(Orders order);

    /**
     * 分页查询订单
     * @param orders .
     * @return .
     */
    Page<Orders> pageQuery(OrdersPageQueryDTO orders);
}
