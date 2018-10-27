package org.zj.winterbatis.core.mq;

public interface RabbitMQProducter {

    /**
     * 发送消息的方法
     */
    //直接发送
    void sendStringMessage(String msg);

    //序列化成json然后发送
    void sendObjectMessage(Object obj);
}
