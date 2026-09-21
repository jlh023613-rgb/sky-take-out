package com.sky.controller.user;

import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/user/shop")
@RestController("UserShopController")
@Slf4j
public class ShopController {
    //注入redis
    @Autowired
    private RedisTemplate redisTemplate;

    //查询店铺状态
    @GetMapping("/status")
    public Result<Integer> getStatus(){
        log.info("查询店铺状态");
        Integer status =(Integer) redisTemplate.opsForValue().get("SHOP_STATUS");
        return Result.success(status);
    }
}
