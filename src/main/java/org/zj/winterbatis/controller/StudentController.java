package org.zj.winterbatis.controller;

import org.zj.winterbatis.core.annotation.*;
import org.zj.winterbatis.bean.Student;
import org.zj.winterbatis.dao.StudentMapper;
import org.zj.winterbatis.service.IStudentService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


/**
 * Created by ZhangJun on 2018/7/7.
 */
@Controller
@RequestMapping("/student")
public class StudentController {

    @Autofired
    StudentMapper studentMapper;

    @Autofired
    HttpServletRequest request;


    @Autofired
    HttpServletResponse response;


    @Autofired
    IStudentService studentService;


    @RequestMapping("/view")
    public String d() {

        request.setAttribute("studentList", studentService.findAll());

        return "c";
    }

    @ResponceBody
    @RequestMapping("/json")
    public List<Student> s() {
        System.out.println("我走了");
        return studentService.findAll();
    }

    @ResponceBody
    @RequestMapping("/param")
    public void ss(@RequestBody Student s) {
        System.out.println(s);
    }

    @ResponceBody
    @RequestMapping("/paramForm")
    public void sss(Student s) {
        System.out.println(s);
    }

    @RequestMapping("/diReq")
    @ResponceBody
    public void sss() throws IOException {
        System.out.println(request.getRequestURI() + " 这是controller");
        response.getWriter().write(" from controller");
    }

    @ResponceBody
    @RequestMapping("/search")
    public List<Student> jj(Student s) {
        return studentMapper.findCondition(s);
    }

    @RequestMapping("/insert")//其实成功了，
    @ResponceBody
    public void insert(Student s) throws IOException {
        studentMapper.insert(s);

        response.getWriter().write("其实写入成功了");

    }

    @RequestMapping("/update")//其实成功了
    @ResponceBody
    public void update(Student s) {
        studentMapper.update(s);
    }

    @RequestMapping("/delete")//其实成功了
    @ResponceBody
    public void delete(Student s) {
        studentMapper.delete(s);
    }

    @RequestMapping("/jj")
    @ResponceBody
    public Object jj() {
        studentService.findAll();
        return null;
    }

}
