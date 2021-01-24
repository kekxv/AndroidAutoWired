package com.kekxv.AutoWired;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 注入工具类
 * 可以直接 extends IAutoWired 自动调用注册当前对象
 */
public class IAutoWired {

    /**
     * 自动调用 inject
     */
    public IAutoWired() {
        IAutoWired.inject(this);
    }

    /**
     * 管理的类
     */
    private static final Map<String, Object> beanList = new HashMap<>();

    /**
     * 还未注入的对象
     */
    private static final Map<String, List<AutoWiredInject>> autoWiredInjectList = new HashMap<>();

    /**
     * 初始化
     *
     * @param context 初始化
     */
    public static void init(Context context) {
        Scanner.scan(context, Service.class);
    }

    /**
     * 初始化
     *
     * @param _list 需要注入的Class
     */
    public static void init(Class<?>[] _list) {
        Scanner.setList(_list, Service.class);
    }

    /**
     * 手动注册 Sign 为 空 的 对象
     *
     * @param cls    对象类
     * @param object 对象
     */
    public static void registered(Class<?> cls, Object object) {
        registered(cls, object, "");
    }

    /**
     * 手动注册对象
     *
     * @param cls    对象类
     * @param object 对象
     * @param Sign   sign
     */
    public static void registered(Class<?> cls, Object object, String Sign) {
        String key = cls.getName() + "_._" + Sign;
        registered(key, object);
    }

    /**
     * 注册对象
     *
     * @param object 对象
     * @param key    sign
     */
    private static void registered(String key, Object object) {
        if (!beanList.containsKey(key)) {
            beanList.put(key, object);
            if (!IAutoWired.class.isAssignableFrom(object.getClass())) {
                // 新对象进行自动注入
                IAutoWired.inject(Objects.requireNonNull(beanList.get(key)));
            }
        }
        if (!autoWiredInjectList.containsKey(key)) autoWiredInjectList.put(key, new ArrayList<>());
        List<AutoWiredInject> list = autoWiredInjectList.get(key);
        if (list == null) return;
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setValue(object);
        }
        list.clear();
    }

    /**
     * 登记后注册
     *
     * @param key
     * @param field
     * @param object
     */
    private static void lastAutoWiredInjectList(String key, Field field, Object object) {
        if (!autoWiredInjectList.containsKey(key)) autoWiredInjectList.put(key, new ArrayList<>());
        List<AutoWiredInject> list = autoWiredInjectList.get(key);
        if (list == null) return;
        AutoWiredInject autoWiredInject = new AutoWiredInject();
        autoWiredInject.field = field;
        autoWiredInject.object = object;
        list.add(autoWiredInject);
    }

    /**
     * 注入
     *
     * @param source 待注入对象
     */
    public static void inject(Object source) {
        List<Class<?>> clas = Scanner.getList();
        Field[] fields = source.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                if (field.get(source) != null) continue;
            } catch (IllegalAccessException e) {
                // e.printStackTrace();
            }
            AutoWired autowired = field.getAnnotation(AutoWired.class);
            if (autowired != null) {
                try {
                    Class<?> clazz = field.getType();
                    String key = clazz.getName() + "_._" + autowired.Sign();
                    Class<?> cla = clazz;
                    if (!beanList.containsKey(key)) {
                        if (!clas.contains(clazz)) {
                            boolean isFound = false;
                            for (Class<?> c : clas) {
                                if (clazz.isAssignableFrom(c)) {
                                    isFound = true;
                                    cla = c;
                                    break;
                                }
                            }
                            if (!isFound) {
                                lastAutoWiredInjectList(key, field, source);
                                continue;
                            }
                        }
                    }
                    {
                        if (!beanList.containsKey(key)) {
                            // Method f = cla.getMethod(cla.getSimpleName());
                            // f.setAccessible(true);
                            Object obj = null;
                            try {
                                Constructor<?> constructor = cla.getDeclaredConstructor();
                                constructor.setAccessible(true);
                                obj = constructor.newInstance();
                                registered(key, obj);
                            } catch (Exception ignored) {
                                obj = cla.newInstance();
                                registered(key, obj);
                            }
                            try {
                                Service service = cla.getAnnotation(Service.class);
                                if (service != null && service.service()) {
                                    Method startMethod = cla.getMethod("start");
                                    startMethod.setAccessible(true);
                                    startMethod.invoke(obj);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                        Object target = beanList.get(key);
                        field.setAccessible(true);
                        field.set(source, target);
                    }
                } catch (Exception e) {
                    Log.e("IAutoWired", e.toString());
                }
            }
        }
    }


    /**
     * 带注入对象
     */
    static class AutoWiredInject {
        /**
         * 对象
         */
        public Object object;
        /**
         * 节点
         */
        public Field field;

        /**
         * 后注入
         *
         * @param object 注入对象的值
         */
        public void setValue(Object object) {
            field.setAccessible(true);
            try {
                field.set(this.object, object);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
