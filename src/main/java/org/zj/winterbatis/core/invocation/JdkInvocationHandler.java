package org.zj.winterbatis.core.invocation;

import org.zj.winterbatis.core.bean.AspectBean;
import org.zj.winterbatis.core.bean.Invoke;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by ZhangJun on 2018/7/7.
 */
public class JdkInvocationHandler implements InvocationHandler {

    Object needProxy;
    AspectBean aspectBean;
    public JdkInvocationHandler(Object needProxy, AspectBean aspectBean){

        System.out.println("传过来的需要代理的类:"+needProxy);

        this.needProxy=needProxy;
        this.aspectBean=aspectBean;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if(aspectBean!=null) {
            for (Invoke invoke: aspectBean.getBefore()) {
                invoke.getMethod().invoke(invoke.getObj(), args);
            }
        }
        Object invoke1 = method.invoke(needProxy, args);

        if(aspectBean!=null) {
            for (Invoke invoke: aspectBean.getAfter()) {
                invoke.getMethod().invoke(invoke.getObj(), args);
            }
        }
        return invoke1;
    }
}
