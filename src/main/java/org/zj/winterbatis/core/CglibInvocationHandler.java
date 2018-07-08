package org.zj.winterbatis.core;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by ZhangJun on 2018/7/7.
 */
public class CglibInvocationHandler implements MethodInterceptor {

    List<Invoke> before;
    List<Invoke> after;
    Object obj;
    public CglibInvocationHandler(List<Invoke> before,List<Invoke> after,Object obj){
        this.before=before;
        this.after=after;
        this.obj=obj;
    }


    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        for(Invoke invoke:before){
            invoke.getMethod().invoke(invoke.getObj(),objects);
        }

        System.out.println("      代理的方法"+methodProxy);
        Object o1 = methodProxy.invokeSuper(o, objects);

        for(Invoke invoke:after){
            invoke.getMethod().invoke(invoke.getObj(),objects);
        }
        return o1;
    }
}
