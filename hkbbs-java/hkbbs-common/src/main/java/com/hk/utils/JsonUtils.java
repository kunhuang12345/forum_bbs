package com.hk.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class JsonUtils {
    public static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);

    /**
     * 对象转json
     * @param obj
     * @return
     */
    public static String convertObj2Json(Object obj){
        return JSON.toJSONString(obj);
    }

    /**
     * json转字符串
     * @param json
     * @param clazz
     * @return
     * @param <T>
     */
    public static <T> T convertJson2Obj(String json,Class<T> clazz){
        return JSONObject.parseObject(json,clazz);
    }

    /**
     * 字符串数组转集合对象
     * @param json
     * @param clazz
     * @return
     * @param <T>
     */
    public static <T>List<T> convertJsonArray2List(String json,Class<T> clazz){
        return JSONArray.parseArray(json,clazz);
    }
}
