package org.zj.winterbatis.dao;

import org.zj.winterbatis.annotation.*;
import org.zj.winterbatis.bean.Student;

import java.util.List;

/**
 * Created by ZhangJun on 2018/7/7.
 */
@Mapper
public interface StudentMapper {

    @Select("select * from student")
    List<Student> findAll();

    @Select("select * from student where username=${username}")
    List<Student> findCondition(Student s);


    @Insert("insert into student(username,password) values(${username},${password})")
    boolean insert(Student student);

    @Delete("delete from student where username=${username}")
    boolean delete(Student s);

    @Update("update student set username=${username}")
    boolean update(Student s);
}
