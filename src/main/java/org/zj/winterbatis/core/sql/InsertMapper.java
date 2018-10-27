package org.zj.winterbatis.core.sql;

import org.zj.winterbatis.core.annotation.Insert;

/**
 * Created by ZhangJun on 2018/9/7.
 */
public interface InsertMapper<T> {
    @Insert("insert into [entity] [each_insert]")
    T insert(T t);
}
