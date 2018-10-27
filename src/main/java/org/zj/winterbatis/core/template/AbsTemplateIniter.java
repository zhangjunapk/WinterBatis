package org.zj.winterbatis.core.template;

import java.lang.reflect.Field;

/**
 * 用于初始化模板，然后放到实例map
 * 不能直接放到isntanceMap，然后后面调用di,因为每个字段的泛型会不一样
 * 所以这里直接new isntance 然后直接注入进去
 */
public abstract class AbsTemplateIniter{

    private Object locaInstance;
    private Field field;
    public abstract boolean isTrue();

    /**
     * 获得要注入的东西
     * @return
     */
    public abstract Object getInstance();
    /**
     * 接受所在类的实例对象,
     * @param locaInstance
     */
    public AbsTemplateIniter(Field f, Object locaInstance){
        this.field=f;
        this.locaInstance=locaInstance;
    }
    /**
     * 生成模板，然后放到instanceMap
     */

    public Field getField(){
        return field;
    }

    /**
     * 获得字段所在的类,因为需要进行注入
     * @return
     */
    public Object getLocaInstance(){
        return locaInstance;
    }

    /**
     * 以后直接这样调用就行了
     */
    public void handle(){
        try{
        if(isTrue()){
            field.setAccessible(true);
            field.set(locaInstance,getInstance());
        }}catch (Exception e){
            e.printStackTrace();
        }
    }

}
