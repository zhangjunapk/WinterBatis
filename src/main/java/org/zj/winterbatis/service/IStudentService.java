package org.zj.winterbatis.service;

import org.zj.winterbatis.bean.Student;

import java.util.List;

/**
 * Created by ZhangJun on 2018/7/7.
 */
public interface IStudentService {
    List<Student> findAll();
}
