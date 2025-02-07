package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {

    /**
     * 添加到购物车
     *
     * @param dto .
     */
    void addToCart(ShoppingCartDTO dto);

    /**
     * 查看购物车
     *
     * @return .
     */
    List<ShoppingCart> getCart();

    /**
     * 减少购物车菜品数量
     *
     * @param dto .
     */
    void subToCart(ShoppingCartDTO dto);

    /**
     * 清空购物车
     */
    void cleanCart();
}
