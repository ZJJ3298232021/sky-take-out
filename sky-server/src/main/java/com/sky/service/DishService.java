package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
    /**
     * 新增菜品
     *
     * @param dish
     */
    void insertDishWithFlavor(DishDTO dish);

    /**
     * 菜品分页查询
     *
     * @param dto 菜品分页查询参数
     * @return
     */
    PageResult<DishVO> pageQuery(DishPageQueryDTO dto);

    /**
     * 批量删除菜品
     *
     * @param ids 菜品id
     */
    void deleteBatches(List<Long> ids);

    void updateDishWithFlavor(DishDTO dishDTO);

    /**
     * 根据id查询菜品
     *
     * @param id
     * @return
     */
    DishVO getByIdWithFlavor(Long id);

    /**
     * 启用禁用菜品
     *
     * @param status
     * @param id
     */
    void startOrStop(Integer status, Long id);

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId
     * @return
     */
    List<Dish> list(Long categoryId, String name);

    /**
     * 条件查询菜品和口味
     *
     * @param dish
     * @return
     */
    List<DishVO> listWithFlavor(Dish dish);
}
