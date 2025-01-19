package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.page.PageMethod;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.utils.AliOssUtil;
import com.sky.utils.EmptyUtil;
import com.sky.vo.DishVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/*
 * @description: 菜品管理
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DishServiceImpl implements DishService {
    private final DishMapper dishMapper;

    private final DishFlavorMapper dishFlavorMapper;

    private final SetmealDishMapper setmealDishMapper;

    private final AliOssUtil aliOssUtil;


    /**
     * 新增菜品
     *
     * @param dishDTO .
     */
    @Transactional
    @Override
    public void insertDishWithFlavor(DishDTO dishDTO) {
        //插入菜品数据
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.insert(dish);

        //插入口味数据
        Long id = dish.getId();
        if (!EmptyUtil.listEmpty(dishDTO.getFlavors())) {
            dishFlavorMapper.insertBatches(dishDTO.getFlavors(), id);
        } else {
            log.warn("菜品口味为空");
        }

    }

    /**
     * 菜品分页查询
     *
     * @param dto 菜品分页查询参数
     * @return 分页结果
     */
    @Override
    public PageResult<DishVO> pageQuery(DishPageQueryDTO dto) {
        PageMethod.startPage(dto.getPage(), dto.getPageSize());
        Dish dish = new Dish();
        // 此处踩坑！DTO的categoryId为Long类型，而Dish中为Integer类型，BeanUtil无法复制
        BeanUtils.copyProperties(dto, dish);
        if (dto.getCategoryId() != null)
            dish.setCategoryId(Long.valueOf(dto.getCategoryId()));
        log.info("查询菜品信息：{}", dish);
        Page<DishVO> pages = dishMapper.queryPage(dish);
        return new PageResult<>(pages.getTotal(), pages.getResult());
    }

    /**
     * 批量删除菜品
     *
     * @param ids 菜品id
     */
    @Transactional
    @Override
    public void deleteBatches(List<Long> ids) {
        ArrayList<Dish> dishes = new ArrayList<>();

        //遍历要删除的菜品ID，判断是否能删除，若不能则一个都不删除直接抛出异常
        ids.forEach(id -> {
            Dish dish = dishMapper.getById(id);
            dishes.add(dish);
            //判断是否启售，启售的菜品不能删除
            if (Objects.equals(dish.getStatus(), StatusConstant.ENABLE)) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
            //判断菜品是否和套餐关联，若存在关联则不能删除
            if (setmealDishMapper.countSetMealDishByDishId(id) > 0) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
            }
        });
        //未抛出异常则正常执行以下删除1.删除菜品表数据2.删除口味表数据
        dishMapper.deleteBatches(ids);
        dishFlavorMapper.deleteBatches(ids);
        for (Dish dish : dishes) {
            int objectNameIndex = dish.getImage().lastIndexOf("img/");
            String objectName = dish.getImage().substring(objectNameIndex);
            aliOssUtil.delete(objectName);
        }
    }

    @Transactional
    @Override
    public void updateDishWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        //将属于Dish的属性拷贝到Dish对象中
        BeanUtils.copyProperties(dishDTO, dish);
        //获取原Dish信息
        Dish original = dishMapper.getById(dish.getId());
        //从原Dish取出原图片信息
        int index = original.getImage().lastIndexOf("img/");
        if (index != -1 && !original.getImage().equals(dish.getImage())) {
            //如果是zank的bucket那就删除，不是不处理，更新后图片不变，不处理
            String objectName = original.getImage().substring(index);
            //删除原图片
            aliOssUtil.delete(objectName);
        }
        //更新菜品信息
        dishMapper.update(dish);
        /*
        //获取原菜品口味ID
        List<Long> ids = dishFlavorMapper
                .getByDishId(dish.getId())
                .stream()
                .map(DishFlavor::getId)
                .collect(Collectors.toList());
        log.info("测试bug: {}",ids);
        //原来的口味数据全删掉
        if (!EmptyUtil.listEmpty(ids)) {
        }
        */

        //删除原口味
        dishFlavorMapper.deleteBatches(Collections.singletonList(dish.getId()));
        //新的口味数据有则添加
        if (!EmptyUtil.listEmpty(dishDTO.getFlavors())) {
            dishFlavorMapper.insertBatches(dishDTO.getFlavors(), dish.getId());
        }

    }

    /**
     * 根据ID查询菜品
     *
     * @param id 菜品ID
     * @return DishVO
     */
    public DishVO getByIdWithFlavor(Long id) {
        DishVO dish = new DishVO();
        DishVO vo = dishMapper.selectById(id);
        BeanUtils.copyProperties(vo, dish);
        dish.setFlavors(dishFlavorMapper.getByDishId(id));
        return dish;
    }

    /**
     * 启售停售菜品
     *
     * @param status .
     * @param id     .
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        Dish dish = Dish
                .builder()
                .id(id)
                .status(status)
                .build();
        dishMapper.update(dish);
    }

    /**
     * 根据分类ID或名称查询菜品
     *
     * @param categoryId 分类ID
     * @param name       菜品名称
     * @return dishes
     */
    @Override
    public List<Dish> list(Long categoryId, String name) {
        //菜品名称为空则返回该分类下的所有菜品，否则返回该分类下包含该名称的菜品
        Dish dish = Dish
                .builder()
                .categoryId(categoryId)
                .name(name)
                .status(StatusConstant.ENABLE)
                .build();
        return dishMapper.list(dish);
    }

    /**
     * 条件查询菜品和口味
     *
     * @param dish .
     * @return .
     */
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d, dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }
}

