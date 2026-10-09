package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;

@Mapper
public interface WorkspaceMapper {

    /**
     * 根据菜品状态统计菜品数量
     */
    @Select("select count(id) from dish where status = #{status}")
    Integer countDishByStatus(Integer status);

    /**
     * 根据套餐状态统计套餐数量
     */
    @Select("select count(id) from setmeal where status = #{status}")
    Integer countSetmealByStatus(Integer status);

    /**
     * 根据订单状态统计订单数量
     */
    @Select("select count(id) from orders where status = #{status}")
    Integer countOrderByStatus(Integer status);

    /**
     * 统计订单总数
     */
    @Select("select count(id) from orders")
    Integer countAllOrders();

    /**
     * 统计指定时间段内的有效订单数（状态为已完成）
     */
    @Select("select count(id) from orders where status = 5 and order_time between #{begin} and #{end}")
    Integer countValidOrdersByTime(@Param("begin") LocalDateTime begin, @Param("end") LocalDateTime end);

    /**
     * 统计指定时间段内的订单总数
     */
    @Select("select count(id) from orders where order_time between #{begin} and #{end}")
    Integer countOrdersByTime(@Param("begin") LocalDateTime begin, @Param("end") LocalDateTime end);

    /**
     * 统计指定时间段内的营业额（状态为已完成的订单金额之和）
     */
    @Select("select sum(amount) from orders where status = 5 and order_time between #{begin} and #{end}")
    Double sumTurnoverByTime(@Param("begin") LocalDateTime begin, @Param("end") LocalDateTime end);

    /**
     * 统计指定时间段内的新增用户数
     */
    @Select("select count(id) from user where create_time between #{begin} and #{end}")
    Integer countNewUsersByTime(@Param("begin") LocalDateTime begin, @Param("end") LocalDateTime end);
}
