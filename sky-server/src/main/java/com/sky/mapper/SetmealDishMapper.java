package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SetmealDishMapper {

    /**
     * 根据菜品id查询菜品是否关联了套餐
     * @param id
     * @return
     */
    @Select("select count(0) from setmeal_dish where dish_id = #{id}")
    int getSetmealDishByDishId(Long id);
}
