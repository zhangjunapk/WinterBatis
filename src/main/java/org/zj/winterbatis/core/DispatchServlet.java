package org.zj.winterbatis.core;

import com.alibaba.druid.pool.DruidDataSource;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.zj.winterbatis.annotation.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ZhangJun on 2018/7/7.
 */
public class DispatchServlet extends HttpServlet {
    boolean isMaven=false;
    Class config=Config.class;

    String webName;

    String viewPrefix;
    String viewSuffix;

    List<Class> classes=new ArrayList<>();

    List<String> basePackage=new ArrayList<>();
    List<String> aspectPackage=new ArrayList<>();
    List<String> mapperPackage=new ArrayList<>();

    DruidDataSource druidDataSource=new DruidDataSource();

    Map<String,Object> instanceMap=new HashMap<>();

    Map<String,Invoke> requestMappingMap=new HashMap<>();

    Map<String,AspectBean> aspectBeanMap=new HashMap<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        handleMapping(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleMapping(req,resp);
    }

    private void handleMapping(HttpServletRequest req, HttpServletResponse resp) {
        Invoke invoke = requestMappingMap.get(req.getRequestURI());

        System.out.println("请求:"+req.getRequestURI());

        if(invoke!=null){
            try {
                Object invoke1 = invoke.getMethod().invoke(invoke.getObj());

                System.out.println(invoke+"   这是执行的数据");


                //看你是返回json还是页面
                handleNext(invoke1,invoke.getMethod(),req,resp);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleNext(Object result, Method method, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        if(method.isAnnotationPresent(ResponceBody.class)){
            //往响应写入json
            writeJson(result,resp);
            return;
        }
        //往响应写入页面
        writePage(result,req,resp);
    }

    private void writePage(Object result, HttpServletRequest req,HttpServletResponse resp) throws Exception {
        //模板引擎
        String midPath = String.valueOf(result);

        String viewPath=getWebPath()+viewPrefix+midPath+viewSuffix;

        writeStaticView(viewPath,req,resp);

        System.out.println("这是页面");
    }

    private void writeStaticView(String viewPath, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        SAXReader saxReader=new SAXReader();
        Document read = saxReader.read(viewPath);

        System.out.println(resp+"    响应");

        resp.getWriter().write(getAfterPageStr(read));
    }

    //获得解析后的页面
    private String getAfterPageStr(Document read) {
        Element rootElement = read.getRootElement();

        recursion(rootElement);

        return "ffffff";
    }

    public void recursion(Element e){
        List<Element> elements = e.elements();
        for(Element ee:elements){
            recursion(ee);
        }
        System.out.println(e);

    }


    private void writeJson(Object result, HttpServletResponse resp) throws IOException {
        resp.setCharacterEncoding("utf-8");

        System.out.println("写入json"+new ObjectMapper().writeValueAsString(result));
        System.out.println(result);

        resp.getWriter().write(new ObjectMapper().writeValueAsString(result));

    }


    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        System.out.println("init");

        //从注解里面获得数据
        initAnnotation();

        //将切面中的对象保存到容器里
        initClass();

        //将切面中映射关系放到容器中
        try {
            initAspectContainer();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        //对service/controller/dao进行初始化
        try {
            instanceObject();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //对对象进行注入
        doDI();

        //扫描所有controller/然后将RequestMapping和对象/方法的对应关系放到容器里
        setUrlMapping();

        System.out.println("finish init");
    }

    private void initAspectContainer() throws IllegalAccessException, InstantiationException {

        //先对切面进行初始化
        for(Class c:classes){
            if(c.isAnnotationPresent(Aspect.class)){

                List<Invoke> before=new ArrayList<>();
                List<Invoke> after=new ArrayList<>();

                instanceMap.put(c.getName(),c.newInstance());
                Condition condition= (Condition) c.getAnnotation(Condition.class);
                for(Method m:c.getDeclaredMethods()){
                    if(m.isAnnotationPresent(Before.class)){
                        before.add(new Invoke(instanceMap.get(c.getName()),m));
                    }
                    if(m.isAnnotationPresent(After.class)){
                        after.add(new Invoke(instanceMap.get(c.getName()),m));
                    }
                }
                //添加到aspect容器里

                aspectBeanMap.put(condition.value(),new AspectBean(before,after));
            }
        }
    }

    private void initClass() {
        initAspectClass();
        initBaseClass();
        initMapperClass();
    }

    private void initMapperClass() {
        for(String packageName:mapperPackage){
            try {
                addClass(classes,packageName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void initBaseClass() {
        for(String packageName:basePackage){
            try {
                addClass(classes,packageName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void initAspectClass() {
        for(String packageName:aspectPackage){
            try {
                addClass(classes,packageName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void doDI() {
        for (Class c: classes) {

            System.out.println("       当前是"+c.getName());

            for (Field field: c.getDeclaredFields()) {
                if (field.isAnnotationPresent(Autofired.class)) {
                    try {
                        System.out.println("       为"+c.getName()+"注入"+field.getName());

                        field.setAccessible(true);
                        System.out.println("-------");
                        System.out.println(instanceMap.get(c.getName())!=null);
                        System.out.println(instanceMap.get(field.getType().getName())!=null);
                        System.out.println("----------");
                        field.set(instanceMap.get(c.getName()), instanceMap.get(field.getType().getName()));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }

    private void setUrlMapping() {
        //扫描所有
        for(Class c:classes){
            if(c.isAnnotationPresent(Controller.class)){

                RequestMapping requestMapping= (RequestMapping) c.getAnnotation(RequestMapping.class);
                String prefixMapping = requestMapping.value();

                for(Method m:c.getDeclaredMethods()){
                    if(m.isAnnotationPresent(RequestMapping.class)){
                        RequestMapping methodMapping=m.getAnnotation(RequestMapping.class);
                        Invoke invoke=new Invoke(instanceMap.get(c.getName()),m);
                        requestMappingMap.put(prefixMapping+methodMapping.value(),invoke);
                    }
                }
            }
        }
    }

    private void instanceObject() throws Exception {
        instanceMapper();
        instanceService();
        instanceController();
    }

    private void instanceController() throws Exception {

        for(Class c:classes){
            if(c.isAnnotationPresent(Controller.class)){
                //这里还要进行切面判断，看是否要生成代理类对方法进行增强
                if(needEnhance(c.getName())){
                    //你懂得
                    //把增强后的代理类放进去
                    instanceMap.put(c.getName(),getEnhanceAfterObj(c));
                    System.out.println("生成需要增强的代理类:"+c.getName());

                    continue;
                }
                System.out.println("直接newinstance:"+c.getName());
                instanceMap.put(c.getName(),c.newInstance());
            }
        }
    }

    private Object getEnhanceAfterObj(Class c) throws IllegalAccessException, InstantiationException {
        //如果有接口，使用jdk动态代理

        //因为类全名包含key,应该是模糊匹配，而不是全匹配
        AspectBean aspectBean = getAspectBean(c.getName());
        if(c.getInterfaces()!=null&&c.getInterfaces().length!=0){
            //使用jdk的动态代理

            List<Invoke> before=aspectBean!=null?aspectBean.getBefore():null;
            List<Invoke> after=aspectBean!=null?aspectBean.getAfter():null;


            if(instanceMap.get(c.getName())==null){
                instanceMap.put(c.getName(),c.newInstance());
            }
            JdkInvocationHandler jdkMethodInvocation=new JdkInvocationHandler(instanceMap.get(c.getName()),aspectBean);
            Object o = Proxy.newProxyInstance(jdkMethodInvocation.getClass().getClassLoader(), c.getInterfaces(), jdkMethodInvocation);

            System.out.println("        ---使用jdk增强方法"+c.getName());

            return o;
        }
        //如果没有接口使用cglib进行增强

        Enhancer enhancer = new Enhancer();
        enhancer.setCallbacks(new Callback[]{new CglibInvocationHandler(aspectBean.getBefore(), aspectBean.getAfter(),instanceMap.get(c.getName()))});
        enhancer.setSuperclass(c);
        System.out.println("          ......>>>使用cglib 增强方法    "+c.getName());
        return enhancer.create();
    }

    private AspectBean getAspectBean(String name) {
        for(Map.Entry<String,AspectBean>entry:aspectBeanMap.entrySet()){
            if(name.contains(entry.getKey())){
                return entry.getValue();
            }
        }
        return null;
    }

    private boolean needEnhance(String className) {
        for(String k:aspectBeanMap.keySet()){
            if(className.contains(k)){
                return true;
            }
        }
        return false;
    }


    private void instanceService() throws Exception {
        for(Class c:classes){
            if(c.isAnnotationPresent(Service.class)){
                Object o=c.newInstance();
                if( needEnhance(c.getName())){
                    o=getEnhanceAfterObj(c);
                    System.out.println("  需要增强:  "+c.getName());
                }
                instanceMap.put(c.getName(),o);
                //还要遍历这个service的所有接口,用于后面注入
                for(Class interfaceClass:c.getInterfaces()){
                    instanceMap.put(interfaceClass.getName(),o);
                    System.out.println("      接口放进去:"+interfaceClass.getName());
                }
            }
        }
    }

    private void instanceMapper() {
        for(Class c:classes){
            if(c.isAnnotationPresent(Mapper.class)&&c.isInterface()){
                //通过mapperInvocationHandler生成代理类
                //传进去数据源和类
                MapperInvocationHandler mapperInvocationHandler=new MapperInvocationHandler(druidDataSource);
                //这里生成的代理类总是空的
                Object o = Proxy.newProxyInstance(c.getClassLoader(), new Class[]{c}, mapperInvocationHandler);



                System.out.println("           >>>>>>>>"+c.getName());
                instanceMap.put(c.getName(),o);
                System.out.println("生成代理类:"+c.getName());

            }
        }
    }


    private void initAnnotation() {
        BasePackage basePackage= (BasePackage) config.getAnnotation(BasePackage.class);
        MapperScan mapperScan= (MapperScan) config.getAnnotation(MapperScan.class);
        AspectScan aspectScan= (AspectScan) config.getAnnotation(AspectScan.class);

        WebPath webPath= (WebPath) config.getAnnotation(WebPath.class);
        this.webName=webPath.value();

        ViewPrefix viewPrefix= (ViewPrefix) config.getAnnotation(ViewPrefix.class);
        ViewSuffix viewSuffix= (ViewSuffix) config.getAnnotation(ViewSuffix.class);

        this.viewSuffix=viewSuffix.value();
        this.viewPrefix=viewPrefix.value();

        for(String s:basePackage.value()){
            this.basePackage.add(s);
        }
        for(String s:mapperScan.value()){
            this.mapperPackage.add(s);
        }
        for(String s:aspectScan.value()){
            this.aspectPackage.add(s);
        }

        DataSource dataSource= (DataSource) config.getAnnotation(DataSource.class);

        druidDataSource.setDriverClassName(dataSource.driver());
        druidDataSource.setUsername(dataSource.username());
        druidDataSource.setUrl(dataSource.url());
        druidDataSource.setPassword(dataSource.password());

        IsMaven isMaven= (IsMaven) config.getAnnotation(IsMaven.class);
        this.isMaven=isMaven.value();
    }

    private void addClass(List<Class> classes,String packgeName) throws Exception{
        ClassLoader classLoader = DispatchServlet.class.getClassLoader();
        packgeName=packgeName.replace(".","/");
        String path=getPackagePath(packgeName,this.isMaven);

        File file=new File(path);

        System.out.println(file.getAbsolutePath());

        inflateClass(classes,file);
    }

    public void inflateClass(List<Class> classes,File file){
        if(file.isDirectory()){
            for(File f:file.listFiles()){
                inflateClass(classes,f);
            }
        }
        if(file.isFile()){
            handleAddClass(classes,file);
        }
    }

    private void handleAddClass(List<Class> classes, File file) {


        if(file.getAbsolutePath().endsWith(".java")){
            String path=file.getAbsolutePath();

            String srcPath = getSrcPath(this.isMaven);
            path=path.replace(srcPath,"");
            String className=path.replace("\\",".");
            //E:\java\base\winter-batis\src\main\java\

            try {
                classes.add(Class.forName(className.substring(1,className.lastIndexOf("."))));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }


    public String getPackagePath(String packageName,boolean isMaven){
        packageName=packageName.replace(".","/");
            return getSrcPath(isMaven)+"/"+packageName;
    }

    public String getSrcPath(boolean isMaven){
        if(isMaven){
            return getProjectPath()+"\\src\\main\\java";
        }
        return getProjectPath()+"\\src\\java";
    }

    public String getProjectPath(){
        return "E:\\java\\base\\winter-batis";
        //return System.getProperty("user.dir");
    }

    public String getWebPath(){

        if(isMaven){
            return getProjectPath()+"/src/main/"+webName;
        }
        return null;
    }


    //通过解析el表达式来获得对象
    public Object getObject(String str){
        Pattern pattern=Pattern.compile("+[*]+");
        Matcher matcher=pattern.matcher(str);
        if(matcher.find()){
            System.out.println(matcher.group(1));
        }
        return null;
    }

}
