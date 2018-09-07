package org.zj.winterbatis.core;

import org.zj.winterbatis.annotation.Update;

/**
 * Created by ZhangJun on 2018/9/7.
 */
public interface UpdateMapper<T> {
    @Update("update [entity]")
    void updateByPrimaryKey(T t);
}
