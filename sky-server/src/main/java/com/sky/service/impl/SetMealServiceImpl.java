package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetMealService;
import com.sky.vo.SetmealVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class SetMealServiceImpl implements SetMealService {
    private final SetmealMapper setmealMapper;
    private final SetmealDishMapper setmealDishMapper;
    private final CategoryMapper categoryMapper;

    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     *
     * @param setmealDTO
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        //这里先保存套餐，再保存套餐和菜品的关联关系
        setmealMapper.save(setmeal);
        //因为前端暂时没有套餐的ID，但是后面要保存套餐和菜品的关联关系，所以先保存套餐，再获取套餐的ID
        Long setmealId = setmeal.getId();
        setmealDTO.getSetmealDishes().forEach(dish -> dish.setSetmealId(setmealId));
        //批量插入套餐和菜品的关联关系
        setmealDishMapper.insertBatches(setmealDTO.getSetmealDishes());

    }

    /**
     * 根据套餐id查询套餐数据
     * @param id
     * @return
     */
    @Override
    public SetmealVO getByIdWithDish(Long id) {
        //查询套餐数据,使用连接查询得到分类名称
        SetmealVO setmealVO = setmealMapper.getByIdWithCategoryName(id);
        //查询套餐中的菜品
        List<SetmealDish> setmealDishes = setmealDishMapper.getDishesBySetmealId(id);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    /**
     * 分页查询套餐
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult<SetmealVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        try (Page<SetmealVO> setmealPageResult = setmealMapper.pageQuery(setmealPageQueryDTO)) {
            return new PageResult<>(setmealPageResult.getTotal(), setmealPageResult.getResult());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 启用禁用套餐
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        Setmeal setmeal = Setmeal
                .builder()
                .id(id)
                .status(status)
                .build();
        setmealMapper.update(setmeal);
    }
}
