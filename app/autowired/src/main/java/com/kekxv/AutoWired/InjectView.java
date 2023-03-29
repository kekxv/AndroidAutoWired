package com.kekxv.AutoWired;

import android.annotation.SuppressLint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 需要 setContentView 之后调用 IAutoWired.inject(this);
 * 只支持 Activity 或者带有自动注入 Activity 的对象
 */
@SuppressLint("NonConstantResourceId")
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface InjectView {
    /**
     * id 需要
     */
    int value() default 0;
}
