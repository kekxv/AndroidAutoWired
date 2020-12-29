package com.kekxv.AutoWired;


import android.content.Context;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import dalvik.system.DexFile;

public class Scanner {

    static List<Class<?>> list = new ArrayList<>();

    public static List<Class<?>> getList() {
        return list;
    }

    public static <A extends Annotation> void scan(String PackageCodePath, String packageName, Class<A> annotationClass) {
        try {
            DexFile df = new DexFile(PackageCodePath);
            for (Enumeration<String> inter = df.entries(); inter.hasMoreElements(); ) {
                String s = inter.nextElement();
                try {
                    if (s.contains(packageName)) {
                        Class<?> cls = Class.forName(s);
                        if (cls.isAnnotationPresent(annotationClass)) {
                            list.add(cls);
                        }
                    }
                } catch (Exception ignored) {
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static <A extends Annotation> void scan(Context context, Class<A> annotationClass) {
        scan(context.getPackageCodePath(), context.getPackageName(), annotationClass);
    }
}
