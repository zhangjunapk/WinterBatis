package org.zj.winterbatis.controller;

import org.zj.winterbatis.annotation.*;
import org.zj.winterbatis.bean.Student;
import org.zj.winterbatis.service.impl.StudentServiceImpl;

import java.util.List;


/**
 * Created by ZhangJun on 2018/7/7.
 */
@Controller
@RequestMapping("/student")
public class StudentController {

    @Autofired
    StudentServiceImpl studentService;
    @RequestMapping("/all")
    public void jj(){
         studentService.findAll();
        /*for(Student s:all){
            System.out.println(s);
        }*//*
        可以吧mapper注入到controller
                但是不能将mapper注入到service*/
    }

    @RequestMapping("/view")
    public String d(){
        return "c";
    }

    @ResponceBody
    @RequestMapping("/json")
    public List<Student> s(){
        System.out.println("我走了");
        return studentService.findAll();
    }

    @ResponceBody
    @RequestMapping("/param")
    public void ss(@RequestBody Student s){
        System.out.println(s);
    }

    @ResponceBody
    @RequestMapping("/paramForm")
    public void sss(Student s){
        System.out.println(s);
    }
}
