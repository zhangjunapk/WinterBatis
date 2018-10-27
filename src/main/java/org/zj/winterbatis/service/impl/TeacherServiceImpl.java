package org.zj.winterbatis.service.impl;

import org.zj.winterbatis.core.annotation.Autofired;
import org.zj.winterbatis.core.annotation.Service;
import org.zj.winterbatis.bean.Teacher;
import org.zj.winterbatis.core.bean.Example;
import org.zj.winterbatis.dao.TeacherMapper;
import org.zj.winterbatis.service.ITeacherService;

import java.util.List;

/**
 * Created by ZhangJun on 2018/9/7.
 */
@Service
public class TeacherServiceImpl implements ITeacherService {
    @Autofired
    TeacherMapper teacherMapper;
    @Override
    public List<Teacher> getAll() {
        return teacherMapper.selectAll();
    }

    @Override
    public List<Teacher> selectCondition(Teacher teacher) {
        Example<Teacher> example=new Example<>();
        if(teacher.getId()!=null){
            example.andLike("id",teacher.getId());
        }
        if(teacher.getUsername()!=null){
            example.andLike("username","%"+teacher.getUsername()+"%");
        }
        if(teacher.getAge()!=null){
            example.andEqualsTo("age",teacher.getAge());
        }
        return teacherMapper.selectByExample(example);
    }

    @Override
    public Teacher selectOne(int id) {
        return teacherMapper.selectByPrimaryKey(id);
    }

    @Override
    public void delete(int id) {
        teacherMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void delete(Teacher teacher) {
        Example<Teacher> example=new Example<>();
        if(teacher.getId()!=null){
            example.andLike("id",teacher.getId());
        }
        if(teacher.getUsername()!=null){
            example.andLike("username","%"+teacher.getUsername()+"%");
        }
        if(teacher.getAge()!=null){
            example.andEqualsTo("age",teacher.getAge());
        }
         teacherMapper.deleteByExample(example);
    }

    @Override
    public void insert(Teacher teacher) {
        teacherMapper.insert(teacher);
    }

    @Override
    public void modify(Teacher teacher) {
        teacherMapper.updateByPrimaryKey(teacher);
    }
}
