package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
    void saveWithFlavor(DishDTO dishDto);

    PageResult pageQuery(DishPageQueryDTO dto);

    void delete(List<Long> ids);

    DishVO getById(Long id);

    void updateWithFlavor(DishDTO dishDto);

    /**
     * 根据分类id查询启售菜品（带口味）
     * @param categoryId
     * @return
     */
    List<DishVO> listWithFlavor(Long categoryId);
}
