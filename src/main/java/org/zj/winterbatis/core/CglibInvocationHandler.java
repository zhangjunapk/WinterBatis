package org.zj.winterbatis.core;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.zj.winterbatis.annotation.Page;
import org.zj.winterbatis.annotation.Service;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by ZhangJun on 2018/7/7.
 */
public class CglibInvocationHandler implements MethodInterceptor {

    List<Invoke> before;
    List<Invoke> after;
    Object obj;
    public CglibInvocationHandler(List<Invoke> before, List<Invoke> after, Object obj){
        this.before=before;
        this.after=after;
        this.obj=obj;
    }


    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        System.out.println("在cglib中进来了");
        for(Invoke invoke:before){
            invoke.getMethod().invoke(invoke.getObj(),objects);
        }

        System.out.println("      代理的方法"+methodProxy);
        Object o1 = methodProxy.invokeSuper(o, objects);

        for(Invoke invoke:after){
            invoke.getMethod().invoke(invoke.getObj(),objects);
        }

        if(obj.getClass().isAnnotationPresent(Service.class)&&method.isAnnotationPresent(Page.class)){

            System.out.println("cglib对service的page注解进行处理");

            //需要对方法进行处理
            //说明需要进行分页
            Page page=(Page)method.getAnnotation(Page.class);
            int pageNum=page.page();
            int rows=page.rows();
            PageHelper.startPage(pageNum,rows);
            o1=methodProxy.invokeSuper(o, objects);
            try {

                return new PageInfo((List) o1);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("返回值并不是List类型");
                return methodProxy.invokeSuper(o,objects);
            }
        }
        //看这个类是否是service,看这个方法是否加了page注解
        return o1;
    }
}
