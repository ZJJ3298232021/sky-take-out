package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.page.PageMethod;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.utils.AliOssUtil;
import com.sky.utils.EmptyUtil;
import com.sky.vo.DishVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/*
 * @description: 菜品管理
 */
@Service
public class DishServiceImpl implements DishService {
    private static final Logger log = LoggerFactory.getLogger(DishServiceImpl.class);
    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Autowired
    private AliOssUtil aliOssUtil;


    /**
     * 新增菜品
     *
     * @param dishDTO
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
     * @param ids 菜品id
     */
    @Transactional
    @Override
    public void deleteBatches(List<Long> ids) {
        ArrayList<Dish> dishes  = new ArrayList<>();

        //遍历要删除的菜品ID，判断是否能删除，若不能则一个都不删除直接抛出异常
        ids.forEach(id->{
            Dish dish = dishMapper.getById(id);
            dishes.add(dish);
            //判断是否启售，启售的菜品不能删除
            if (Objects.equals(dish.getStatus(), StatusConstant.ENABLE)) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
            //判断菜品是否和套餐关联，若存在关联则不能删除
            if(setmealDishMapper.getSetmealDishByDishId(id) > 0) {
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
}
