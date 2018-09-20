package org.zj.winterbatis.controller;

import org.zj.winterbatis.annotation.*;
import org.zj.winterbatis.core.RabbitMQProducter;

@Controller
@RequestMapping("/mq")
public class MQController {

    @Autofired
    @RabbitProducter(routeKey = "test.demo.*",queueName = {"test.student","test.teacher"},exchangeName = "test")
    RabbitMQProducter rabbitMQProducter;

    @RequestMapping("/tt")
    @ResponceBody
    public void test(){
        rabbitMQProducter.sendStringMessage("我是消息 被发送了");
    }
}
