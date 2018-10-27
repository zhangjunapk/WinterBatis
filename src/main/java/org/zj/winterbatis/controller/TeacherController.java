package org.zj.winterbatis.controller;

import org.zj.winterbatis.core.annotation.*;
import org.zj.winterbatis.bean.Teacher;
import org.zj.winterbatis.dao.TeacherMapper;

import java.util.List;

/**
 * Created by ZhangJun on 2018/9/7.
 */
@RestController
@RequestMapping("/teacher")
public class TeacherController {

    @Autofired
    TeacherMapper teacherMapper;

    @GetMapping("/cc")
    public void cc(){
        teacherMapper.insert(new Teacher(1,"张君",19));
        teacherMapper.updateByPrimaryKey(new Teacher(1,"你懂得",99));
        List<Teacher> teachers = teacherMapper.selectAll();
        System.out.println(teachers.size()+"  获得的大小");
        for(Teacher t:teachers){
            System.out.println(t.toString());
        }
        teacherMapper.deleteByPrimaryKey(1);
    }
}
