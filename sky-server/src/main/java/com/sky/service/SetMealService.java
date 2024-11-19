package com.sky.service;

import com.sky.dto.SetmealDTO;

/**
 * 套餐业务
 */
public interface SetMealService {
    /**
     * 新增套餐
     */
    void saveWithDish(SetmealDTO setmealDTO);


}
