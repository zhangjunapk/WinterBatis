package org.zj.winterbatis.core.invocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @BelongsProject: WinterBatis
 * @BelongsPackage: org.zj.winterbatis.core
 * @Author: Java
 * @CreateTime: 2018-09-22 11:21
 * @Description: ${Description}
 */
public class TaskInvocationHandler implements InvocationHandler {

    private Object object;
    private Method method;

    public TaskInvocationHandler(Method m,Object obj){
        this.method=m;
        this.object=obj;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        System.out.println("我被调用了-------------");

        method.invoke(object,args);
        return null;
    }
}
