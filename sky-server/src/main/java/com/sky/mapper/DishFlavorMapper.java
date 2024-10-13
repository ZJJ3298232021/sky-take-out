package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface DishFlavorMapper {
    /**
     * 批量插入口味数据
     * @param flavors 口味数据
     * @param dishId 菜品id
     */
    void insertBatches(@Param("flavors") List<DishFlavor> flavors, @Param("dishId") Long dishId);

    /**
     * 根据菜品id删除口味数据
     * @param ids
     */
    void deleteBatches(List<Long> ids);
}
