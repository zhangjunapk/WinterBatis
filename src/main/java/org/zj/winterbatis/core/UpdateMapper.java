package org.zj.winterbatis.core;

import org.zj.winterbatis.annotation.Update;

/**
 * Created by ZhangJun on 2018/9/7.
 */
public interface UpdateMapper<T> {
    @Update("update [entity] set [update_set] where [pk_condition]")
    T updateByPrimaryKey(T t);
}
