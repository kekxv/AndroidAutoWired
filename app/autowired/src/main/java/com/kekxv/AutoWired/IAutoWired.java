package com.kekxv.AutoWired;

import android.app.Activity;
import android.content.Context;
import android.support.v4.util.ArrayMap;
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
        Service service = null;
        Class<?> cla = this.getClass();
        try {
            service = cla.getAnnotation(Service.class);
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
        Class<?> sourceClass = source.getClass();
        Field[] fields = getDeclaredFields(sourceClass);
        List<Field> viewFields = new ArrayList<>();
        int hasActivity = 0;
        Field activityField = null;
        String activityKey = "";

        List<Field> autoWiredFields = new ArrayList<>();
        List<Field> interpretationFields = new ArrayList<>();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                if (field.get(source) != null) continue;
            } catch (IllegalAccessException e) {
                // e.printStackTrace();
            }
            InjectView injectView = field.getAnnotation(InjectView.class);
            if (injectView != null) {
                viewFields.add(field);
            }
            AutoWired autowired = field.getAnnotation(AutoWired.class);
            if (autowired == null) continue;

            try {
                Class<?> clazz = field.getType();
                String key = clazz.getName() + "_._" + autowired.Sign();
                if (Activity.class.isAssignableFrom(clazz)) {
                    hasActivity++;
                    activityField = field;
                    activityKey = key;
                }
                if (!beanList.containsKey(key)) {
                    if (!clas.contains(clazz)) {
                        List<Class<?>> foundCls = new ArrayList<>();
                        for (Class<?> c : clas) {
                            if (clazz.isAssignableFrom(c)) {
                                foundCls.add(c);
                            }
                        }
                        if (foundCls.isEmpty()) {
                            lastAutoWiredInjectList(key, field, source);
                            continue;
                        } else if (foundCls.size() > 1) {
                            if (autowired.Interpretation() == null || autowired.Interpretation().isEmpty()) {
                                lastAutoWiredInjectList(key, field, source);
                                continue;
                            }
                            interpretationFields.add(field);
                            continue;
                        }
                    }
                }
                autoWiredFields.add(field);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        injectAutoWired(source, autoWiredFields, clas, sourceClass);
        injectAutoWiredInterpretation(source, interpretationFields, clas, sourceClass);

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
        injectHasActivity(source,
                activityKey,
                activityField,
                viewFields);
    }

    private static Field[] getDeclaredFields(Class<?> sourceClass) {
        Map<String, Field> list = new ArrayMap<>();
        if (sourceClass == null || sourceClass == Object.class) return new Field[0];
        Class<?>[] cla = sourceClass.getInterfaces();
        Field[] fields = sourceClass.getDeclaredFields();
        for (Field field : fields) {
            if (list.containsKey(field.getName())) continue;
            list.put(field.getName(), field);
        }
        for (Class<?> cls : cla) {
            Field[] _fields = cls.getDeclaredFields();
            for (Field field : _fields) {
                if (list.containsKey(field.getName())) continue;
                list.put(field.getName(), field);
            }
        }
        for (Field field : fields) {
            if (list.containsKey(field.getName())) continue;
            list.put(field.getName(), field);
        }
        fields = new Field[list.size()];
        int i = 0;
        for (String key : list.keySet()) {
            fields[i++] = list.get(key);
        }
        return fields;
    }

    private static void injectAutoWired(Object source, List<Field> fields, List<Class<?>> clas, Class<?> sourceClass) {
        for (Field field : fields) {
            AutoWired autowired = field.getAnnotation(AutoWired.class);
            if (autowired != null) {
                try {
                    Class<?> clazz = field.getType();
                    String key = clazz.getName() + "_._" + autowired.Sign();
                    Class<?> cla = clazz;
                    if (!beanList.containsKey(key)) {
                        if (!clas.contains(clazz)) {
                            List<Class<?>> foundCls = new ArrayList<>();
                            for (Class<?> c : clas) {
                                if (clazz.isAssignableFrom(c)) {
                                    foundCls.add(c);
                                }
                            }
                            if (foundCls.size() == 1) {
                                cla = foundCls.get(0);
                            } else {
                                continue;
                            }
                        }
                    }
                    {
                        if (!beanList.containsKey(key)) {
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

    private static void injectAutoWiredInterpretation(Object source, List<Field> fields, List<Class<?>> clas, Class<?> sourceClass) {

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
                            List<Class<?>> foundCls = new ArrayList<>();
                            for (Class<?> c : clas) {
                                if (clazz.isAssignableFrom(c)) {
                                    foundCls.add(c);
                                }
                            }
                            if (foundCls.size() <= 1) {
                                continue;
                            }
                            if (autowired.Interpretation() == null || autowired.Interpretation().isEmpty()) {
                                throw new Exception("无法确定注入对象");
                            }
                            Method method;
                            try {
                                method = sourceClass.getMethod(autowired.Interpretation());
                            } catch (Exception e) {
                                method = sourceClass.getDeclaredMethod(autowired.Interpretation());
                                method.setAccessible(true);
                            }
                            if (method == null) {
                                throw new Exception("未指定 Interpretation");
                            }
                            method.setAccessible(true);
                            Object obj = null;
                            if (String.class == method.getReturnType()) {
                                cla = null;
                                String className = (String) method.invoke(source);
                                for (Class<?> c : foundCls) {
                                    if (c.getName().contains(className)) {
                                        cla = c;
                                        break;
                                    }
                                }
                                if (cla == null) {
                                    throw new Exception("无法确定注入对象");
                                }
                                try {
                                    Constructor<?> constructor = cla.getDeclaredConstructor();
                                    constructor.setAccessible(true);
                                    obj = constructor.newInstance();
                                } catch (Exception ignored) {
                                    obj = cla.newInstance();
                                }
                            } else if (method.getReturnType() == Class.class) {
                                cla = (Class<?>) method.invoke(source);
                                try {
                                    Constructor<?> constructor = cla.getDeclaredConstructor();
                                    constructor.setAccessible(true);
                                    obj = constructor.newInstance();
                                } catch (Exception ignored) {
                                    obj = cla.newInstance();
                                }
                            } else if (clazz.isAssignableFrom(method.getReturnType())) {
                                obj = method.invoke(source);
                                cla = obj.getClass();
                            }
                            registered(key, obj);
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
                    }
                    Object target = beanList.get(key);
                    field.setAccessible(true);
                    field.set(source, target);
                } catch (Exception e) {
                    Log.e("IAutoWired", e.toString());
                }
            }
        }
    }


    /**
     * 调用注入 injectView
     *
     * @param source
     * @param activityKey
     * @param activityField
     * @param viewFields
     */
    private static void injectHasActivity(Object source, String activityKey, Field activityField, List<Field> viewFields) {
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
                if (value <= 0) {
                    value = getCommentID(activity, "id", field.getName());
                }
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
     * 反射得到组件的id号
     *
     * @param activity  activity
     * @param className layout,string,drawable,style,id,color,array
     * @param idName    唯一文件名
     * @return 资源id
     */
    public static int getCommentID(Activity activity, String className, String idName) {
        int id = -1;
        // Log.v("testapp", "appname  :" + activity.getPackageName());
        try {
            Class<?> cls = Class.forName(activity.getPackageName()
                    + ".R$" + className);
            // Log.v("testapp", "classname :" + cls.getName());
            id = cls.getField(idName).getInt(cls);
        } catch (Exception e) {
            e.printStackTrace();
            // Log.v("testapp", "get error");
        }
        return id;
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
