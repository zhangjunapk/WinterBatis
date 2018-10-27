package org.zj.winterbatis.core.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by ZhangJun on 2018/9/7.
 */
public class AnnotationUtil {
    /**
     * 获得方法上指定的注解的val
     * @param method
     * @param annotation
     * @return
     */
    static public <T> String getMethodAnnotationVal(String fieldName, Method method, T annotation){
        Annotation a=(Annotation)annotation;
        if(method.isAnnotationPresent(a.annotationType())){
            //试试通过反射来获取
            Class<? extends Annotation> aClass = a.annotationType();
            try {
                Field field = aClass.getField(fieldName);
                field.setAccessible(true);
                return (String) field.get(annotation);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }
}
