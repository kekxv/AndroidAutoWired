package com.kekxv.Interceptor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class InterceptorJdkProxy implements InvocationHandler {

    private Object target;
    private Class<? extends Interceptor> interceptorClass = null;  //通过拦截器类名引入

    public InterceptorJdkProxy(Object target, Class<? extends Interceptor> interceptorClass) {
        this.interceptorClass = interceptorClass;
        this.target = target;
    }

    public static Object bind(Object target, Class<? extends Interceptor> interceptorClass) {
        return Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(), new InterceptorJdkProxy(target, interceptorClass));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 标识未设置拦截器，可以直接调用原方法返回
        if (interceptorClass == null) {
            return method.invoke(target, args);
        }
        Object result = null;
        // 类名反射生成拦截器对象
        Interceptor interceptor = interceptorClass.newInstance();
        if (interceptor.before(proxy, target, method, args)) {
            result = method.invoke(target, args);
        } else {
            interceptor.around(proxy, target, method, args);
        }
        interceptor.after(proxy, target, method, args);
        return result;
    }
}
