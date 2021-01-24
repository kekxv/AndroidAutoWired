package com.kekxv.AutoWired;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;

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
        try {
            Class<?> cla = this.getClass();
            Service service = cla.getAnnotation(Service.class);
            if (service != null) {
                registered(cla, this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
     * 还未注入的对象
     */
    private static final Map<String, List<AutoWiredInjectView>> autoWiredInjectViewList = new HashMap<>();

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
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setValue(object);
            }
            list.clear();
        }
        if (object instanceof Activity) {
            if (!autoWiredInjectViewList.containsKey(key))
                autoWiredInjectViewList.put(key, new ArrayList<>());
            List<AutoWiredInjectView> listView = autoWiredInjectViewList.get(key);
            if (listView != null) {
                for (int i = 0; i < listView.size(); i++) {
                    AutoWiredInjectView autoWiredInjectView = listView.get(i);
                    try {
                        injectView((Activity) object, autoWiredInjectView.object, autoWiredInjectView.fields);
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }
                listView.clear();
            }
        }
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
     * 登记后注册
     *
     * @param key
     * @param field
     * @param object
     */
    private static void lastAutoWiredInjectViewList(String key, Field[] field, Object object) {
        if (!autoWiredInjectViewList.containsKey(key))
            autoWiredInjectViewList.put(key, new ArrayList<>());
        List<AutoWiredInjectView> list = autoWiredInjectViewList.get(key);
        if (list == null) return;
        AutoWiredInjectView autoWiredInjectView = new AutoWiredInjectView();
        autoWiredInjectView.fields = field;
        autoWiredInjectView.object = object;
        list.add(autoWiredInjectView);
    }

    /**
     * 注入
     *
     * @param source 待注入对象
     */
    public static void inject(Object source) {
        List<Class<?>> clas = Scanner.getList();

        Field[] fields = source.getClass().getDeclaredFields();
        List<Field> viewFields = new ArrayList<>();
        int hasActivity = 0;
        Field activityField = null;
        String activityKey = "";
        for (Field field : fields) {
            try {
                if (field.get(source) != null) continue;
            } catch (IllegalAccessException e) {
                // e.printStackTrace();
            }
            InjectView injectView = field.getAnnotation(InjectView.class);
            if (injectView != null) {
                viewFields.add(field);
            }
            AutoWired autowired = field.getAnnotation(AutoWired.class);
            if (autowired != null) {
                try {
                    Class<?> clazz = field.getType();
                    String key = clazz.getName() + "_._" + autowired.Sign();
                    if (Activity.class.isAssignableFrom(clazz)) {
                        hasActivity++;
                        activityField = field;
                        activityKey = key;
                    }
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
        if (viewFields.isEmpty()) return;
        if (source instanceof Activity) {
            try {
                IAutoWired.injectView((Activity) source, fields);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            return;
        }
        if (hasActivity != 1) return;
        try {
            activityField.setAccessible(true);
            Activity activity = (Activity) activityField.get(source);
            if (activity != null) {
                try {
                    IAutoWired.injectView(activity, source, viewFields.toArray(new Field[0]));
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
                return;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return;
        }
        lastAutoWiredInjectViewList(activityKey, viewFields.toArray(new Field[0]), source);
    }

    /**
     * 自动注入 View
     *
     * @param activity
     * @throws NoSuchMethodException
     */
    public static void injectView(Activity activity) throws NoSuchMethodException {
        injectView(activity, activity, activity.getClass().getDeclaredFields());
    }

    /**
     * 自动注入 View
     *
     * @param activity
     * @param declaredFields
     * @throws NoSuchMethodException
     */
    private static void injectView(Activity activity, Field[] declaredFields) throws NoSuchMethodException {
        injectView(activity, activity, declaredFields);
    }

    /**
     * 需要 setContentView 之后调用
     *
     * @param activity       Activity 的对象Activity
     * @param source         赋值对象
     * @param declaredFields 对象列表
     * @throws NoSuchMethodException
     */
    private static void injectView(Activity activity, Object source, Field[] declaredFields) throws NoSuchMethodException {
        if (null == activity) return;
        //找到findViewById方法
        Method findViewByIdMethod = activity.getClass().getMethod("findViewById", int.class);
        //获取activity的.class对象
        for (Field field : declaredFields) {
            //找到有@InjectView注解的成员变量
            if (field.isAnnotationPresent(InjectView.class)) {
                //得到注解类的对象
                InjectView annotation = field.getAnnotation(InjectView.class);
                if (annotation == null) continue;
                //找到VIew的id
                int value = annotation.value();
                try {
                    //暴力访问，可获取私有方法
                    findViewByIdMethod.setAccessible(true);
                    View view = (View) findViewByIdMethod.invoke(activity, value);
                    field.setAccessible(true);
                    //将结果赋值给成员变量
                    field.set(source, view);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 等待注入对象
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

    /**
     * 等待注入对象View
     */
    static class AutoWiredInjectView {
        /**
         * 对象
         */
        public Object object;
        /**
         * 节点
         */
        public Field[] fields;
    }
}
