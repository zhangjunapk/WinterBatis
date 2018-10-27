package org.zj.winterbatis.core.util;

import com.sun.xml.internal.bind.v2.model.annotation.FieldLocatable;
import org.zj.winterbatis.core.annotation.RedisConfiguration;
import org.zj.winterbatis.core.invocation.RedisOpsInvocationHandler;
import org.zj.winterbatis.core.redis.IListOps;
import org.zj.winterbatis.core.redis.IValueOps;
import org.zj.winterbatis.core.redis.RedisTemplate;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * 这个工具用于对模板进行操作
 */
public class TemplateUtil {

    /**
     * 这里通过代理来生成代理类，然后返回
     * @return
     */
    public static Object  getRedisTemplate(RedisConfiguration redisConfiguration,Field field){
        //这里获得生成操作值的代理类
        Object opsObj = Proxy.newProxyInstance(IValueOps.class.getClassLoader(), new Class[]{IValueOps.class},  new RedisOpsInvocationHandler(redisConfiguration,field));
        Object opsList = Proxy.newProxyInstance(IListOps.class.getClassLoader(), new Class[]{IListOps.class}, new RedisOpsInvocationHandler(redisConfiguration, field));

        RedisTemplate redisTemplate=new RedisTemplate();

        //接下来反射赋值进去
        for(Field f:redisTemplate.getClass().getDeclaredFields()){
            f.setAccessible(true);
            try {
                if (f.getType() == IValueOps.class)
                    f.set(redisTemplate, opsObj);

                if (f.getType() == IListOps.class)
                    f.set(redisTemplate, opsList);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return redisTemplate;
    }
}
