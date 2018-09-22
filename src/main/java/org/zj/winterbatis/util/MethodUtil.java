package org.zj.winterbatis.util;

import java.lang.reflect.Method;

/**
 * @BelongsProject: WinterBatis
 * @BelongsPackage: org.zj.winterbatis.util
 * @Author: ZhangJun
 * @CreateTime: 2018-09-22 14:42
 * @Description: ${Description}
 */
public class MethodUtil {
    /**
     * 获得方法的指定索引参数的类型
     * @param index
     * @param method
     * @return
     */
    public static Class getParamType(int index, Method method){
        Class<?>[] parameterTypes = method.getParameterTypes();
        return parameterTypes[0];
    }
}
