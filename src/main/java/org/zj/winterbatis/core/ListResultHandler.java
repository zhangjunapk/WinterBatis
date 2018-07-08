package org.zj.winterbatis.core;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZhangJun on 2018/7/7.
 */
public class ListResultHandler<T> {
    public List<T> getListResult(ResultSet resultSet, String className) throws SQLException {
        List<T> result=new ArrayList<>();
        while(resultSet.next()){
            result.add((T) new BeanResultHandler().convertToBean(resultSet,className));
        }

        System.out.println(result);

        return result;
    }
}
