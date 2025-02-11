package com.sky.service;

import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;

public interface WorkspaceService {

    /**
     * 根据时间段统计营业数据
     * @param today 今日时间字符串
     * @return .
     */
    BusinessDataVO getBusinessData(String today);

    /**
     * 查询订单管理数据
     * @return .
     */
    OrderOverViewVO getOrderOverView();

    /**
     * 查询菜品总览
     * @return .
     */
    DishOverViewVO getDishOverView();

    /**
     * 查询套餐总览
     * @return .
     */
    SetmealOverViewVO getSetmealOverView();

}
