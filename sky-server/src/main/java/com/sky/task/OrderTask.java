package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author: Tan
 * @createTime: 2024/09/23 11:09
 * @description: 自定义定时任务类: 定时处理订单状态
 */

@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 处理超时订单
     */
    @Scheduled(cron = "0 0/1 * * * ?") // 每分钟触发一次
    public void processTimeOutOrder(){
        log.info("定时处理超时订单：{}", LocalDateTime.now());

        LocalDateTime orderTime = LocalDateTime.now().plusMinutes(-15);

        // 查询订单表中处于待支付状态的订单，且订单时间要小于当前时间-15分钟
        // select * from orders where status = ? and order_time < (当前时间 - 15分钟)
        List<Orders> ordersList = orderMapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT, orderTime);

        // 遍历符合条件的订单，将订单状态修改为已取消
        if(ordersList != null && ordersList.size() > 0){
            for(Orders orders : ordersList){
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason("订单超时，自动取消");
                orders.setCancelTime(LocalDateTime.now());
                orderMapper.update(orders);
            }
        }

    }


    /**
     * 处理一直处于派送中状态的订单
     */
    @Scheduled(cron = "0 0 1 * * ? ") // 每天凌晨一点触发一次
    public void processDeliveryOrder(){
        log.info("定时处理一直处于派送中的订单：{}", LocalDateTime.now());

        LocalDateTime orderTime = LocalDateTime.now().plusHours(-1);

        // select * form orders where status = Orders.DELIVERY_IN_PROGRESS
        List<Orders> ordersList = orderMapper.getByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS, orderTime);

        // 筛选出符合条件的订单，并修改它们的状态为已完成
        if(ordersList != null && ordersList.size() > 0){
            for(Orders orders : ordersList){
                orders.setStatus(Orders.COMPLETED);
                orderMapper.update(orders);
            }

        }

    }



}
