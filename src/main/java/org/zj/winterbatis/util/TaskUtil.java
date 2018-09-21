package org.zj.winterbatis.util;

import org.quartz.*;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.triggers.CronTriggerImpl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;

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
    public static void startTask(String cron, Method method,Object obj) throws ParseException, SchedulerException {
        CronTriggerImpl cronTrigger = new CronTriggerImpl();
        CronExpression cexp = new CronExpression(cron);

        cronTrigger.setCronExpression(cexp);

        SchedulerFactory schedulerFactory = new StdSchedulerFactory();

        Scheduler scheduler = schedulerFactory.getScheduler();

        JobDetail jobDetail=new JobDetailImpl();
        ((JobDetailImpl) jobDetail).setJobClass(new Job() {
            @Override
            public void execute(JobExecutionContext context) throws JobExecutionException {
                try {
                    method.invoke(obj);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }.getClass());

        scheduler.scheduleJob(jobDetail, cronTrigger);

        scheduler.start();
    }
}
