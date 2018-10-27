package org.zj.winterbatis.core.template;

import org.zj.winterbatis.core.annotation.RedisConfiguration;
import org.zj.winterbatis.core.redis.RedisTemplate;
import org.zj.winterbatis.core.util.RedisUtil;
import org.zj.winterbatis.core.util.TemplateUtil;

import java.lang.reflect.Field;
import java.util.Map;

public class RedisTemplateHandler extends AbsTemplateIniter {

    private RedisConfiguration redisConfiguration;

    /**
     * 接受所在类的实例对象,
     *
     * @param f
     * @param locaInstance
     */
    public RedisTemplateHandler(Field f, Object locaInstance) {
        super(f, locaInstance);
    }
    public RedisTemplateHandler(Field f,Object locaInstance,RedisConfiguration redisConfiguration){
        this(f,locaInstance);
        this.redisConfiguration=redisConfiguration;
    }

    @Override
    public boolean isTrue() {
        return getField().getType()==RedisTemplate.class;
    }

    @Override
    public Object getInstance() {
        //这里直接获得,然后返回
        return TemplateUtil.getRedisTemplate(redisConfiguration,getField());
    }
}
