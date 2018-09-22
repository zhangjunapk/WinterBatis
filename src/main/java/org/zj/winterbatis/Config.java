package org.zj.winterbatis;

import org.zj.winterbatis.annotation.*;

/**
 * Created by ZhangJun on 2018/7/7.
 */
@MapperScan("org.zj.winterbatis.dao")
@IsMaven(true)
@ViewPrefix("/html/")
@ViewSuffix(".html")
@WebPath("webapp")
//132.232.105.60
@Rabbit(host = "localhost",port = 5672,username = "guest",password = "guest")
@DataSource(username = "root",password = "",url="jdbc:mysql://localhost:3306/bilibili",driver = "com.mysql.jdbc.Driver")
public class Config {
    public static void main(String[] args) {
        //在这里启动web服务器并且初始化框架
    }
}
