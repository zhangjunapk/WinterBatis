package org.zj.winterbatis.aspect;

import org.zj.winterbatis.annotation.After;
import org.zj.winterbatis.annotation.Aspect;
import org.zj.winterbatis.annotation.Before;
import org.zj.winterbatis.annotation.Condition;
/**
 * Created by ZhangJun on 2018/7/7.
 */
@Condition("org.zj.winterbatis.service")
@Aspect
public class DemoAspect {
    @Before
    public void jj(){
        System.out.println("我是之前的方法");
    }
    @Before
    public void d(){
        System.out.println("我也是之前的方法");
    }
    @After
    public void a(){
        System.out.println("我是之后的方法");
    }

}
