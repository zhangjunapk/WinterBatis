package org.zj.winterbatis.classhandler;

import org.quartz.SchedulerException;
import org.zj.winterbatis.annotation.Scheduling;
import org.zj.winterbatis.util.TaskUtil;

import java.io.IOException;
import java.lang.reflect.Method;
import java.text.ParseException;

public class TaskClassHandler extends AbsClassHandler {

    @Override
    public void handleClass(Class c) throws IOException, SchedulerException, IllegalAccessException, InstantiationException, ParseException {
        for(Method m:c.getDeclaredMethods()){
            if(!m.isAnnotationPresent(Scheduling.class))
                continue;

            Scheduling scheduling= (Scheduling) c.getAnnotation(Scheduling.class);
            String cron=scheduling.cron();

            //开始创建任务
            TaskUtil.startTask(cron,m,c.newInstance());
        }
    }
}
