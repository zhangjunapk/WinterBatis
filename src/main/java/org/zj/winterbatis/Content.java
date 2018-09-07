package org.zj.winterbatis;


import org.zj.winterbatis.annotation.Delete;
import org.zj.winterbatis.annotation.Mapper;
import org.zj.winterbatis.util.AnnotationUtil;
import org.zj.winterbatis.util.ClassUtil;

/**
 * Created by ZhangJun on 2018/7/7.
 */
public class Content {
    public static void main(String[] args) throws Exception {
        System.out.println(ClassUtil.isListReturn(DoThing.class.getMethod("get")));
        System.out.println(ClassUtil.getListGeneric(DoThing.class.getMethod("get")));
        System.out.println(AnnotationUtil.getMethodAnnotationVal("value",DoThing.class.getMethod("get"), Delete.class));


        for(Class c: Mapper.class.getInterfaces()){
            System.out.println(c.isInterface()+"  "+c.getName());
        }

    }
}
