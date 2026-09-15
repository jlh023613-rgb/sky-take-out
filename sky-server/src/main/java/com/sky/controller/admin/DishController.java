package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @PostMapping
    public Result save(@RequestBody DishDTO dishDto){
        log.info("新增菜品：{}", dishDto);
        dishService.saveWithFlavor(dishDto);

        return Result.success();
    }

    @GetMapping("/page")
    public Result<PageResult> page(DishPageQueryDTO dto){
        log.info("菜品分页查询：{}", dto);
        PageResult pageResult = dishService.pageQuery(dto);
        return Result.success(pageResult);
    }
    @DeleteMapping
    public Result del(@RequestParam List<Long> ids){  //加param注解之后原先逗号分隔的字符串形式会变成long一个个差分
        log.info("删除菜品");
        dishService.delete(ids);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<DishVO> get(@PathVariable Long id){
        DishVO dishVO = dishService.getById(id);
        return Result.success(dishVO);
    }

    @PutMapping
    public Result update(@RequestBody DishDTO dishDto){
        dishService.updateWithFlavor(dishDto);
        return Result.success();
    }
}
