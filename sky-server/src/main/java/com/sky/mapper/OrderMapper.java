package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

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
     *
     * @param orders .
     * @return .
     */
    List<Orders> getOrders(Orders orders);

    /**
     * 更新订单信息
     *
     * @param order .
     */
    void update(Orders order);

    /**
     * 分页查询订单
     *
     * @param orders .
     * @return .
     */
    Page<Orders> pageQuery(OrdersPageQueryDTO orders);

    /**
     * 统计订单状态数量
     *
     * @param status 订单状态
     * @return .
     */
    Integer statusCount(Integer status, String date);

    /**
     * 得到超时订单
     */
    List<Orders> getTimeOutOrders();

    /**
     * 根据订单状态查询订单
     * @param status .
     * @return .
     */
    List<Orders> getOrdersByStatus(Integer status);

    /**
     * 根据日期统计营业额
     * @param date .
     * @return .
     */
    Float getTurnover(String date);

    /**
     * 根据日期统计当日总订单数
     * @param condition .
     * @return .
     */
    Integer getOrderCount(Map<?,?> condition);

    /**
     * 统计销量排名前十
     * @param begin 开始时间
     * @param end 结束时间
     * @return .
     */
    List<GoodsSalesDTO> getTop10Dishes(String begin, String end);
}
