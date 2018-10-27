package org.zj.winterbatis;


import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.zj.winterbatis.scheluding.GhostContext;

import java.lang.reflect.Method;

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
