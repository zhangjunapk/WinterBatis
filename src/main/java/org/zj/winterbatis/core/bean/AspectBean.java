package org.zj.winterbatis.core.bean;

import java.util.List;

/**
 * Created by ZhangJun on 2018/7/7.
 */
public class AspectBean {
    List<Invoke> before;
    List<Invoke> after;

    public AspectBean() {
    }

    public List<Invoke> getBefore() {
        return before;
    }

    public void setBefore(List<Invoke> before) {
        this.before = before;
    }

    public List<Invoke> getAfter() {
        return after;
    }

    public void setAfter(List<Invoke> after) {
        this.after = after;
    }

    public AspectBean(List<Invoke> before, List<Invoke> after) {
        this.before = before;
        this.after = after;
    }

    @Override
    public String toString() {
        return "AspectBean{" +
                "before=" + before +
                ", after=" + after +
                '}';
    }
}
