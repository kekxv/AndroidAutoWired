package com.kekxv.AutoWired.utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.Strictness;

import java.lang.reflect.Type;

public class GsonUtils {
    private static Gson gson;

    // 获取Gson实例
    private static Gson getGsonInstance() {
        if (gson == null) {
            gson = new GsonBuilder()
                    // 可以配置Gson，这里可以添加其他配置
                    .setStrictness(Strictness.LENIENT) // 容许一些非标准的JSON
                    .serializeNulls()
                    .disableHtmlEscaping()
                    .create();
        }
        return gson;
    }

    // 对象转JSON字符串
    public static String objectToJson(Object obj) {
        return getGsonInstance().toJson(obj);
    }

    // JSON字符串转对象
    public static <T> T jsonToObject(String json, Class<T> clazz) {
        return getGsonInstance().fromJson(json, clazz);
    }

    // JSON字符串转对象（使用Type）
    public static <T> T jsonToObject(String json, Type typeOfT) {
        return getGsonInstance().fromJson(json, typeOfT);
    }

    // JSON字符串转集合
    public static <T> T jsonToList(String json, Type typeOfT) {
        return getGsonInstance().fromJson(json, typeOfT);
    }
}