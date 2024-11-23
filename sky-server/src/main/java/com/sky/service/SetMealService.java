package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;

import java.util.List;

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

    /**
     * 修改套餐
     * @param setmealDTO
     */
    void updateSetMealWithDish(SetmealDTO setmealDTO);

    /**
     * 批量删除套餐
     * @param ids
     */
    void deleteBatchSetMeals(List<Long> ids);

        /**
     * 条件查询
     * @param setmeal
     * @return
     */
    List<Setmeal> list(Setmeal setmeal);

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    List<DishItemVO> getDishItemById(Long id);
}
