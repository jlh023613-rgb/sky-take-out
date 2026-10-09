package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;


@Component
@Slf4j
public class OrderTask {
    @Autowired
    private OrderMapper orderMapper;
    //处理超时订单 每分钟检查一次
    @Scheduled(cron = "0 * * * * ?")
    public void processTimeoutOrders() {
        log.info("处理超时订单...");
        //先查有么有超时订单 当前时间减去15分钟就是应该超时的时间
        LocalDateTime outTime = LocalDateTime.now().plusMinutes(-15);
        List<Orders> orders = orderMapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT,outTime);
        if (orders != null && orders.size() > 0){
            for (Orders order : orders) {
                //处理超时订
                order.setStatus(Orders.CANCELLED);
                order.setCancelReason("订单超时未支付，系统自动取消");
                order.setCancelTime(LocalDateTime.now());
                orderMapper.update(order);
            }
        }
    }

    //处理一直处于派送中的订单 每天凌晨一点检查
    @Scheduled(cron = "0 0 1 * * ?")
    public void processDeliveryOrders() {
        log.info("处理一直处于派送中的订单...");
        //先查有么有一直处于派送中的订单
        List<Orders> orders = orderMapper.getByStatus(Orders.DELIVERY_IN_PROGRESS);
        if (orders != null && orders.size() > 0){
            for (Orders order : orders) {
                //处理一直处于派送中的订单
                order.setStatus(Orders.COMPLETED);
                orderMapper.update(order);
            }
        }
    }

}
