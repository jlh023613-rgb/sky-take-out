package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Override
    @Transactional //涉及多个数据表操作，需要使用事务
    public void saveWithFlavor(DishDTO dishDto) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDto, dish);
        //前端新增菜品时不传状态，默认设置为停售（0），起售后再通过修改状态功能开启
        dish.setStatus(0);
        //菜品表插入数据
        dishMapper.insert(dish);
        Long dishId = dish.getId();
        //口味表插入n条数据
        List<DishFlavor> flavors = dishDto.getFlavors();

        if (flavors != null && !flavors.isEmpty()){
            for (DishFlavor flavor : flavors) {
                flavor.setDishId(dishId);
            }
            dishFlavorMapper.insertBatch(flavors);

        }

    }

    @Override
    public PageResult pageQuery(DishPageQueryDTO dto) {
        PageHelper.startPage(dto.getPage(), dto.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dto);
        return new PageResult(page.getTotal(), page.getResult());

    }

    @Transactional
    @Override
    public void delete(List<Long> ids) {
        //一次可以删除多个
        //起售中的不让删
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if (dish.getStatus() == 1){
                //不让删
                throw new DeletionNotAllowedException("当前菜品在起售");
            }
        }
        //套餐关联的不能删
        List<Long> setmeal = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if (setmeal != null && !setmeal.isEmpty()){
            throw new DeletionNotAllowedException("当前菜品已关联套餐");
        }
        //要删除菜品
//        for (Long id : ids) {
//            dishMapper.deleteById(id);
//            //如果有关联的口味也要一起删
//            dishFlavorMapper.deleteByDishId(id);
//        }

        //批量删除比较好性能高
        dishMapper.deleteBatch(ids);
        dishFlavorMapper.deleteByDishIds(ids);


    }

    //根据id获得数据回显
    @Override
    public DishVO getById(Long id) {
        //获取菜品表的数据
        Dish dish = dishMapper.getById(id);
        //查询口味数据
        List<DishFlavor> flavors = dishFlavorMapper.getByDishId(id);
        //封装到VO
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(flavors);
        return dishVO;
    }

    @Transactional
    @Override
    public void updateWithFlavor(DishDTO dishDto) {
        //修改菜品表
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDto, dish);
        dishMapper.update(dish);
        //删除原先口味
        dishFlavorMapper.deleteByDishId(dishDto.getId());
        //新增口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        if (flavors != null && !flavors.isEmpty()){
            for (DishFlavor flavor : flavors) {
                flavor.setDishId(dishDto.getId());
            }
            dishFlavorMapper.insertBatch(flavors);
        }
    }
}
