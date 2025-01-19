package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.DishService;
import com.sky.service.SetMealService;
import com.sky.service.ShoppingCartService;
import com.sky.utils.EmptyUtil;
import com.sky.vo.DishVO;
import com.sky.vo.SetmealVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final ShoppingCartMapper shoppingCartMapper;

    private final SetMealService setMealService;

    private final DishService dishService;

    /**
     * 添加购物车
     *
     * @param dto .
     */
    @Override
    public void addToCart(ShoppingCartDTO dto) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(dto, shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());
        //首先要判断添加的菜品或套餐是否存在，套餐存在数量加1,菜品存在且口味相同数量加1
        List<ShoppingCart> item = shoppingCartMapper.getCartItem(shoppingCart);
        if (!EmptyUtil.listEmpty(item)) {
            ShoppingCart temp = item.get(0);
            temp.setNumber(temp.getNumber() + 1);
            shoppingCartMapper.updateNumberById(temp);
        }

        //套餐、菜品不存在，添加到购物车，数量默认为1
        else {
            if (Objects.nonNull(dto.getDishId())) {
                DishVO dish = dishService.getByIdWithFlavor(dto.getDishId());
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
            } else {
                SetmealVO setmealVO = setMealService.getByIdWithDish(dto.getSetmealId());
                shoppingCart.setName(setmealVO.getName());
                shoppingCart.setImage(setmealVO.getImage());
                shoppingCart.setAmount(setmealVO.getPrice());
            }
            //新添加的购物车菜品数量为1
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCart);
        }
    }

    /**
     * 查询购物车
     *
     * @return .
     */
    @Override
    public List<ShoppingCart> getCart() {
        ShoppingCart cart = new ShoppingCart();
        cart.setUserId(BaseContext.getCurrentId());
        return shoppingCartMapper.getCartItem(cart);
    }

    /**
     * 减少购物车菜品数量
     *
     * @param dto .
     */
    @Override
    public void subToCart(ShoppingCartDTO dto) {
        ShoppingCart cart = new ShoppingCart();
        BeanUtils.copyProperties(dto, cart);
        cart.setUserId(BaseContext.getCurrentId());
        ShoppingCart item = shoppingCartMapper.getCartItem(cart).get(0);

        if (Objects.equals(item.getNumber(), 1)) {
            //菜品数量为1，直接删除
            shoppingCartMapper.deleteById(item);
        } else {
            item.setNumber(item.getNumber() - 1);
            shoppingCartMapper.updateNumberById(item);
        }
    }

    /**
     * 清空购物车
     */
    @Override
    public void cleanCart() {
        ShoppingCart cart = new ShoppingCart();
        cart.setUserId(BaseContext.getCurrentId());
        shoppingCartMapper.deleteById(cart);
    }
}
