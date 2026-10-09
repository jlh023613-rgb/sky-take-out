package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 工作台（管理端首页）
 */
@RestController("adminWorkspaceController")
@RequestMapping("/admin/workspace")
@Slf4j
public class WorkspaceController {

    @Autowired
    private WorkspaceService workspaceService;

    /**
     * 工作台今日数据查询
     */
    @GetMapping("/businessData")
    public Result<BusinessDataVO> businessData() {
        //获得当天的开始时间
        LocalDateTime begin = LocalDateTime.now().with(LocalTime.MIN);
        //获得当天的结束时间
        LocalDateTime end = LocalDateTime.now().with(LocalTime.MAX);

        BusinessDataVO businessDataVO = workspaceService.getBusinessData(begin, end);
        return Result.success(businessDataVO);
    }

    /**
     * 查询订单管理数据
     */
    @GetMapping("/overviewOrders")
    public Result<OrderOverViewVO> orderOverView() {
        OrderOverViewVO orderOverViewVO = workspaceService.getOrderOverView();
        return Result.success(orderOverViewVO);
    }

    /**
     * 查询菜品总览
     */
    @GetMapping("/overviewDishes")
    public Result<DishOverViewVO> dishOverView() {
        DishOverViewVO dishOverViewVO = workspaceService.getDishOverView();
        return Result.success(dishOverViewVO);
    }

    /**
     * 查询套餐总览
     */
    @GetMapping("/overviewSetmeals")
    public Result<SetmealOverViewVO> setmealOverView() {
        SetmealOverViewVO setmealOverViewVO = workspaceService.getSetmealOverView();
        return Result.success(setmealOverViewVO);
    }
}
