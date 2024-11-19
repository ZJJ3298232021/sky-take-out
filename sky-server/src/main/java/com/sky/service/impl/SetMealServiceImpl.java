package com.sky.service.impl;

import com.sky.dto.SetmealDTO;
import com.sky.entity.Setmeal;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.service.SetMealService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class SetMealServiceImpl implements SetMealService {
    private final SetmealMapper setmealMapper;
    private final SetmealDishMapper setmealDishMapper;

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
}
