package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

/**
 * @author: Tan
 * @createTime: 2024/09/18 14:42
 * @description:
 */
public interface ShoppingCartService {

    /**
     * 添加购物车方法
     * @param shoppingCartDTO
     */
    void addShoppingCart(ShoppingCartDTO shoppingCartDTO);

    /**
     * 查看购物车方法
     * @return
     */
    List<ShoppingCart> showShoppingCart();

    /**
     * 清空购物车
     */
    void cleanShoppingCart();


    /**
     * 删除购物车中一个商品
     * @param shoppingCartDTO
     */
    void subShoppingCart(ShoppingCartDTO shoppingCartDTO);
}
