package com.sky.mapper;

import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;


/**
 * @author: Tan
 * @createTime: 2024/09/19 9:42
 * @description:
 */
@Mapper
public interface OrderMapper {
    /**
     * 插入单条订单数据
     * @param orders
     */
    void insert(Orders orders);
}
