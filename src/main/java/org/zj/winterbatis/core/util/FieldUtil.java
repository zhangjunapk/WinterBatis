package org.zj.winterbatis.core.util;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Arrays;

public class FieldUtil {

    /**
     *
     * @param f
     * @return
     */
    public static Type getGenericType(Field f){
        return f.getGenericType();
    }

    /**
     * 获得一个字段指定索引的泛型
     * @param field
     * @param index
     * @return
     */
    public static Class getGenericClass(Field field,int index){
        Type genericType = getGenericType(field);
        String s = genericType.toString();
        String substring = s.substring(s.indexOf("<")+1, s.lastIndexOf(">"));
        String[] split = substring.split(",");
        try{
            System.out.println("字段泛型数组里面的内容        --------->");
            System.out.println(Arrays.toString(split));
            return Class.forName(split[index].trim());
        }catch (Exception e){
            return null;
        }
    }

}
