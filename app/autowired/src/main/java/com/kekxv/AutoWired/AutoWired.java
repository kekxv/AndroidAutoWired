package com.kekxv.AutoWired;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记为需要自动注入
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoWired {
    /**
     * 使用不同的 Sign 可以创建不同的对象
     * @return
     */
    public String Sign() default "";
    /**
     * 是否在构造函数依赖
     * @return
     */
    public boolean dependencies() default false;
}
