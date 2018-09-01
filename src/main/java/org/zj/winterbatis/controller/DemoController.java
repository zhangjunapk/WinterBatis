package org.zj.winterbatis.controller;

import org.zj.winterbatis.annotation.Controller;
import org.zj.winterbatis.annotation.RequestMapping;
import org.zj.winterbatis.annotation.RequestParam;
import org.zj.winterbatis.annotation.ResponceBody;

import java.util.Arrays;

/**
 * Created by ZhangJun on 2018/9/1.
 */
@Controller
@RequestMapping("/demo")
public class DemoController {

    @RequestMapping("/param")
    @ResponceBody
    public String param(@RequestParam("name")Integer[] name){
        return "这是从请求中获取的参数  "+ Arrays.toString(name);
    }
}
