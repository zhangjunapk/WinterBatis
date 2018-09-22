package org.zj.winterbatis;


import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.zj.winterbatis.annotation.BaseMapper;
import org.zj.winterbatis.annotation.Delete;
import org.zj.winterbatis.annotation.Mapper;
import org.zj.winterbatis.scheluding.GhostContext;
import org.zj.winterbatis.util.AnnotationUtil;
import org.zj.winterbatis.util.ClassUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Created by ZhangJun on 2018/7/7.
 */
public class Content {
    public static void main(String[] args) throws Exception {
/*
        System.out.println(ClassUtil.isListReturn(DoThing.class.getMethod("get")));
        System.out.println(ClassUtil.getListGeneric(DoThing.class.getMethod("get")));
        System.out.println(AnnotationUtil.getMethodAnnotationVal("value",DoThing.class.getMethod("get"), Delete.class));
*/

/*

        Type[] genericInterfaces = BaseMapper.class.getGenericInterfaces();
        for(Type t:genericInterfaces){
            System.out.println(t.getTypeName());
        }

*/


        Class<? extends Job> aClass = new Job() {
            @Override
            public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

                System.out.println("我在啊");

            }
        }.getClass();



        Method execute = aClass.getMethod("execute", JobExecutionContext.class);
        execute.invoke(aClass.newInstance(),new Object[]{new GhostContext()});

    }
}
