package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetMealService;
import com.sky.utils.EmptyUtil;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
@Slf4j
public class SetMealServiceImpl implements SetMealService {
    private final SetmealMapper setmealMapper;
    private final SetmealDishMapper setmealDishMapper;

    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     *
     * @param setmealDTO .
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
     *
     * @param id .
     * @return .
     */
    @Override
    public SetmealVO getByIdWithDish(Long id) {
        //查询套餐数据,使用连接查询得到分类名称
        List<SetmealVO> setmealVO = setmealMapper.getByIdWithCategoryName(Collections.singletonList(id));
        SetmealVO setmeal = setmealVO.get(0);
        //查询套餐中的菜品
        List<SetmealDish> setmealDishes = setmealDishMapper.getDishesBySetmealId(id);
        setmeal.setSetmealDishes(setmealDishes);
        return setmeal;
    }

    /**
     * 分页查询套餐
     *
     * @param setmealPageQueryDTO .
     * @return .
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
     *
     * @param status .
     * @param id     .
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        Setmeal setmeal = Setmeal
                .builder()
                .id(id)
                .status(status)
                .build();
        //判断套餐中的菜品是否都处于起售状态
        List<Integer> allDishStatus = setmealDishMapper.getAllDishStatusLinkBySetmealId(id);
        if(!allDishStatus.stream().allMatch(dishStatus-> dishStatus.equals(StatusConstant.ENABLE))) {
            throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
        }
        setmealMapper.update(setmeal);
    }

    /**
     * 修改套餐
     *
     * @param setmealDTO .
     */
    @Transactional
    @Override
    public void updateSetMealWithDish(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        //更新套餐的基本信息
        setmealMapper.update(setmeal);
        //删除套餐中的菜品信息
        setmealDishMapper.deleteBatchesBySetmealIds(Collections.singletonList(setmealDTO.getId()));
        if (!EmptyUtil.listEmpty(setmealDTO.getSetmealDishes())) {
            //添加新的菜品信息
            setmealDTO.getSetmealDishes().forEach(dish -> dish.setSetmealId(setmealDTO.getId()));
            setmealDishMapper.insertBatches(setmealDTO.getSetmealDishes());
        }
    }

    @Transactional
    @Override
    public void deleteBatchSetMeals(List<Long> ids) {
        // 实际上不应该出现这种问题的，由于前端这个点没有做好所以需要判断一下，不然会出现code500
        if (EmptyUtil.listEmpty(ids)) return;
        //判断套餐是否起售，起售的套餐不能删除
        setmealMapper.getByIdWithCategoryName(ids).forEach(setmealVO -> {
            //起售中的套餐不能删除
            if (Objects.equals(setmealVO.getStatus(), StatusConstant.ENABLE)) {
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        });
        //删除套餐中的菜品信息
        setmealDishMapper.deleteBatchesBySetmealIds(ids);
        //批量删除套餐
        setmealMapper.deleteBatches(ids);
    }


    /**
     * 条件查询
     *
     * @param setmeal .
     * @return .
     */
    @Override
    public List<Setmeal> list(Setmeal setmeal) {
        return setmealMapper.list(setmeal);
    }

    /**
     * 根据id查询菜品选项
     *
     * @param id .
     * @return .
     */
    @Override
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }
}
