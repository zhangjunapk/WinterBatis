package org.zj.winterbatis.util;

/**
 * Created by ZhangJun on 2018/9/7.
 */


import jdk.internal.org.objectweb.asm.TypeReference;
import org.zj.winterbatis.dao.TeacherMapper;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 反射工具
 */
public class ClassUtil {
    /**
     * 看方法的返回值是否是List
     * @param method
     * @return
     */
    static public boolean isListReturn(Method method){/*
        Type genericReturnType = method.getGenericReturnType();
        return ((ParameterizedType)genericReturnType).getRawType()==java.util.List.class;*/

        Class<?> returnType = method.getReturnType();

        System.out.println(returnType.getName()+"类型");

        return returnType.getName().contains("List");

    }

    /**
     * 获得方法返回list中的泛型类
     * @param method
     * @return
     */
    static public Class getListGeneric(Method method) throws ClassNotFoundException {

        System.out.println(method);

        try {
            Type genericReturnType = method.getGenericReturnType();
            //获得泛型名
            String name = genericReturnType.toString().substring(genericReturnType.toString().indexOf("<") + 1, genericReturnType.toString().length() - 1);
            System.out.println(name+"  泛型 名");
            if(name.equals("T"))
                return null;

            Class type = Class.forName(name);
            return type;
        }catch (Exception e){
            e.printStackTrace();
            //主要应对没有写泛型的情况
            return null;
        }
    }


    /**
     * 获得方法的返回类型
     * @param method
     * @return
     * @throws ClassNotFoundException
     */
    static public Class getReturnType(Method method) throws ClassNotFoundException {
        String s = method.getGenericReturnType().toString();
        if(s.equals("void"))
            return null;

        return Class.forName(s);
    }

    static public Method getMethod(Class c,String methodName,Class[] objects) throws NoSuchMethodException {
        return (Method) c.getMethod(methodName,c);
    }

    /**
     * 获得指定对象中的指定值
     * @param fieldName
     * @param o
     * @return
     */
    static public Object getVal(String fieldName,Object o) throws NoSuchFieldException, IllegalAccessException {
        Field declaredField = o.getClass().getDeclaredField(fieldName);
        declaredField.setAccessible(true);
        return declaredField.get(o);
    }


    /**
     * 获得返回的泛型
     * @param method
     * @return
     */
   static public Class getGeneric(Method method) throws ClassNotFoundException {
        if(isListReturn(method)){
            return getListGeneric(method);
        }
        return getReturnType(method);
    }

    /**
     * 获得接口的泛型
     * @param c
     * @return
     */
    static public Class getInterfaceGeneric(Class c){
        Type[] types = c.getGenericInterfaces();

        for (Type type : types) {
            if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                for(Type actualTypeArgument : actualTypeArguments) {
                    String typeName = actualTypeArgument.getTypeName();
                    try {
                        return Class.forName(typeName);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            }
        }
        return null;
    }


    public static void main(String[] args) {
        getInterfaceGeneric(TeacherMapper.class);
    }
}
