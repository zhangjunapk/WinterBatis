package org.zj.winterbatis;

import org.zj.winterbatis.core.annotation.*;

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
@RedisConfiguration(host="localhost",port=6379)
public class Config {
    public static void main(String[] args) {
        //后面会像springboot一样通过一个Application来开启web服务器并初始化框架
    }
}
