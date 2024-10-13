package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     *
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /*
     * 新增菜品，同时插入菜品对应的口味数据
     */
    @AutoFill(value = OperationType.INSERT)
    void insert(@Param("dish") Dish dish);

    /**
     * 分页查询
     * @param dish 查询条件
     * @return 菜品分页对象
     */
    Page<DishVO> queryPage(Dish dish);

    /**
     * 根据id查询菜品和对应的口味信息
     * @param id
     * @return
     */
    @Select("select * from dish where id = #{id}")
    Dish getById(Long id);

    /**
     * 批量删除菜品
     * @param ids
     */
    void deleteBatches(List<Long> ids);
}
