package org.zj.winterbatis.service;

import org.zj.winterbatis.bean.Teacher;

import java.util.List;

/**
 * Created by ZhangJun on 2018/9/7.
 */
public interface ITeacherService {
    List<Teacher> getAll();
    List<Teacher> selectCondition(Teacher teacher);
    Teacher selectOne(int id);
    void delete(int id);
    void delete(Teacher teacher);
    void insert(Teacher teacher);
    void modify(Teacher teacher);
}
