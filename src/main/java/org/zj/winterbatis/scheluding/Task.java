package org.zj.winterbatis.scheluding;

import org.zj.winterbatis.annotation.Component;
import org.zj.winterbatis.annotation.Scheduling;

/**
 * @BelongsProject: WinterBatis
 * @BelongsPackage: org.zj.winterbatis.scheluding
 * @Author: ZhangJun
 * @CreateTime: 2018-09-21 21:58
 * @Description: ${Description}
 */
@Component
public class Task {

    @Scheduling(cron = "*/5 * * * * ?")
    public void vv(){
        System.out.println("jjjjjjjjjjjjjjjjjjj");
    }
}
