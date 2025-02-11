package com.sky.service.impl;

import com.sky.constant.StatusConstant;
import com.sky.entity.Orders;
import com.sky.mapper.DishMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkspaceServiceImpl implements WorkspaceService {

    private final OrderMapper orderMapper;
    private final UserMapper userMapper;
    private final DishMapper dishMapper;
    private final SetmealMapper setmealMapper;

    /**
     * 根据时间段统计营业数据
     *
     * @param today 今日时间字符串
     * @return .
     */
    public BusinessDataVO getBusinessData(String today) {
/*
        营业额：当日已完成订单的总金额
        有效订单：当日已完成订单的数量
        订单完成率：有效订单数 / 总订单数
        平均客单价：营业额 / 有效订单数
        新增用户：当日新增用户的数量
*/

        Map<String, String> map = new HashMap<>();
        map.put("date", today);

        //查询总订单数
        Integer totalOrderCount = orderMapper.getOrderCount(map);

        //营业额
        Float turnover = orderMapper.getTurnover(today);
        turnover = turnover == null ? 0.0f : turnover;

        //有效订单数
        map.put("status", String.valueOf(Orders.COMPLETED));
        Integer validOrderCount = orderMapper.getOrderCount(map);

        double unitPrice = 0.0;

        double orderCompletionRate = 0.0;
        if (totalOrderCount != 0 && validOrderCount != 0) {
            //订单完成率
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
            //平均客单价
            unitPrice = turnover / validOrderCount;
        }

        //新增用户数
        Integer newUsers = userMapper.getNewUser(today);


        return BusinessDataVO.builder()
                .turnover(remain2Decimal(Double.valueOf(turnover)))
                .validOrderCount(validOrderCount)
                .orderCompletionRate(remain2Decimal(orderCompletionRate))
                .unitPrice(remain2Decimal(unitPrice))
                .newUsers(newUsers)
                .build();
    }


    /**
     * 查询订单管理数据
     *
     * @return .
     */
    public OrderOverViewVO getOrderOverView() {
        String today = LocalDate.now().toString();

        //待接单
        Integer waitingOrders = orderMapper.statusCount(Orders.TO_BE_CONFIRMED, today);

        //待派送
        Integer deliveredOrders = orderMapper.statusCount(Orders.CONFIRMED, today);

        //已完成
        Integer completedOrders = orderMapper.statusCount(Orders.COMPLETED, today);

        //已取消
        Integer cancelledOrders = orderMapper.statusCount(Orders.CANCELLED, today);

        //全部订单
        Integer allOrders = orderMapper.statusCount(null, today);

        return OrderOverViewVO.builder()
                .waitingOrders(waitingOrders)
                .deliveredOrders(deliveredOrders)
                .completedOrders(completedOrders)
                .cancelledOrders(cancelledOrders)
                .allOrders(allOrders)
                .build();
    }

    /**
     * 查询菜品总览
     *
     * @return .
     */
    public DishOverViewVO getDishOverView() {
        Integer sold = dishMapper.getDishCount(StatusConstant.ENABLE,null);
        Integer discontinued = dishMapper.getDishCount(StatusConstant.DISABLE, null);

        return DishOverViewVO.builder()
                .sold(sold)
                .discontinued(discontinued)
                .build();
    }

    /**
     * 查询套餐总览
     *
     * @return .
     */
    public SetmealOverViewVO getSetmealOverView() {
        Integer sold = setmealMapper.getSetmealCount(StatusConstant.ENABLE, null);
        Integer discontinued = setmealMapper.getSetmealCount(StatusConstant.DISABLE, null);

        return SetmealOverViewVO.builder()
                .sold(sold)
                .discontinued(discontinued)
                .build();
    }

    public double remain2Decimal(double num) {
        return  (double) Math.round(num * 100) / 100;
    }
}
