package org.zj.winterbatis.classhandler;

import org.zj.winterbatis.annotation.Scheduling;
import org.zj.winterbatis.util.TaskUtil;

import java.lang.reflect.Method;

/**
 * 定时任务类处理器，用于开启定时任务
 */
public class TaskClassHandler extends AbsClassHandler {

    @Override
    public void handleClass(Class c) throws Exception {
        for(Method m:c.getDeclaredMethods()){
            if(!m.isAnnotationPresent(Scheduling.class))
                continue;

            Scheduling scheduling= m.getAnnotation(Scheduling.class);
            String cron=scheduling.cron();

            //开始创建任务
            TaskUtil.startTask(cron,m,c.newInstance());
        }
    }
}
