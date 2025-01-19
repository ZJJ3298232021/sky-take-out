package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     * 根据菜品id查询菜品是否关联了套餐
     *
     * @param id .
     * @return .
     */
    @Select("select count(0) from setmeal_dish where dish_id = #{id}")
    int countSetMealDishByDishId(Long id);

    /**
     * 批量插入套餐菜品关系数据
     *
     * @param setmealDishes .
     */
    void insertBatches(List<SetmealDish> setmealDishes);

    /**
     * 根据套餐id查询套餐菜品
     *
     * @param id 套餐ID
     * @return .
     */
    @Select("select " +
            "id, setmeal_id, dish_id, name, price, copies" +
            " from setmeal_dish where setmeal_id = #{id}")
    List<SetmealDish> getDishesBySetmealId(Long id);

    /**
     * 根据多套餐id删除套餐菜品关联数据
     *
     * @param ids .
     */
    void deleteBatchesBySetmealIds(List<Long> ids);
}
