package org.zj.winterbatis.core.sql;

import org.zj.winterbatis.core.annotation.Delete;
import org.zj.winterbatis.core.bean.Example;

/**
 * Created by ZhangJun on 2018/9/7.
 */
public interface DeleteMapper<T> {
    @Delete("delete from [entity] where [pk] = [param_0]")
    T deleteByPrimaryKey(Object o);
    @Delete("delete from [entity] [example]")
    T deleteByExample(Example example);
}
