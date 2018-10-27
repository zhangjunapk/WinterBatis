package org.zj.winterbatis.core.sql;

import org.zj.winterbatis.core.annotation.Select;
import org.zj.winterbatis.core.bean.Example;

import java.util.List;

/**
 * Created by ZhangJun on 2018/9/7.
 */
public interface SelectMapper<T> {
    @Select("select * from [entity]")
    List<T> selectAll();

    @Select("select * from [entity] where [example]")
    List<T> selectByExample(Example example);

    @Select("select * from [entity] where [pk] =[param_0]")
    T selectByPrimaryKey(Object pk);
}
