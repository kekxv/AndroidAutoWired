package com.kekxv.AutoWired;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Service {
    /**
     * 是否是服务类型，如果为 true
     * 则自动调用 start 无参 函数
     * 默认为 false
     * @return
     */
    public boolean service() default false;

}

