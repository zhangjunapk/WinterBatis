package org.zj.winterbatis.core.classhandler;

import org.zj.winterbatis.core.annotation.Scheduling;
import org.zj.winterbatis.core.util.TaskUtil;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 定时任务类处理器，用于开启定时任务
 */
public class TaskClassHandler extends AbsClassHandler {

    @Override
    public void handleClass(List<Class> classes) throws Exception {
        for (Class c : classes) {
            for (Method m : c.getDeclaredMethods()) {
                if (!m.isAnnotationPresent(Scheduling.class))
                    continue;

                Scheduling scheduling = m.getAnnotation(Scheduling.class);
                String cron = scheduling.cron();

                //开始创建任务
                TaskUtil.startTask(cron, m, c.newInstance());
            }
        }
    }
}
