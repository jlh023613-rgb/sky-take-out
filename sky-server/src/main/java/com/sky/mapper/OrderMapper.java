package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {

    void insert(Orders orders);

    /**
     * 根据订单id查询订单
     */
    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);

    /**
     * 根据订单号查询订单
     */
    @Select("select * from orders where number = #{number}")
    Orders getByNumber(String number);

    /**
     * 更新订单信息（动态SQL，见 OrderMapper.xml）
     */
    void update(Orders orders);

    @Select("select * from orders where status = #{pendingPayment} and order_time < #{outTime}")
    List<Orders> getByStatusAndOrderTimeLT(Integer pendingPayment, LocalDateTime outTime);

    /**
     * 分页条件查询订单（动态SQL，见 OrderMapper.xml）
     */
    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 根据状态统计订单数量
     */
    @Select("select count(id) from orders where status = #{status}")
    Integer countStatus(Integer status);

    @Select("select * from orders where status = #{status}")
    List<Orders> getByStatus(Integer status);

    @Select("select count(*) from orders where order_time >= #{beginTime} and order_time <= #{endTime}")
    Integer getOrderCount(LocalDateTime beginTime, LocalDateTime endTime);

    @Select("select count(*) from orders where order_time >= #{beginTime} and order_time <= #{endTime} and status = 5")
    Integer getValidOrderCount(LocalDateTime beginTime, LocalDateTime endTime);

    List<GoodsSalesDTO> getGoodsSalesTop10(LocalDateTime begin, LocalDateTime end);
}
