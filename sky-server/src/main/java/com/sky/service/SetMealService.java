package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;

/**
 * 套餐业务
 */
public interface SetMealService {
    /**
     * 新增套餐
     */
    void saveWithDish(SetmealDTO setmealDTO);

    /**
     * 根据id查询套餐和其菜品
     * @param id
     * @return
     */
    SetmealVO getByIdWithDish(Long id);

    /**
     * 分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    PageResult<SetmealVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 启用禁用套餐
     * @param status
     * @param id
     */
    void startOrStop(Integer status, Long id);
}
