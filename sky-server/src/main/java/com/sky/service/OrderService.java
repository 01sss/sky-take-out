package com.sky.service;

import com.sky.dto.OrdersSubmitDTO;
import com.sky.vo.OrderSubmitVO;

/**
 * @author: Tan
 * @createTime: 2024/09/19 9:35
 * @description:
 */
public interface OrderService {

    /**
     * 用户订单提交
     * @param ordersSubmitDTO
     * @return
     */
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);
}
