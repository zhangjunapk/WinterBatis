package org.zj.winterbatis.core.util;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * json工具类，用于转换json为list集合
 */
public class JsonUtil {
    /**
     * 将json字符串转换成对象
     * @param jsonStr
     * @param c
     * @param <T>
     * @return
     */
    public static <T> List<Object> convertList(String jsonStr,Class c){
        return (List<Object>) JSONObject.parseArray(jsonStr,c);
    }

    /**
     * 将String 转换成bean
     * @param <T>
     * @return
     */
    public static  <T> Object convertBean(String jsonStr,Class c){
        return JSONObject.parseObject(jsonStr,c);
    }

}
