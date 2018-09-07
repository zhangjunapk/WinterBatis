package org.zj.winterbatis.core;

import org.zj.winterbatis.annotation.*;
import org.zj.winterbatis.util.*;

import javax.sql.DataSource;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.List;

/**
 * 通用Mapper的处理
 * Created by ZhangJun on 2018/9/7.
 */

public class BaseMapperInvocationHandler implements InvocationHandler {
    private DataSource dataSource;

    public BaseMapperInvocationHandler(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        //这里需要获得注解上面的sql ，然后进行解析并执行sql

        if (method.getAnnotations().length == 0) {
            return o;
        }
        if (method.isAnnotationPresent(Select.class)) {
            Select select = method.getAnnotation(Select.class);

            System.out.println("要执行的代码");

            String sql = parseSql(method, select.value(), objects);

            System.out.println("解析后的代码:"+ sql);

            //如果不是list返回值，直接返回
            if (!ClassUtil.isListReturn(method))
                return o;

            return MapperUtil.handleResult(method, sql, dataSource);
        }
        String rawSql = getSql(method);

        System.out.println("转换前的代码:   "+rawSql);

        rawSql = parseSql(method, rawSql, objects[0]);

        System.out.println(" 要执行的代码  "+rawSql);

        //接下来直接执行sql就行了
        DBUtil.runSql(rawSql, dataSource);
        return o;
    }

    private String getSql(Method method) {
        if (method.isAnnotationPresent(Select.class)) {
            return method.getAnnotation(Select.class).value();
        }
        if (method.isAnnotationPresent(Delete.class)) {
            return method.getAnnotation(Delete.class).value();
        }
        if (method.isAnnotationPresent(Update.class)) {
            return method.getAnnotation(Update.class).value();
        }
        if (method.isAnnotationPresent(Insert.class)) {
            return method.getAnnotation(Insert.class).value();
        }
        return null;
    }

    //根据sql表达式来解析成
    private String parseSql(Method method, String rawSql, Object object) throws ClassNotFoundException, SQLException, NoSuchFieldException, IllegalAccessException {
        //表示数据实体类
        Class c;
        String tablenName=null;
        if (ClassUtil.isListReturn(method)) {
            c = ClassUtil.getListGeneric(method);
        } else {
            c = ClassUtil.getReturnType(method);
        }

        tablenName= FormateUtil.toLine(c.getName());
        if(c.isAnnotationPresent(Table.class)){
            Table annotation = (Table) c.getAnnotation(Table.class);
            tablenName=annotation.value();
        }

        //对sql进行解析
        if (rawSql.contains("[entity]")) {
            //替换掉
            String name = c.getName();
            if (c.isAnnotationPresent(Id.class)) {
                Id ano = (Id) c.getAnnotation(Id.class);
                name = ano.value();
            }

            rawSql = rawSql.replace("[entity]", name);
        }
        if (rawSql.contains("pk"))
            rawSql = rawSql.replace("[pk]", MapperUtil.getIdName(c));

        if (rawSql.contains("[param_0]"))
            rawSql = rawSql.replace("[param_0]", ValUtil.parseString(object));

        if (rawSql.contains("[example]"))
            rawSql = rawSql.replace("[example]", ((Example) object).getCondition());

        if (rawSql.contains("[each_insert]")) {
            //遍历传递过来的参数中的每个字段


            if (method.isAnnotationPresent(Insert.class)) {
                rawSql+="(";
                List<String> fields = DBUtil.getFields(tablenName, dataSource);
                //insert into student(username,password,age) values();
                for(String str:fields){
                    rawSql+=str+",";
                }
                rawSql=rawSql.substring(0,rawSql.length()-1);
                rawSql+=") values(";
                //遍历获取实体bean的每个字段的值

                for(String s:fields){
                    rawSql+=ClassUtil.getVal(s,object)+",";
                }
                rawSql=rawSql.substring(0,rawSql.length()-1);
                rawSql+=")";
            }

        }
        return rawSql;
    }

}
