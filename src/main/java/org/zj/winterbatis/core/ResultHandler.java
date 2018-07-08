package org.zj.winterbatis.core;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by ZhangJun on 2018/7/7.
 */
public class ResultHandler {
    /**
     * 根据方法/sql语句/数据源返回结果
     * @param method
     * @param sql
     * @param dataSource
     * @return
     */
    public static Object handleResult(Method method, String sql, DataSource dataSource) throws SQLException {

        Connection connection = dataSource.getConnection();

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        Type genericReturnType = method.getGenericReturnType();
        //获得泛型名
        String name=genericReturnType.toString().substring(genericReturnType.toString().indexOf("<")+1,genericReturnType.toString().length()-1);
        Class<?> returnType = method.getReturnType();

        System.out.println(returnType.getName()+"类型哦");

        if(returnType.getName().contains("List")){
            if(name!=null&&name!=""){

                System.out.println("转换的方法走了");

                try {
                    return new ListResultHandler<>().getListResult(resultSet,name);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

}
