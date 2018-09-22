package org.zj.winterbatis.util;

import jdk.internal.org.objectweb.asm.ClassWriter;
import org.quartz.*;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.zj.winterbatis.core.TaskInvocationHandler;
import org.zj.winterbatis.scheluding.J;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * 任务调度工具类
 */
public class TaskUtil {
    /**
     * 通过cron 表达式和方法和对象来开启任务
     * @param cron
     * @param method
     * @param obj
     */
    public static void startTask(String cron, Method method,Object obj) throws ParseException, SchedulerException, InvocationTargetException, IllegalAccessException, IOException, ClassNotFoundException {
        CronTriggerImpl cronTrigger = new CronTriggerImpl();
        CronExpression cexp = new CronExpression(cron);

        cronTrigger.setCronExpression(cexp);

        SchedulerFactory schedulerFactory = new StdSchedulerFactory();

        Scheduler scheduler = schedulerFactory.getScheduler();

        JobDetail jobDetail=new JobDetailImpl(System.currentTimeMillis()+"", new Job() {
            @Override
            public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
                try {
                    method.invoke(obj);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }.getClass());

        cronTrigger.setName(System.currentTimeMillis()+"");
        scheduler.scheduleJob(jobDetail, cronTrigger);

        scheduler.start();
    }
}
