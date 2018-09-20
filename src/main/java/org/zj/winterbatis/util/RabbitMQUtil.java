package org.zj.winterbatis.util;

import org.zj.winterbatis.annotation.Rabbit;
import org.zj.winterbatis.annotation.RabbitProducter;
import org.zj.winterbatis.core.RabbitMQProducterInvocationHandler;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class RabbitMQUtil {
    /**
     * 根据这些来监听队列然后执行方法
     * @param method
     * @param object
     * @param queueName
     */
    public static void listenQueue(Method method,Object object,String queueName){
        //这里还要根据方法接收的参数类型来做转换

        //比如接收Student参数，那么就要对接收到的信息通过json工具来转换成对象
    }

    /**
     * 通过这些来获得一个代理类
     * @param rabbit
     * @param c
     * @param rabbitProducter
     * @return
     */
    public static Object getProxyObject(Rabbit rabbit, Class c, RabbitProducter rabbitProducter){
        return Proxy.newProxyInstance(c.getClassLoader(), new Class[]{c}, new RabbitMQProducterInvocationHandler(rabbit,rabbitProducter));
    }

}
