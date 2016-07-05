package com.cloudvision.tanzhenv2.order.deal;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * Map类型数据装换
 * 用于处理请求获得的简单json数据
 * 
 * Created by 谭智文
 */
public class MapUtils {

    public static java.util.Map<String, String> parseData(String data) {
        GsonBuilder gb = new GsonBuilder();
        Gson g = gb.create();
        return g.fromJson(data, new TypeToken<java.util.Map<String, String>>() {}.getType());
    }
    
    public static java.util.Map<String, Object> parseObjectData(String data) {
        GsonBuilder gb = new GsonBuilder();
        Gson g = gb.create();
        return g.fromJson(data, new TypeToken<java.util.Map<String, Object>>() {}.getType());
    }
}
