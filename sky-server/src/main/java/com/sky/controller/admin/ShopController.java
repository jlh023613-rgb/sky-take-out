package com.sky.controller.admin;

import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/admin/shop")
@RestController("AdminShopController")
@Slf4j
public class ShopController {
    //注入redis
    @Autowired
    private RedisTemplate redisTemplate;

    //设置店铺状态
    @PutMapping("/{status}")
    public Result setStatus(@PathVariable Integer status){
        log.info("设置店铺状态");
        //存到redis中
        redisTemplate.opsForValue().set("SHOP_STATUS",status);
        return Result.success();
    }

    //查询店铺状态
    @GetMapping("/status")
    public Result<Integer> getStatus(){
        log.info("查询店铺状态");
        Integer status =(Integer) redisTemplate.opsForValue().get("SHOP_STATUS");
        return Result.success(status);
    }


}
