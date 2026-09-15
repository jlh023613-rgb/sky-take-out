package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自定义切面，实现公共字段自动填充
 * 拦截 com.sky.mapper 下所有被 @AutoFill 注解标识的方法，
 * 在执行数据库插入/更新操作前，自动为实体对象的公共字段赋值。
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    /**
     * 切入点：拦截 Mapper 层中带有 @AutoFill 注解的方法
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut() {
    }

    /**
     * 前置通知：在目标方法执行前，自动填充公共字段
     */
    @Before("autoFillPointCut()")
    public void autoFillBefore(JoinPoint joinPoint) {
        log.info("开始公共字段自动填充...");

        //1、获取当前被拦截方法上的数据库操作类型
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
        OperationType operationType = autoFill.value();

        //2、获取被拦截方法的参数 —— 约定实体对象作为第一个参数传入
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return;
        }
        Object entity = args[0];

        //3、准备要填充的数据：当前时间、当前登录用户id
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        //4、根据操作类型，为对应属性赋值
        if (operationType == OperationType.INSERT) {
            //插入操作：填充 4 个公共字段
            try {
                Method setCreateTime = entity.getClass()
                        .getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setUpdateTime = entity.getClass()
                        .getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setCreateUser = entity.getClass()
                        .getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateUser = entity.getClass()
                        .getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                setCreateTime.invoke(entity, now);
                setUpdateTime.invoke(entity, now);
                setCreateUser.invoke(entity, currentId);
                setUpdateUser.invoke(entity, currentId);
            } catch (Exception e) {
                log.error("公共字段自动填充失败（INSERT）：{}", e.getMessage());
            }

        } else if (operationType == OperationType.UPDATE) {
            //更新操作：填充 2 个公共字段
            try {
                Method setUpdateTime = entity.getClass()
                        .getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass()
                        .getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                setUpdateTime.invoke(entity, now);
                setUpdateUser.invoke(entity, currentId);
            } catch (Exception e) {
                log.error("公共字段自动填充失败（UPDATE）：{}", e.getMessage());
            }
        }
    }
}
