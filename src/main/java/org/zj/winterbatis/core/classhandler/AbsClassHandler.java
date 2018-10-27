package org.zj.winterbatis.core.classhandler;

import org.zj.winterbatis.core.util.ClassUtil;

import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;

/**
 * 抽象类处理器，不变的方法就是获得配置文件，变的方法就是处理每个类
 */
public abstract class AbsClassHandler {
    private static Properties properties=new Properties();
   public abstract void handleClass(List<Class> classes) throws Exception;
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
