package org.zj.winterbatis.util;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import org.zj.winterbatis.core.AspectBean;
import org.zj.winterbatis.core.CglibInvocationHandler;

import java.util.Map;

public class AspectUtil {

    private Map<String,Object> instanceMap;
    private Map<String,AspectBean> aspectBeanMap;
    public AspectUtil(Map<String,Object> instanceMap,Map<String,AspectBean> aspectBeanMap){
        this.aspectBeanMap=aspectBeanMap;
        this.instanceMap=instanceMap;
    }

    public Object getEnhanceAfterObj(Class c) throws IllegalAccessException, InstantiationException {
        //如果有接口，使用jdk动态代理

        if (instanceMap.get(c.getName()) == null) {
            instanceMap.put(c.getName(), c.newInstance());
        }

        //因为类全名包含key,应该是模糊匹配，而不是全匹配
        AspectBean aspectBean = getAspectBean(c.getName());

        //jdk动态代理生成代理类对方法进行增强有毒

        Enhancer enhancer = new Enhancer();
        enhancer.setCallbacks(new Callback[]{new CglibInvocationHandler(aspectBean.getBefore(), aspectBean.getAfter(), instanceMap.get(c.getName()))});
        enhancer.setSuperclass(c);
        System.out.println("          ......>>>使用cglib 增强方法    " + c.getName());
        return enhancer.create();
    }

    private AspectBean getAspectBean(String name) {
        for (Map.Entry<String, AspectBean> entry : aspectBeanMap.entrySet()) {
            if (name.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    private boolean needEnhance(String className) {
        for (String k : aspectBeanMap.keySet()) {
            if (className.contains(k)) {
                return true;
            }
        }
        return false;
    }


}
