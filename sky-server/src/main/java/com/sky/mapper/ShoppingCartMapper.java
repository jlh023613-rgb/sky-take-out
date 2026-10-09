package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    //查询是否存在
    List<ShoppingCart> query(ShoppingCart shoppingCart);

    @Update("update shopping_cart  set number = #{number} where id =#{id}")
    void updateNumber(ShoppingCart cart);

    @Insert("insert into shopping_cart (user_id, dish_id, setmeal_id, name, amount, image, number, create_time,dish_flavor) " +
            "values (#{userId}, #{dishId}, #{setmealId}, #{name}, #{amount}, #{image}, #{number}, #{createTime}, #{dishFlavor})")
    void insert(ShoppingCart shoppingCart);

    List<ShoppingCart> queryList(ShoppingCart shoppingCart);

    @Delete("delete from shopping_cart where user_id = #{userId}")
    void deleteByUserId(Long userId);

    /**
     * 批量插入购物车数据（用于"再来一单"）
     */
    @Insert("<script>" +
            "insert into shopping_cart (user_id, dish_id, setmeal_id, name, amount, image, number, create_time, dish_flavor) values " +
            "<foreach collection='shoppingCartList' item='cart' separator=','>" +
            "(#{cart.userId}, #{cart.dishId}, #{cart.setmealId}, #{cart.name}, #{cart.amount}, #{cart.image}, #{cart.number}, #{cart.createTime}, #{cart.dishFlavor})" +
            "</foreach>" +
            "</script>")
    void insertBatch(List<ShoppingCart> shoppingCartList);
}
