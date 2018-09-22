package org.zj.winterbatis.classhandler;

import org.quartz.SchedulerException;
import org.zj.winterbatis.annotation.Controller;
import org.zj.winterbatis.annotation.RestController;
import org.zj.winterbatis.classhandler.AbsClassHandler;
import org.zj.winterbatis.core.AspectBean;
import org.zj.winterbatis.util.AspectUtil;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;

/**
 * Controller 类处理器，用于对类进行实例化以及增强
 */
public class ControllerClassHandler extends AbsClassHandler {
    private Map<String, AspectBean> aspectBeanMap;
    private Map<String,Object> instanceMap;

    public ControllerClassHandler(Map<String,Object>instanceMap,Map<String,AspectBean> aspectBeanMap){
        this.instanceMap=instanceMap;
        this.aspectBeanMap=aspectBeanMap;
    }
    @Override
    public void handleClass(Class c) throws IOException, SchedulerException, IllegalAccessException, InstantiationException, ParseException {
        if (c.isAnnotationPresent(Controller.class) || c.isAnnotationPresent(RestController.class)) {
            //这里还要进行切面判断，看是否要生成代理类对方法进行增强
            AspectUtil aspectUtil=new AspectUtil(instanceMap,aspectBeanMap);
            Object enhanceAfterObj = aspectUtil.getEnhanceAfterObj(c);
            if (enhanceAfterObj!=null) {
                //你懂得
                //把增强后的代理类放进去
                instanceMap.put(c.getName(), enhanceAfterObj);
                System.out.println("生成需要增强的代理类:" + c.getName());
            }
            System.out.println("直接newinstance:" + c.getName());
            instanceMap.put(c.getName(), c.newInstance());
        }
    }
}
