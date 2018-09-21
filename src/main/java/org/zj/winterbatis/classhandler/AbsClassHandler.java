package org.zj.winterbatis.classhandler;

import org.quartz.SchedulerException;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Properties;

public abstract class AbsClassHandler {
    private static Properties properties;
   public abstract void handleClass(Class c) throws IOException, SchedulerException, IllegalAccessException, InstantiationException, ParseException;
    static{
        properties=new Properties();
        try {
            properties.load(new FileInputStream("application.properties"));
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public Properties getProperties(){
        return properties;
    }
}
