package org.zj.winterbatis.util;

/**
 * Created by ZhangJun on 2018/9/7.
 */


import org.zj.winterbatis.DoThing;
import org.zj.winterbatis.IStudent;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
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
        try {
            Type genericReturnType = method.getGenericReturnType();
            //获得泛型名
            String name = genericReturnType.toString().substring(genericReturnType.toString().indexOf("<") + 1, genericReturnType.toString().length() - 1);
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
        return Class.forName(s);
    }

    static public Method getMethod(Class c,String methodName,Class[] objects) throws NoSuchMethodException {
        return (Method) c.getMethod(methodName,c);
    }

    static public List<Class> getInterfaceGenerics(Class c){

        List<Class> result=new ArrayList<>();

        if(!c.isInterface()) return null;
        TypeVariable[] typeParameters = c.getTypeParameters();
        for(TypeVariable t:typeParameters){
            System.out.println(t.getTypeName()+" "+t.getName());
        }
        return null;
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

    public static void main(String[] args) {
        getInterfaceGenerics(IStudent.class);
    }
}
