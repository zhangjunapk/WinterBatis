package org.zj.winterbatis.controller;

import org.zj.winterbatis.core.annotation.*;
import org.zj.winterbatis.bean.Teacher;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZhangJun on 2018/9/8.
 */
@RestController
public class ResponceController {
    @GetMapping("/jj")
    @AutoRestfulResponce
    public List<Teacher> ss(){
        List<Teacher> result=new ArrayList<>();
        result.add(new Teacher(1,"丁丁",24));
        result.add(new Teacher(2,"龙龙",27));
        result.add(new Teacher(3,"桐桐",35));
        result.add(new Teacher(4,"钊钊",27));
        result.add(new Teacher(5,"雨雨",18));
        result.add(new Teacher(6,"奇奇",34));
        return result;
    }
}
