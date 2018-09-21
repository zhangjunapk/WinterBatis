package org.zj.winterbatis;

import org.zj.winterbatis.annotation.*;

/**
 * Created by ZhangJun on 2018/7/7.
 */
@BasePackage({"org.zj.winterbatis.service","org.zj.winterbatis.controller"})
@MapperScan("org.zj.winterbatis.dao")
@AspectScan({"org.zj.winterbatis.aspect"})
@IsMaven(true)
@ViewPrefix("/html/")
@ViewSuffix(".html")
@WebPath("webapp")
@Rabbit(host = "localhost",port = 15635,username = "admin",password = "admin")
@DataSource(username = "root",password = "",url="jdbc:mysql://localhost:3306/bilibili",driver = "com.mysql.jdbc.Driver")
public class Config {
    public static void main(String[] args) {
        //在这里启动web服务器并且初始化框架
    }
}
