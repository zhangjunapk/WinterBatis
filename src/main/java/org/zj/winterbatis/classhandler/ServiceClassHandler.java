package org.zj.winterbatis.classhandler;

import org.quartz.SchedulerException;
import org.zj.winterbatis.annotation.Service;
import org.zj.winterbatis.core.AspectBean;
import org.zj.winterbatis.util.AspectUtil;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;

public class ServiceClassHandler extends AbsClassHandler {

    private Map<String, AspectBean> aspectBeanMap;
    private Map<String,Object> instanceMap;

    public ServiceClassHandler(Map<String,Object>instanceMap,Map<String,AspectBean> aspectBeanMap){
        this.instanceMap=instanceMap;
        this.aspectBeanMap=aspectBeanMap;
    }

    @Override
    public void handleClass(Class c) throws IOException, SchedulerException, IllegalAccessException, InstantiationException, ParseException {

        AspectUtil aspectUtil=new AspectUtil(instanceMap,aspectBeanMap);

        if (c.isAnnotationPresent(Service.class)) {
            Object o = c.newInstance();
            Object enhanceAfterObj = aspectUtil.getEnhanceAfterObj(c);
            if (enhanceAfterObj!=null) {
                o = enhanceAfterObj;
                System.out.println("  需要增强:  " + c.getName());
            }
            //这里还要进行page处理
            //生成处理page之后的代理类
            //Object oo=getPageProxyObject(o);
            instanceMap.put(c.getName(), o);
            //还要遍历这个service的所有接口,用于后面注入
            for (Class interfaceClass : c.getInterfaces()) {
                instanceMap.put(interfaceClass.getName(), o);
                System.out.println("      接口放进去:" + interfaceClass.getName());
            }
        }

    }






}
