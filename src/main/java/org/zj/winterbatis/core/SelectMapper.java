package org.zj.winterbatis.core;

import org.zj.winterbatis.annotation.Delete;
import org.zj.winterbatis.annotation.Select;

import java.util.List;

/**
 * Created by ZhangJun on 2018/9/7.
 */
public interface SelectMapper<T> {
    @Select("select * from [entity]")
    List<T> selectAll();

    @Select("select * from [entity] [example]")
    List<T> selectByExample(Example example);

    @Select("select * from [entity] where [pk] =[param_0]")
    T selectByPrimaryKey(Object pk);
}
