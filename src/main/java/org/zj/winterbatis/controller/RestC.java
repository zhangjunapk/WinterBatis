package org.zj.winterbatis.controller;

import org.zj.winterbatis.annotation.*;

/**
 * Created by ZhangJun on 2018/9/1.
 */
@RestController
@RequestMapping("/rest")
public class RestC {
    @GetMapping("/get")
    public String s() {
        return "get";
    }

    @PostMapping("/post")
    public String post() {
        return "post";
    }

    @DeleteMapping("/delete")
    public String delete() {
        return "delete";
    }

    @PutMapping("/put")
    public String put() {
        return "put";
    }
}
