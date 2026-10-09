package com.sky.service;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {
    OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 订单支付（模拟支付：调用后直接支付成功）
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     */
    void paySuccess(String outTradeNo);

    /**
     * 用户端订单分页查询
     */
    PageResult pageQuery4User(int pageNum, int pageSize, Integer status);

    /**
     * 查询订单详情
     */
    OrderVO details(Long id);

    /**
     * 用户取消订单
     */
    void userCancelById(Long id) throws Exception;

    /**
     * 再来一单
     */
    void repetition(Long id);

    /**
     * 客户催单
     */
    void reminder(Long id);

    /**
     * 订单搜索（管理端）
     */
    PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 各个状态的订单数量统计（管理端）
     */
    OrderStatisticsVO statistics();

    /**
     * 接单（管理端）
     */
    void confirm(OrdersConfirmDTO ordersConfirmDTO);

    /**
     * 拒单（管理端）
     */
    void rejection(OrdersRejectionDTO ordersRejectionDTO) throws Exception;

    /**
     * 取消订单（管理端）
     */
    void cancel(OrdersCancelDTO ordersCancelDTO) throws Exception;

    /**
     * 派送订单（管理端）
     */
    void delivery(Long id);

    /**
     * 完成订单（管理端）
     */
    void complete(Long id);
}
