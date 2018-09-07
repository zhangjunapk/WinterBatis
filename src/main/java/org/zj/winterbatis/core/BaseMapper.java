package org.zj.winterbatis.core;

import java.util.List;

/**
 * Created by ZhangJun on 2018/9/7.
 */

/**
 * 难点
 * 2.怎么获得具体要执行的方法
 * @param <T>
 */
public interface BaseMapper<T> extends SelectMapper<T>,DeleteMapper<T>,UpdateMapper<T>,InsertMapper<T>{

}
