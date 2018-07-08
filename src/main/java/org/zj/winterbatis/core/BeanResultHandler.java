package org.zj.winterbatis.core;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by ZhangJun on 2018/7/7.
 */
public class BeanResultHandler {
    public Object convertToBean(ResultSet resultSet,String className){

        System.out.println("走了bean convert");

        try {
            Class<?> aClass = Class.forName(className);
            Object o = aClass.newInstance();

            System.out.println(aClass.getName());

            for(Field f:aClass.getDeclaredFields()){

                System.out.println(f.getName());
                System.out.println(getValue(f,resultSet));
                f.setAccessible(true);
                f.set(o,getValue(f,resultSet));
            }
            return o;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object getValue(Field f,ResultSet result) throws Exception {

        String typeName=f.getType().getName();

        if(typeName.contains("Integer")||typeName.contains("int")){
            return result.getInt(f.getName());
        }
        if(typeName.contains("String")){
            return result.getString(f.getName());
        }
        return null;
    }

}
