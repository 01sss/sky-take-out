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
}
