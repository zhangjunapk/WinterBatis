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
    @RabbitListener(queue = "studentQueue")
    public void handleMessage(Student student){
        System.out.println(student);
    }

}
