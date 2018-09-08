package org.zj.winterbatis.core;

import org.zj.winterbatis.annotation.*;
import org.zj.winterbatis.util.*;

import javax.sql.DataSource;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.text.Normalizer;
import java.util.List;

/**
 * 通用Mapper的处理
 * Created by ZhangJun on 2018/9/7.
 */

public class BaseMapperInvocationHandler implements InvocationHandler {
    private DataSource dataSource;
    private Class interfacee;
    public BaseMapperInvocationHandler(Class c,DataSource dataSource) {
        this.dataSource = dataSource;
        this.interfacee=c;
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

            String sql = parseSql(MapperUtil.getParentInterfaceGeneric(interfacee),method, select.value(), objects);

            System.out.println("解析后的代码:"+ sql);

            //如果不是list返回值，直接返回
            if (!ClassUtil.isListReturn(method))
                return o;

            return MapperUtil.handleResult(MapperUtil.getParentInterfaceGeneric(interfacee),method, sql, dataSource);
        }
        String rawSql = getSql(method);

        System.out.println("转换前的代码:   "+rawSql);

        rawSql = parseSql(ClassUtil.getInterfaceGeneric(interfacee),method, rawSql, objects[0]);

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

    /**
     *对指定的sql 进行解析
     * @param c 表示实体类
     * @param method
     * @param rawSql
     * @param object
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    private String parseSql(Class c,Method method, String rawSql, Object object) throws ClassNotFoundException, SQLException, NoSuchFieldException, IllegalAccessException {
        //表示数据实体类
        String tablenName=null;


        System.out.println(ClassUtil.getInterfaceGeneric(c));

        // TODO: 2018/9/8  
        tablenName= FormateUtil.toLine(c.getSimpleName());
        

        
        if(c.isAnnotationPresent(Table.class)){
            Table annotation = (Table) c.getAnnotation(Table.class);
            tablenName=annotation.value();
        }

        //对sql进行解析
        if (rawSql.contains("[entity]")) {
            //替换掉
                rawSql = rawSql.replace("[entity]", tablenName);
        }
        if (rawSql.contains("[pk]"))
            rawSql = rawSql.replace("[pk]", MapperUtil.getIdName(c));

        if (rawSql.contains("[param_0]"))
            rawSql = rawSql.replace("[param_0]", ValUtil.parseString(object));

        if (rawSql.contains("[example]"))
            rawSql = rawSql.replace("[example]", ((Example) object).getCondition());

        if (rawSql.contains("[each_insert]")) {
            //遍历传递过来的参数中的每个字段

            System.out.println("之前--------");
            System.out.println(rawSql);
                System.out.println("insert");

                rawSql+="(";
                List<String> fields = DBUtil.getFields(tablenName, dataSource);
                //insert into student(username,password,age) values();
                for(String str:fields){
                    rawSql+=str+",";
                    System.out.println(rawSql);
                }
                rawSql=rawSql.substring(0,rawSql.length()-1);
                rawSql+=") values(";
                //遍历获取实体bean的每个字段的值

                for(String s:fields){
                    rawSql+="'"+ClassUtil.getVal(s,object)+"',";
                    System.out.println(rawSql);
                }
                rawSql=rawSql.substring(0,rawSql.length()-1);
                rawSql+=")";

                rawSql=rawSql.replace("[each_insert]","");

            System.out.println("之后--------");
            System.out.println(rawSql);

        }

        if(rawSql.contains("[update_set]")){
            // username='zhangsan',password='lisi'
            //遍历这个表中的所有字段
            String str="";
            for(String name:DBUtil.getFields(FormateUtil.toLine(c.getSimpleName()),dataSource)){
                str+=name+"='"+ClassUtil.getVal(FormateUtil.toCamelCase(0,name),object)+"',";
                System.out.println(rawSql);
            }
            str=str.substring(0,str.length()-1);
            rawSql=rawSql.replace("[update_set]",str);
        }

        if(rawSql.contains("[pk_condition]")){
            String idName = MapperUtil.getIdName(c);
            rawSql+= FormateUtil.toLine(idName)+"="+ClassUtil.getVal(idName,object);
            System.out.println(rawSql);
        }
        rawSql=rawSql.replace("[pk_condition]","");

        return rawSql;
    }

}
