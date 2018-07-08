package org.zj.winterbatis.core;

import org.zj.winterbatis.annotation.Select;

import javax.sql.DataSource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by ZhangJun on 2018/7/7.
 */
public class MapperInvocationHandler implements InvocationHandler {
    DataSource dataSource;
    public MapperInvocationHandler(DataSource dataSource){
        this.dataSource=dataSource;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        Object result = null;

        System.out.println(method+"方法");

        System.out.println("这是mapper里面的方法:"+method);

        //获取方法上的注解
        if(method.isAnnotationPresent(Select.class)){
            Select select=method.getAnnotation(Select.class);
            String value = select.value();
            //根据sql获得结果
             result = ResultHandler.handleResult(method, value, dataSource);
        }

        System.out.println(result);

        return result;
    }
}
