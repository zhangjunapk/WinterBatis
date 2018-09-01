package org.zj.winterbatis.service.impl;

import com.github.pagehelper.PageInfo;
import org.zj.winterbatis.annotation.Autofired;
import org.zj.winterbatis.annotation.Page;
import org.zj.winterbatis.annotation.Service;
import org.zj.winterbatis.bean.Student;
import org.zj.winterbatis.dao.StudentMapper;
import org.zj.winterbatis.service.IStudentService;

import java.util.List;

/**
 * Created by ZhangJun on 2018/7/7.
 */
@Service
//如果实现了接口，注入不进去 提示不能注入
public class StudentServiceImpl{

    //往这里面注入mapper有问题
    @Autofired
    StudentMapper studentMapper;

    public List<Student> findAll() {
        studentMapper.findAll();

        System.out.println("查找所有");
        return studentMapper.findAll();
    }

    @Page(page =1,rows = 5)
    public Object f(){
        System.out.println("service 进来了");
        List<Student> all = studentMapper.findAll();
        return all;
    }
}
