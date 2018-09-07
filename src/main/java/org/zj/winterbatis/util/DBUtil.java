package org.zj.winterbatis.util;

import org.zj.winterbatis.annotation.Table;
import org.zj.winterbatis.core.Example;
import org.zj.winterbatis.core.ListResultHandler;
import org.zj.winterbatis.enums.SqlMethod;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZhangJun on 2018/9/7.
 */
//数据库工具类
public class DBUtil {

    /**
     * 返回sql执行的list结果
     * @param sql
     * @param dataSource
     * @param t
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> List<T> getListResult(String sql, DataSource dataSource,T t) throws Exception {
        ResultSet resultSet = getStatement(dataSource).executeQuery(sql);
        return new ListResultHandler<T>().getListResult(resultSet, (Class<?>) t);
    }

    public static Object convertToBean(ResultSet resultSet, Class clazz){

        System.out.println("走了bean convert");

        try {
            Class<?> aClass = clazz;
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

    public static Object getValue(Field f, ResultSet result) throws Exception {

        String typeName=f.getType().getName();

        if(typeName.contains("Integer")||typeName.contains("int")){
            return result.getInt(f.getName());
        }
        if(typeName.contains("String")){
            return result.getString(f.getName());
        }
        return null;
    }

    /**
     * 直接执行sql语句
     * @param sql
     * @param dataSource
     * @return
     */
    public static boolean runSql(String sql,DataSource dataSource) throws SQLException {
        Statement statement = getStatement(dataSource);
        return statement.execute(sql);
    }

    public static Statement getStatement(DataSource dataSource) throws SQLException {
        Connection connection = dataSource.getConnection();
        return connection.createStatement();
    }

    /**
     * 获得表中所有的字段名
     * @param tableName
     * @param dataSource
     * @return
     */
    static public List<String> getFields(String tableName,DataSource dataSource) throws SQLException {
        List<String> result=new ArrayList<>();

        String sql="select * from "+tableName;
        Statement statement = getStatement(dataSource);
        ResultSet resultSet = statement.executeQuery(sql);
        ResultSetMetaData metaData = resultSet.getMetaData();
        for(int i=1;i<metaData.getColumnCount();i++){
            result.add(metaData.getColumnName(i));
        }
        resultSet.close();
        return result;
    }
}
