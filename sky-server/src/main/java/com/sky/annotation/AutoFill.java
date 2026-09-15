package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) //定义注解只能加在方法上
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFill {
    //刚才分析的只有update和insert才会用到所以获取一下操作类型
    OperationType value();
}
