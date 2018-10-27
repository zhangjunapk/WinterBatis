package org.zj.winterbatis.core.util;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.quartz.*;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.zj.winterbatis.core.annotation.Scheduling;
import org.zj.winterbatis.core.bean.Task;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * 任务调度工具类
 */
public class TaskUtil {

    private static List<String> autoTaskClassNames=new ArrayList<>();

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

    /**
     * 监听指定的配置文件，然后通过配置文件来添加任务
     * @param file
     */
    public static void listenConfig(File file) throws IOException {

        new Thread(new Runnable() {
            @Override
            public void run() {
                for(;;){
                    try{
                    //把json读取出来
                    byte[] buffer=new byte[1024];
                    FileInputStream fis=new FileInputStream(file);
                    int len=-1;
                    StringBuilder sb=new StringBuilder();
                    while((len=fis.read(buffer))!=-1){
                        sb.append(new String(buffer,0,len));
                    }
                    fis.close();

                    JavaType javaType = new ObjectMapper().getTypeFactory().constructParametricType(ArrayList.class, Task.class);

                    List<Task> tasks = new ObjectMapper().readValue(sb.toString(), javaType);

                    for(Task t:tasks){
                        String javaPath = t.getJavaPath();
                        String className=t.getClassName();
                        //把这个路径的java文件编译成class,然后
                        Class compile = ClassUtil.getCompile(className, new File(javaPath), new File("d:/cc"));

                        //如果这个任务已经在执行了就返回
                        if(autoTaskClassNames.contains(className))
                            continue;

                        //这里先对里面的字段进行注入吧，后面加
                        Object o = compile.newInstance();
                        //获得所有方法，然后再编译
                        for(Method m:compile.getDeclaredMethods()){
                            if(!m.isAnnotationPresent(Scheduling.class))
                                return;
                            Scheduling annotation = m.getAnnotation(Scheduling.class);
                            startTask(annotation.cron(),m,o);
                        }

                    }

                }catch (Exception e){

                    }
                }
            }
        }).start();

    }

}
