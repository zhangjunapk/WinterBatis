package org.zj.winterbatis.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import org.zj.winterbatis.annotation.Rabbit;
import org.zj.winterbatis.annotation.RabbitProducter;
import org.zj.winterbatis.util.ValUtil;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class RabbitMQProducterInvocationHandler implements InvocationHandler {

    private String host;
    private int port;
    private String username;
    private String password;

    private RabbitProducter rabbitProducter;
    private Rabbit rabbit;
    private String routeKey;
    private String[] queueName;
    private String exchangeName;

    private ConnectionFactory factory;
    private Connection connection;
    private Channel channel;
    private AMQP.BasicProperties bp;
    private boolean inited=false;


    public RabbitMQProducterInvocationHandler(Rabbit rabbit, RabbitProducter rabbitProducter){
        this.rabbit=rabbit;
        this.rabbitProducter=rabbitProducter;
        this.host=host;
        this.port=port;
        this.username=username;
        this.password=password;

        this.routeKey=routeKey;
        this.queueName=queueName;
        this.exchangeName=exchangeName;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if(!inited)
            init();

        //直接发送String
        if(method.getName().equals("sendStringMessage")){
            channel.basicPublish(exchangeName,routeKey,false,bp, ValUtil.parseString(args[0]).getBytes());
        }
        //转换成json后再发送
        if(method.getName().equals("sendObjectMessage")){
            channel.basicPublish(exchangeName,routeKey,false,bp, new ObjectMapper().writeValueAsString(args[0]).getBytes());
        }

        return null;
    }

    /**
     * 对生产者进行初始化
     */
    private void init() throws IOException, TimeoutException {
        this.host=rabbit.host();
        this.port=rabbit.port();
        this.username=rabbit.username();
        this.password=rabbit.password();
        this.queueName=rabbitProducter.queueName();
        this.routeKey=rabbitProducter.routeKey();
        this.exchangeName=rabbitProducter.exchangeName();


        factory=new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        factory.setUsername(username);
        factory.setPassword(password);
        connection=factory.newConnection();
        channel=connection.createChannel();
        channel.exchangeDeclare(exchangeName, BuiltinExchangeType.DIRECT,true);

        for(String queue:queueName){
            channel.queueBind(queue,exchangeName,routeKey);
        }
        Map<String,Object> header=new HashMap<>();
        header.put("charset","utf-8");
        AMQP.BasicProperties.Builder b=new AMQP.BasicProperties.Builder();
        b.headers(header);
         bp=b.build();
    }
}
