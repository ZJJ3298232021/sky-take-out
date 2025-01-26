package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    /**
     * 插入到购物车表
     */
    void insertBatch(List<ShoppingCart> items);

    /**
     * 查询符合条件的购物车数据
     *
     * @param shoppingCart .
     * @return .
     */
    List<ShoppingCart> getCartItem(ShoppingCart shoppingCart);

    /**
     * 更新购物车物品数量
     *
     * @param shoppingCart .
     */
    void updateNumberById(ShoppingCart shoppingCart);

    /**
     * 根据ID删除购物车商品
     *
     * @param shoppingCart .
     */
    void deleteById(ShoppingCart shoppingCart);

    /**
     * 根据用户ID删除购物车
     *
     * @param userId .
     */
    @Delete("delete from shopping_cart where user_id = #{userId}")
    void deleteByUserId(Long userId);

}
