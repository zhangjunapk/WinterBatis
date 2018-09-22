package org.zj.winterbatis.classhandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.rabbitmq.client.*;
import org.zj.winterbatis.annotation.Component;
import org.zj.winterbatis.annotation.RabbitListener;
import org.zj.winterbatis.util.ClassUtil;
import org.zj.winterbatis.util.MethodUtil;
import org.zj.winterbatis.util.ValUtil;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeoutException;

/**
 * 消息消费者的类处理器，用于开启消费者的监听
 */
public class RabbitMQCustomerClassHandler extends AbsClassHandler {

    private ConnectionFactory factory;
    private Connection connection;
    private Channel channel;
    private Consumer consumer;

    private Object instance;

    private String[] queue;
    public RabbitMQCustomerClassHandler(RabbitListener rabbitListener) throws IOException, TimeoutException {
        factory=new ConnectionFactory();
        factory.setHost(getHost());
        factory.setPort(ValUtil.parseInteger(getPort()));
        factory.setUsername(getUsername());
        factory.setPassword(getPassword());
        connection=factory.newConnection();
        channel=connection.createChannel();

        queue = rabbitListener.queue();
        String exchangeName = rabbitListener.exchangeName();
        String routeKey = rabbitListener.routeKey();

        channel.exchangeDeclare(exchangeName, BuiltinExchangeType.DIRECT,true);
        for(String q:queue){
            channel.queueBind(q,exchangeName,routeKey);
        }
    }

    @Override
    public void handleClass(Class c) throws IOException, IllegalAccessException, InstantiationException {

        if(instance==null){
            instance=c.newInstance();
        }

        if(!c.isAnnotationPresent(Component.class))
            return;
        for(java.lang.reflect.Method m:c.getDeclaredMethods()){
            if(!m.isAnnotationPresent(RabbitListener.class))
                continue;

            //接下来开启监听

            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                                           byte[] body) throws IOException {

                    try {
                        Class generic = MethodUtil.getParamType(0,m);
                        if(!(generic==String.class)){
                            //调用json来讲对象进行转换，然后执行m方法

                            try {
                                Object o = new ObjectMapper().readValue(new String(body, "UTF-8"), generic);
                                m.invoke(instance, o);

                                System.out.println("我收到了对象的消息");
                                return;
                            }catch (Exception e){

                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    String message = new String(body, "UTF-8");

                    System.out.println("你知道吗 我收到字符串消息了");

                    System.out.println(" [x] Received '" + message + "'");

                    try {
                        m.invoke(instance,message);



                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            };
            for(String q:queue){
                channel.basicConsume(q, true, consumer);
            }
        }
    }

    private String getHost(){
        return getProperties().getProperty("rabbit.host");
    }
    private String getPort(){
        return getProperties().getProperty("rabbit.port");
    }

    private String getUsername(){
        return getProperties().getProperty("rabbit.username");
    }

    private String getPassword(){
        return getProperties().getProperty("rabbit.password");
    }



}
