package com.example.test_autowired.AutoWired;

import android.content.Context;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IAutoWired {

    public IAutoWired() {
        IAutoWired.inject(this);
    }

    private static final Map<Class<?>, Object> beanList = new HashMap<>();

    public static void init(Context context) {
        Scanner.scan(context, Service.class);
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
            Autowired autowired = field.getAnnotation(Autowired.class);
            if (autowired != null) {
                try {
                    Class<?> clazz = field.getType();
                    Class<?> cla = clazz;
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
                    {
                        if (!beanList.containsKey(clazz)) {
                            beanList.put(clazz, cla.newInstance());
                            IAutoWired.inject(beanList.get(clazz));
                        }

                        Object target = beanList.get(clazz);
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
