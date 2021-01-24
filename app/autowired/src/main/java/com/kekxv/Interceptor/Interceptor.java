package com.kekxv.Interceptor;

import java.lang.reflect.Method;

/**
 * 定义拦截器接口
 */
public interface Interceptor {
    default boolean before(Object proxy, Object target, Method method, Object[] args) {
        return true;
    }

    default void around(Object proxy, Object target, Method method, Object[] args) {
    }

    public void after(Object proxy, Object target, Method method, Object[] args);
}