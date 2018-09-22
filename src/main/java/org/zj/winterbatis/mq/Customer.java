package org.zj.winterbatis.mq;


import org.zj.winterbatis.annotation.Component;
import org.zj.winterbatis.annotation.RabbitListener;
import org.zj.winterbatis.bean.Student;

@Component
public class Customer {
    /**
     * 监听studentQueue队列的消息，并将每个消息转换成对象来处理
     * @param student
     */
    @RabbitListener(routeKey = "test.*",exchangeName = "test",queue = "test.student")
    public void handleMessage(Student student){
        System.out.println("收到对象信息");
        System.out.println(student);
    }

    @RabbitListener(routeKey = "test.*",exchangeName = "test",queue = "test.student")
    public void handleMessagee(String student){
        System.out.println("收到字符串信息");
        System.out.println(student);
    }

}
