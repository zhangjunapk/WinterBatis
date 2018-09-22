package org.zj.winterbatis.classhandler;

import com.alibaba.druid.pool.DruidDataSource;
import org.quartz.SchedulerException;
import org.zj.winterbatis.annotation.BaseMapper;
import org.zj.winterbatis.annotation.Mapper;
import org.zj.winterbatis.core.BaseMapperInvocationHandler;
import org.zj.winterbatis.core.MapperInvocationHandler;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.text.ParseException;
import java.util.Map;

/**
 * mapper处理器，用于生成代理类
 */
public class MapperClassHandler extends AbsClassHandler {

    private DruidDataSource druidDataSource;
    private Map<String,Object> instanceMap;

    public MapperClassHandler(DruidDataSource druidDataSource,Map<String,Object> instanceMap){
        this.druidDataSource=druidDataSource;
        this.instanceMap=instanceMap;
    }

    @Override
    public void handleClass(Class c) throws IOException, SchedulerException, IllegalAccessException, InstantiationException, ParseException {
        // TODO: 2018/9/7 有问题
        if (c.isAnnotationPresent(BaseMapper.class) && c.isInterface()) {
            //通过mapperInvocationHandler生成代理类
            //传进去数据源和类
            InvocationHandler mapperInvocationHandler = new BaseMapperInvocationHandler(c, druidDataSource);
            //这里生成的代理类总是空的
            Object o = Proxy.newProxyInstance(c.getClassLoader(), new Class[]{c}, mapperInvocationHandler);
            System.out.println("           >>>>>>>>" + c.getName());
            instanceMap.put(c.getName(), o);
            System.out.println("生成代理类:" + c.getName());
        }


        if (c.isAnnotationPresent(Mapper.class) && c.isInterface()) {
            //通过mapperInvocationHandler生成代理类
            //传进去数据源和类
            MapperInvocationHandler mapperInvocationHandler = new MapperInvocationHandler(druidDataSource, c);
            //这里生成的代理类总是空的
            Object o = Proxy.newProxyInstance(c.getClassLoader(), new Class[]{c}, mapperInvocationHandler);


            System.out.println("           >>>>>>>>" + c.getName());
            instanceMap.put(c.getName(), o);
            System.out.println("生成代理类:" + c.getName());

        }
    }
}
