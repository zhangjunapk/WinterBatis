package org.zj.winterbatis.classhandler;

import org.quartz.SchedulerException;
import org.zj.winterbatis.util.ClassUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Properties;

/**
 * 抽象类处理器，不变的方法就是获得配置文件，变的方法就是处理每个类
 */
public abstract class AbsClassHandler {
    private static Properties properties=new Properties();
   public abstract void handleClass(Class c) throws Exception;
    static{
        try {

            properties.load(new FileInputStream(ClassUtil.getClassPath()+"\\application.properties"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Properties getProperties(){
        return properties;
    }
}
