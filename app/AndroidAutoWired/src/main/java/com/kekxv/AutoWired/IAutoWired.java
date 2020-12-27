package com.kekxv.AutoWired;

import android.content.Context;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class IAutoWired {

    public IAutoWired() {
        IAutoWired.inject(this);
    }

    private static final Map<String, Object> beanList = new HashMap<>();

    public static void init(Context context) {
        Scanner.scan(context, Service.class);
    }

    public static void registered(Class<?> cls, Object object) {
        registered(cls, object, "");
    }

    public static void registered(Class<?> cls, Object object, String Sign) {
        String key = cls.getName() + "_._" + Sign;
        if (!beanList.containsKey(key)) {
            beanList.put(key, object);
            IAutoWired.inject(Objects.requireNonNull(beanList.get(key)));
        }
    }

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
                            if (!isFound) continue;
                        }
                    }
                    {
                        if (!beanList.containsKey(key)) {
                            beanList.put(key, cla.newInstance());
                            IAutoWired.inject(Objects.requireNonNull(beanList.get(key)));
                        }

                        Object target = beanList.get(key);
                        field.setAccessible(true);
                        field.set(source, target);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
