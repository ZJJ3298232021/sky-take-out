package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
    /**
     * 批量插入口味数据
     *
     * @param flavors 口味数据
     * @param dishId  菜品id
     */
    void insertBatches(@Param("flavors") List<DishFlavor> flavors, @Param("dishId") Long dishId);

    /**
     * 根据多菜品id删除口味数据
     *
     * @param ids 菜品口味id
     */
    void deleteBatches(List<Long> ids);

    /**
     * 根据菜品id查询口味数据
     *
     * @param dishId 菜品id
     * @return List<DishFlavor>
     */
    @Select("select * from dish_flavor where dish_id = #{id}")
    List<DishFlavor> getByDishId(Long dishId);
}
