package org.zj.winterbatis.core.invocation;

import com.alibaba.fastjson.JSONObject;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import org.zj.winterbatis.core.annotation.RedisConfiguration;
import org.zj.winterbatis.core.util.ClassUtil;
import org.zj.winterbatis.core.util.JsonUtil;
import org.zj.winterbatis.core.util.RedisUtil;
import org.zj.winterbatis.core.util.ValUtil;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 这是处理RedisTemplate类的拦截器
 */
public class RedisOpsInvocationHandler implements InvocationHandler {
    private JedisPool jedisPool;
    private RedisConfiguration redisConfiguration;
    private Field field;

    public RedisOpsInvocationHandler(RedisConfiguration redisConfiguration, Field field) {
        this.redisConfiguration = redisConfiguration;
        this.field = field;
        initJedis();
    }

    /**
     * 初始化jedis
     */
    private void initJedis() {
        if (jedisPool == null) {
            synchronized (this) {
                if (jedisPool == null)
                    jedisPool = new JedisPool(new JedisPoolConfig(),
                            redisConfiguration.host(),
                            redisConfiguration.port(),
                            redisConfiguration.timeout(),
                            redisConfiguration.password() == null || redisConfiguration.password().equals("") ? null : redisConfiguration.password());
            }
        }
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        return handleMethod(o, method, objects);
    }

    /**
     * 拦截方法执行的
     *
     * @param o
     * @param method
     * @param objects
     * @return
     */
    private Object handleMethod(Object o, Method method, Object[] objects) throws ClassNotFoundException {
        String name = method.getName();
        if (name.equals("get")) {
            System.out.println("你调用了get方法-------->");
            //根据参数来获得数据
            String s = jedisPool.getResource().get(ValUtil.parseString(objects[0]));
            //返回了String字符串

            //获得Field的value泛型类型

            Class valClass = RedisUtil.getValClass(field);

            System.out.println("获得值的class 类型:------>   " + valClass);

            // TODO: 2018/10/27

            //然后根据这个的返回值来解析json
            return getResult(valClass, method, s);
        }
        if (method.equals("set"))
            System.out.println("你调用了set方法-------->");
        //如果是设置的话,直接放进去就行了
        jedisPool.getResource().set(ValUtil.parseString(objects[0]), JSONObject.toJSONString(objects[1]));

        if (method.equals("add")) {

            System.out.println("你调用了add 方法-------->");

            //这就要添加到list
            jedisPool.getResource().lpush(ValUtil.parseString(objects[0]), String.valueOf(JSONObject.toJSON(objects[1])));
        }
        return null;
    }

    /**
     * 根据方法返回值来解析json数据
     *
     * @param method
     * @param s
     * @return
     */
    private Object getResult(Class c, Method method, String s) throws ClassNotFoundException {
        System.out.println(s + "  要转换的bean");
        System.out.println(method + "   这是那个方法");

        if (ClassUtil.isListReturn(method)) {
            try {
                //这里判断当前要执行的方法的返回值
        /*if(ClassUtil.getGeneric(method)==String.class)
            return s;
*/
                //尝试将结果转成list结果
                return JsonUtil.convertList(s, c);
            } catch (Exception e) {
                e.printStackTrace();
                //这说明里面只有一个，但是json格式确实一个bean的格式,那就这样弄就行了
                List<Object> result=new ArrayList<>();
                result.add(JsonUtil.convertBean(s, c));
                return result;
            }
         }

        //那就直接转换成bean
        return JsonUtil.convertBean(s, c);
    }

}
