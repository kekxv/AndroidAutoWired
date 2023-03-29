package com.kekxv.AutoWired;


import android.content.Context;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import dalvik.system.DexFile;

/**
 * 扫描
 */
public class Scanner {

  /**
   * 需要自动注入的class
   */
  static List<Class<?>> list = new ArrayList<>();

  /**
   * 获取需要自动注入的 class
   */
  static List<Class<?>> getList() {
    return list;
  }

  /**
   * 手动设置，不使用 scan 自动扫描
   */
  static <A extends Annotation> void setList(Class<?>[] _list, @SuppressWarnings({"unused", "SameParameterValue"}) Class<A> annotationClass) {
    for (Class<?> aClass : _list) {
      if (!list.contains(aClass))
        list.add(aClass);
    }
  }

  /**
   * 自动扫描指定类
   */
  static <A extends Annotation> void scan(String PackageCodePath, String packageName, Class<A> annotationClass) {
    try {
      // noinspection deprecation
      DexFile df = new DexFile(PackageCodePath);
      for (// noinspection deprecation
          Enumeration<String> inter = df.entries(); inter.hasMoreElements(); ) {
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

  /**
   * 自动扫描
   */
  static <A extends Annotation> void scan(Context context, @SuppressWarnings("SameParameterValue") Class<A> annotationClass) {
    scan(context.getPackageCodePath(), context.getPackageName(), annotationClass);
  }
}