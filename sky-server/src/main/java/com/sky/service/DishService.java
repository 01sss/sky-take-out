package com.sky.service;

import com.sky.dto.DishDTO;
import org.springframework.stereotype.Service;

/**
 * @author: Tan
 * @createTime: 2024/08/23 21:12
 * @description: 新增菜品和对应的口味数据
 */
public interface DishService {
    public void saveWithFlavor(DishDTO dishDTO);
}
