package org.zj.winterbatis.core;

import org.zj.winterbatis.annotation.*;

import javax.sql.DataSource;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

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

        System.out.println("方法走了吧");

        Object result = null;

        System.out.println(method+"方法");

        System.out.println("这是mapper里面的方法:"+method);

        //获取方法上的注解
        if(method.isAnnotationPresent(Select.class)){
            //根据sql获得结果
            System.out.println("得到的sql       "+getSql(method,args));
             return ResultHandler.handleResult(method,getSql(method,args), dataSource);
        }
        System.out.println(result);
        //直接运行sql语句
        runSql(getSql(method,args),method.getReturnType());
        return result;
    }

    public String getSql(Method method,Object[] args) throws Exception {
        if(method.isAnnotationPresent(Select.class)) {
            Select select = method.getAnnotation(Select.class);
            return _getSql(select.value(),args);
        }
        if(method.isAnnotationPresent(Insert.class)){
            Insert insert=method.getAnnotation(Insert.class);
            return _getSql(insert.value(),args);
        }
        if(method.isAnnotationPresent(Update.class)){
            Update update=method.getAnnotation(Update.class);
            return _getSql(update.value(),args);
        }

        if(method.isAnnotationPresent(Delete.class)){
            Delete delete=method.getAnnotation(Delete.class);
            return _getSql(delete.value(),args);
        }
        return null;
    }

    //解析参数列表获得转换后的sql
    public String _getSql(String before,Object[] args) throws Exception {

        if(args==null){
            return before;
        }

        for(Object arg:args){
            before =getReplaceSql(before,arg);
        }

        System.out.println("替换了:"+ before);

        return before;
    }

    public String getReplaceSql(String before,Object arg) throws Exception {

        System.out.println(arg.getClass().getName()+          "参数类型  "+before+arg.getClass().isAnnotationPresent(Param.class));


        //一个注解都得不到
        for(Annotation a:arg.getClass().getAnnotations()){
            System.out.println("         "+a.getClass().getName());
        }

        //Parameter能获得注解，但是不能获得值

        /*if(true*//*arg.getClass().isAnnotationPresent(Param.class)*//*){

            System.out.println("用param定义参数名了");

            Param annotation = arg.getClass().getAnnotation(Param.class);

            before=before.replace("${"+annotation.value()+"}","'"+arg+"'");
            return before;
        }*/

        for(Field field:arg.getClass().getDeclaredFields()){
            field.setAccessible(true);
            String s = String.valueOf(field.get(arg));



            System.out.println("${"+field.getName()+"}"+"       这是需要替换的表达式");

            System.out.println("       "+field.getName());
            System.out.println("替换成:       "+s);

            System.out.println("有毒吧"+field.get(arg));

            //方法和方法之间传递String是值传递   但是他是个引用对象

            before=before.replace("${"+field.getName()+"}","'"+s+"'");

            System.out.println("这是替换后的      >"+before);

        }
        return before;
    }

    public Object runSql(String sql,Class c) throws SQLException {
        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        boolean execute = statement.execute(sql);

        if(c.getName().contains("boolean")){
            return execute;
        }

        statement.close();
        connection.close();

        return null;
    }

}
