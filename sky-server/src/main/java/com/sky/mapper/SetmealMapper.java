package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealMapper {

    /**
     * 根据分类id查询套餐的数量
     *
     * @param id .
     * @return .
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    /**
     * 新增套餐
     *
     * @param setmeal .
     */
    @AutoFill(value = OperationType.INSERT)
    void save(Setmeal setmeal);

    /**
     * 根据id查询套餐和其分类名称
     *
     * @param ids .
     * @return .
     */
    List<SetmealVO> getByIdWithCategoryName(List<Long> ids);

    /**
     * 分页查询
     *
     * @param pageQuery .
     * @return .
     */
    Page<SetmealVO> pageQuery(SetmealPageQueryDTO pageQuery);

    /**
     * 修改套餐
     *
     * @param setmeal .
     */
    @AutoFill(OperationType.UPDATE)
    void update(Setmeal setmeal);

    /**
     * 批量删除套餐
     *
     * @param ids .
     */
    void deleteBatches(List<Long> ids);


    /**
     * 动态条件查询套餐
     *
     * @param setmeal .
     * @return .
     */
    List<Setmeal> list(Setmeal setmeal);

    /**
     * 根据套餐id查询菜品选项
     *
     * @param setmealId .
     * @return .
     */
    @Select("select sd.name, sd.copies, d.image, d.description " +
            "from setmeal_dish sd left join dish d on sd.dish_id = d.id " +
            "where sd.setmeal_id = #{setmealId}")
    List<DishItemVO> getDishItemBySetmealId(Long setmealId);

    /**
     * 根据条件统计套餐数量
     * @param status .
     * @param categoryId .
     * @return .
     */
    Integer getSetmealCount(Integer status, Long categoryId);
}
