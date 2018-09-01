package org.zj.winterbatis.core;

import java.lang.reflect.Method;

/**
 * Created by ZhangJun on 2018/7/7.
 */
public class Invoke {
    Object obj;
    Method method;
    String requestMethod;
    public Invoke() {
    }

    public Invoke(Object obj, Method method, String requestMethod) {
        this.obj = obj;
        this.method = method;
        this.requestMethod = requestMethod;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
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
