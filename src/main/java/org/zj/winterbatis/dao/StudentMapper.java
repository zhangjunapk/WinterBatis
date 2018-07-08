package org.zj.winterbatis.dao;

import org.zj.winterbatis.annotation.Mapper;
import org.zj.winterbatis.annotation.Select;
import org.zj.winterbatis.bean.Student;

import java.util.List;

/**
 * Created by ZhangJun on 2018/7/7.
 */
@Mapper
public interface StudentMapper {

    @Select("select * from student")
    List<Student> findAll();
}
