package com.sky.service.impl;

import com.sky.constant.StatusConstant;
import com.sky.entity.Orders;
import com.sky.mapper.WorkspaceMapper;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class WorkspaceServiceImpl implements WorkspaceService {

    @Autowired
    private WorkspaceMapper workspaceMapper;

    /**
     * 根据时间段统计营业数据
     */
    @Override
    public BusinessDataVO getBusinessData(LocalDateTime begin, LocalDateTime end) {
        //新增用户数
        Integer newUsers = workspaceMapper.countNewUsersByTime(begin, end);

        //有效订单数
        Integer validOrderCount = workspaceMapper.countValidOrdersByTime(begin, end);

        //订单总数
        Integer totalOrderCount = workspaceMapper.countOrdersByTime(begin, end);

        //营业额
        Double turnover = workspaceMapper.sumTurnoverByTime(begin, end);
        turnover = turnover == null ? 0.0 : turnover;

        Double unitPrice = 0.0;
        Double orderCompletionRate = 0.0;

        if (totalOrderCount != null && totalOrderCount != 0 && validOrderCount != null) {
            //订单完成率
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
        }
        if (validOrderCount != null && validOrderCount != 0) {
            //平均客单价
            unitPrice = turnover / validOrderCount;
        }

        return BusinessDataVO.builder()
                .turnover(turnover)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .unitPrice(unitPrice)
                .newUsers(newUsers)
                .build();
    }

    /**
     * 查询订单管理数据
     */
    @Override
    public OrderOverViewVO getOrderOverView() {
        return OrderOverViewVO.builder()
                .waitingOrders(workspaceMapper.countOrderByStatus(Orders.TO_BE_CONFIRMED))
                .deliveredOrders(workspaceMapper.countOrderByStatus(Orders.DELIVERY_IN_PROGRESS))
                .completedOrders(workspaceMapper.countOrderByStatus(Orders.COMPLETED))
                .cancelledOrders(workspaceMapper.countOrderByStatus(Orders.CANCELLED))
                .allOrders(workspaceMapper.countAllOrders())
                .build();
    }

    /**
     * 查询菜品总览
     */
    @Override
    public DishOverViewVO getDishOverView() {
        return DishOverViewVO.builder()
                .sold(workspaceMapper.countDishByStatus(StatusConstant.ENABLE))
                .discontinued(workspaceMapper.countDishByStatus(StatusConstant.DISABLE))
                .build();
    }

    /**
     * 查询套餐总览
     */
    @Override
    public SetmealOverViewVO getSetmealOverView() {
        return SetmealOverViewVO.builder()
                .sold(workspaceMapper.countSetmealByStatus(StatusConstant.ENABLE))
                .discontinued(workspaceMapper.countSetmealByStatus(StatusConstant.DISABLE))
                .build();
    }
}
