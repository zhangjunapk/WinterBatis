package org.zj.winterbatis.core.classhandler;

import org.quartz.SchedulerException;
import org.zj.winterbatis.core.annotation.Service;
import org.zj.winterbatis.core.bean.AspectBean;
import org.zj.winterbatis.core.util.AspectUtil;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * service类处理器，用于生成类的实例和增强
 */
public class ServiceClassHandler extends AbsClassHandler {

    private Map<String, AspectBean> aspectBeanMap;
    private Map<String, Object> instanceMap;

    public ServiceClassHandler(Map<String, Object> instanceMap, Map<String, AspectBean> aspectBeanMap) {
        this.instanceMap = instanceMap;
        this.aspectBeanMap = aspectBeanMap;
    }

    @Override
    public void handleClass(List<Class> classes) throws IOException, SchedulerException, IllegalAccessException, InstantiationException, ParseException {

        for (Class c : classes) {
            AspectUtil aspectUtil = new AspectUtil(instanceMap, aspectBeanMap);

            if (c.isAnnotationPresent(Service.class)) {
                Object o = c.newInstance();
                Object enhanceAfterObj = aspectUtil.getEnhanceAfterObj(c);
                if (enhanceAfterObj != null) {
                    o = enhanceAfterObj;
                    System.out.println("  需要增强:  " + c.getName());
                }
                //在service的intercepter里面处理分页了
                instanceMap.put(c.getName(), o);

                //还要遍历这个service的所有接口,用于后面注入
                for (Class interfaceClass : c.getInterfaces()) {
                    instanceMap.put(interfaceClass.getName(), o);
                    System.out.println("      接口放进去:" + interfaceClass.getName());
                }
            }
        }
    }


}
