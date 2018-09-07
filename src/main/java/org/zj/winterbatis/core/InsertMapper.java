package org.zj.winterbatis.core;

import org.zj.winterbatis.annotation.Insert;

/**
 * Created by ZhangJun on 2018/9/7.
 */
public interface InsertMapper<T> {
    @Insert("insert into [entity] [each_insert]")
    void insert(T t);
}
