package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    /**
     * 插入到购物车表
     */
    public void insert(ShoppingCart shoppingCart);

    /**
     * 查询符合条件的购物车数据
     * @param shoppingCart .
     * @return .
     */
    public List<ShoppingCart> getCartItem(ShoppingCart shoppingCart);

    /**
     * 更新购物车物品数量
     * @param shoppingCart .
     */
    public void updateNumberById(ShoppingCart shoppingCart);

    /**
     * 根据ID删除购物车商品
     * @param shoppingCart .
     */
    public void deleteById(ShoppingCart shoppingCart);

}
