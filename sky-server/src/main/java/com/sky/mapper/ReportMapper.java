package com.sky.mapper;

import com.sky.dto.GoodsSalesDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface ReportMapper {

    /**
     * 统计指定时间段内的营业额（状态为已完成的订单金额之和）
     */
    @Select("select sum(amount) from orders where status = 5 and order_time between #{begin} and #{end}")
    Double getTurnover(LocalDateTime begin, LocalDateTime end);

    /**
     * 统计指定时间段内的新增用户数
     */
    @Select("select count(id) from user where create_time between #{begin} and #{end}")
    Integer getUserCount(LocalDateTime begin, LocalDateTime end);

    /**
     * 统计指定时间段内的订单总数
     */
    @Select("select count(id) from orders where order_time between #{begin} and #{end}")
    Integer getOrderTotal(LocalDateTime begin, LocalDateTime end);

    /**
     * 统计指定时间段内、指定状态的订单数
     */
    @Select("select count(id) from orders where status = #{status} and order_time between #{begin} and #{end}")
    Integer getOrderCountByStatus(Integer status, LocalDateTime begin, LocalDateTime end);

    /**
     * 统计指定时间段内的销量排名前10的菜品
     */
    @Select("select od.name, sum(od.number) number from order_detail od left join orders o on od.order_id = o.id " +
            "where o.status = 5 and o.order_time between #{begin} and #{end} " +
            "group by od.name order by number desc limit 0, 10")
    List<GoodsSalesDTO> getTop10(LocalDateTime begin, LocalDateTime end);

}
