package org.zj.winterbatis.core;

import org.zj.winterbatis.util.DBUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZhangJun on 2018/7/7.
 */
public class ListResultHandler<T> {
    public List<T> getListResult(ResultSet resultSet, Class<?> clazz) throws SQLException {
        List<T> result=new ArrayList<>();
        while(resultSet.next()){
            result.add((T) DBUtil.convertToBean(resultSet,clazz));
        }

        System.out.println(result);

        return result;
    }
}
