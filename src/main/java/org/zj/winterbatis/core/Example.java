package org.zj.winterbatis.core;

import org.zj.winterbatis.util.FormateUtil;
import org.zj.winterbatis.util.ValUtil;

import javax.management.ValueExp;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ZhangJun on 2018/9/7.
 */
//用于通用mapper 查询条件的封装
public class Example<T> {

    private T type;

    private Map<String, String> equalMap = new HashMap<>();
    private Map<String, String> likeMap = new HashMap<>();


    public T getType() {
        return type;
    }

    public void setType(T type) {
        this.type = type;
    }

    public Map<String, String> getEqualMap() {
        return equalMap;
    }

    public void setEqualMap(Map<String, String> equalMap) {
        this.equalMap = equalMap;
    }

    public Map<String, String> getLikeMap() {
        return likeMap;
    }

    public void setLikeMap(Map<String, String> likeMap) {
        this.likeMap = likeMap;
    }

    public void andLike(String name, Object o) {
        if (ValUtil.isBlank(name))
            return;
        likeMap.put(FormateUtil.toLine(name), ValUtil.parseString(o));
    }

    public void andEqualsTo(String name, Object o) {
        if (ValUtil.isBlank(name))
            return;
        equalMap.put(FormateUtil.toLine(name), ValUtil.parseString(o));
    }

    public String getCondition() {
        if (equalMap.size() == 0 && likeMap.size() == 0)
            return "";
        String result ="";
        for (Map.Entry<String, String> entry : equalMap.entrySet()) {
            result += entry.getKey() + "=" + entry.getValue() + " and ";
        }
        for (Map.Entry<String, String> entry : likeMap.entrySet()) {
            result += entry.getKey() + " like " + entry.getValue() + " and ";
        }
        return result.substring(0, result.length() - 4);
    }
}
