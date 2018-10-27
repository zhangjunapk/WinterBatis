package org.zj.winterbatis.controller;

import org.zj.winterbatis.bean.Student;
import org.zj.winterbatis.core.annotation.Autofired;
import org.zj.winterbatis.core.annotation.Controller;
import org.zj.winterbatis.core.annotation.RequestMapping;
import org.zj.winterbatis.core.annotation.ResponceBody;
import org.zj.winterbatis.core.redis.RedisTemplate;

@Controller
@RequestMapping("/redis")
public class RedisController {

    @Autofired
    RedisTemplate<String,Student> redisTemplate;

    @RequestMapping("/setVal")
    @ResponceBody
    public String ss(){
        redisTemplate.opsForValue.set("445000",new Student("asdf","asdffff"));
        return "";
    }

    @RequestMapping("/getVal")
    @ResponceBody
    public void sss(){

        Student student = (Student) redisTemplate.opsForValue().get("445000");
        System.out.println("获得的数据    "+student);

    }


    @RequestMapping("/setList")
    @ResponceBody
    public void dsssd(){

        redisTemplate.opsForList.add("789123",new Student("张君","zhangjun249"));
        redisTemplate.opsForList.add("789123",new Student("历史","asdfasdf61"));

    }

    @RequestMapping("/getList")
    @ResponceBody
    public void sasdfasdf(){
        for(Student s:redisTemplate.opsForList.get("789123")){
            System.out.println("获得的数据:-------->   "+s);
        }
    }

}
