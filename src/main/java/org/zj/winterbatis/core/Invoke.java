package org.zj.winterbatis.core;

import java.lang.reflect.Method;

/**
 * Created by ZhangJun on 2018/7/7.
 */
public class Invoke {
    Object obj;
    Method method;

    public Invoke() {
    }


    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Invoke( Object obj, Method method) {
        this.obj = obj;
        this.method = method;
    }

    @Override
    public String toString() {
        return "Invoke{" +
                "obj=" + obj +
                ", method=" + method +
                '}';
    }
}
