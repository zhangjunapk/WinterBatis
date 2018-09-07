package org.zj.winterbatis.core;

import org.zj.winterbatis.annotation.Delete;

/**
 * Created by ZhangJun on 2018/9/7.
 */
public interface DeleteMapper<T> {
    @Delete("delete from [entity] where [pk] = [param_0]")
    T deleteByPrimaryKey(Object o);
    @Delete("delete from [entity] [example]")
    T deleteByExample(Example example);
}
