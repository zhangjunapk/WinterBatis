package org.zj.winterbatis;

import org.zj.winterbatis.annotation.Delete;
import org.zj.winterbatis.bean.Teacher;

import java.util.List;

/**
 * Created by ZhangJun on 2018/9/7.
 */
public class DoThing<Teacher> {
    @Delete("select * from student")
    public List<Teacher> get(){
        return null;
    }
}
