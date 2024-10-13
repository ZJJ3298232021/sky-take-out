package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
    /**
     * 新增菜品
     * @param dish
     */
    void insertDishWithFlavor(DishDTO dish);

    /**
     * 菜品分页查询
     * @param dto 菜品分页查询参数
     * @return
     */
    PageResult<DishVO> pageQuery(DishPageQueryDTO dto);

    /**
     * 批量删除菜品
     * @param ids 菜品id
     */
    void deleteBatches(List<Long> ids);
}
