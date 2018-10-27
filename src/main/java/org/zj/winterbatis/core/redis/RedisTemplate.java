package org.zj.winterbatis.core.redis;

import org.zj.winterbatis.bean.Student;
import org.zj.winterbatis.core.annotation.Autofired;
import org.zj.winterbatis.core.annotation.Component;
import org.zj.winterbatis.core.sql.BaseMapper;
import org.zj.winterbatis.core.util.ClassUtil;
import org.zj.winterbatis.core.util.MapperUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @BelongsProject: WinterBatis
 * @BelongsPackage: org.zj.winterbatis.core
 * @Author: Java
 * @CreateTime: 2018-09-21 16:37
 * @Description: ${Description}
 */
@Component
public class RedisTemplate<K,V> {


    RedisTemplate<String,Student> redisTemplate;

    public IValueOps<K,V> opsForValue;

    public IListOps<K,V> opsForList;

    public IValueOps opsForValue(){
        return opsForValue;
    }
    public IListOps opsForList(){
        return opsForList;
    }

    public static void main(String[] args) {
        /*RedisTemplate<String, Student> s=new RedisTemplate<>();
        for(Field f:s.getClass().getDeclaredFields()){
            for(Method m:f.getClass().getDeclaredMethods()){
                String name = m.getName();
                if(name.equals("get")||name.equals("set")||name.equals("add"))
                System.out.println(m);
            }
        }*/

/*
        BaseMapper<Student> baseMapper;
        System.out.println("---------------");
        //这里要获得那个接口的泛型
        Class interfaceGeneric = ClassUtil.getInterfaceGeneric();
        System.out.println(interfaceGeneric);
*/

        //获得一个字段的泛型
        try {
            Field redisTemplate = RedisTemplate.class.getDeclaredField("redisTemplate");

            Type genericType = redisTemplate.getGenericType();
            System.out.println(genericType.getTypeName());

        }catch (Exception e){

        }

    }


}
