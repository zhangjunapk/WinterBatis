package org.zj.winterbatis.core.util;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class RedisUtil {
    /**
     * 获得一个字段的val泛型
     * @param field
     * @return
     */
    public static Class getValClass(Field field){
        Class genericClass = FieldUtil.getGenericClass(field, 1);

        //

        System.out.println("------------------下面是获得的字段的泛型");
        System.out.println(genericClass);
        return genericClass;
    }



}
