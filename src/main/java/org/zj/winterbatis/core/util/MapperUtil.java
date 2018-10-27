package org.zj.winterbatis.core.util;

import org.zj.winterbatis.core.annotation.Id;
import org.zj.winterbatis.core.annotation.Table;
import org.zj.winterbatis.core.bean.Example;
import org.zj.winterbatis.core.sql.ListResultHandler;
import org.zj.winterbatis.core.enums.SqlMethod;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by ZhangJun on 2018/9/7.
 */
public class MapperUtil {
    /**
     * 通过example和sql 语言类型来获得具体的sql语句
     * @param sqlMethod
     * @param example
     * @return
     */
    static public String getSql(SqlMethod sqlMethod, Example example){
        String result = null;
        switch (sqlMethod) {
            case DELETE:
                result="delete from ";
                break;
            case SELECT:
                result="select * from ";
                break;
            case UPDATE:
                result="update ";
                break;
            case INSERT:
                return null;
        }
        String tableName=example.getType().getClass().getSimpleName();
        if(example.getType().getClass().isAnnotationPresent(Table.class)){
            tableName=example.getType().getClass().getAnnotation(Table.class).value();
        }
        result+=tableName;
        return result+example.getCondition();
    }

    /**
     * 根据方法/sql语句/数据源返回结果
     * @param method
     * @param sql
     * @param dataSource
     * @return
     */
    public static Object handleResult(Method method, String sql, DataSource dataSource) throws SQLException, ClassNotFoundException {
        return handleResult(ClassUtil.getGeneric(method),method,sql,dataSource);
    }

    public static Object handleResult(Class c,Method method,String sql,DataSource dataSource) throws SQLException, ClassNotFoundException {

        System.out.println("这是sql语句:         >  "+sql);

        if(sql==null||sql.equals("")){
            return null;
        }

        Connection connection = dataSource.getConnection();

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        if(ClassUtil.isListReturn(method)){
            if (c==null)
                return null;

            System.out.println("转换的方法走了");

            try {
                return new ListResultHandler<>().getListResult(resultSet,c);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return null;
    }


    /**
     * 获得指定class中的id字段名
     * @param c
     * @return
     */
    static public String getIdName(Class c){
        for(Field f:c.getDeclaredFields()){
            f.setAccessible(true);
            if(f.isAnnotationPresent(Id.class))
                return f.getName();
        }
        return FormateUtil.toLine(c.getSimpleName());
    }

    /**
     * 获得一个接口的父接口的泛型
     * @param c
     * @return
     */
    static public Class getParentInterfaceGeneric(Class c){
        return ClassUtil.getInterfaceGeneric(c);
    }


}
