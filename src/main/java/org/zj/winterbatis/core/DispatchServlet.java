package org.zj.winterbatis.core;

import com.alibaba.druid.pool.DruidDataSource;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.zj.winterbatis.annotation.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.*;
import java.util.*;
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

        try {
            handleMapping(req,resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            try {
                handleMapping(req,resp);
            } catch (Exception e) {
                e.printStackTrace();
            }

    }

    private void handleMapping(HttpServletRequest req, HttpServletResponse resp) throws IllegalAccessException, InstantiationException {

        //处理request注入
        Invoke invoke2 = requestMappingMap.get(req.getRequestURI());
        Object controllerInstance=invoke2==null?null:invoke2.getObj();
        handleRequestResponseDI(controllerInstance,req,resp);

        Invoke invoke = requestMappingMap.get(req.getRequestURI());

        System.out.println("请求:"+req.getRequestURI());

        if(invoke!=null){
            try {

                //为方法的参数列表注入参数

                System.out.println("调用  "+invoke.getObj().getClass().getName()+"中的"+ invoke.getMethod().getName()+"方法");
                System.out.println("参数   :"+getParamer(invoke.getMethod(),req));
                Object invoke1 = invoke.getMethod().invoke(invoke.getObj(),getParamer(invoke.getMethod(),req));

                System.out.println(invoke+"   这是执行的数据");

                //看你是返回json还是页面
                handleResponse(invoke1,invoke.getMethod(),req,resp);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleRequestResponseDI(Object obj,HttpServletRequest req, HttpServletResponse resp) throws IllegalAccessException, InstantiationException {
        if(obj==null){
            return;
        }

        System.out.println(obj.getClass().getName()+"    类名");

        //遍历所有字段，如果找到request 和 responce 就注入
        for(Field f:obj.getClass().getDeclaredFields()){
            //如果你爹是HttpServletRequest住进去

            System.out.println("controller 里面的字段:"+f.getName()+"  我爹是request吗"+(HttpServletRequest.class.isAssignableFrom(obj.getClass())));
            System.out.println("controller 里面的字段:"+f.getName()+"  我爹是response吗"+(HttpServletResponse.class.isAssignableFrom(obj.getClass())));


            /*if(f.getType().getName().contains("HttpServletRequest")){
                f.setAccessible();
            }*/

            System.out.println("        >"+f.getType().getName()+"   :   "+f.getType().getName().contains("HttpServletRequest"));

            if(ServletRequest.class.isAssignableFrom(obj.getClass())
                    ||ServletRequest.class.getName().equals(f.getType().getClass().getName())||f.getType().getName().contains("HttpServletRequest")){
                f.setAccessible(true);
                System.out.println("给"+obj.getClass().getName()+" 注入"+f.getName());
                f.set(obj,req);
            }
            if(ServletResponse.class.isAssignableFrom(obj.getClass())
                    ||ServletResponse.class.getName().equals(f.getType().getClass().getName())||f.getType().getName().contains("HttpServletResponse")){
                f.setAccessible(true);
                f.set(obj,resp);
                System.out.println("给"+obj.getClass().getName()+" 注入"+f.getName());

            }
        }
    }

    private Object[] getParamer(Method method, HttpServletRequest req) throws IllegalAccessException, InstantiationException, IOException {

        Object[] param=new Object[method.getParameters().length];

        System.out.println("一共有"+method.getParameters().length+"   个参数");

        Parameter[] parameters = method.getParameters();
        for(int i=0;i<parameters.length;i++){
            param[i]=_getParam(parameters[i],req);
        }

        for(Object o:param){
            System.out.println(o.toString()+"     参fghjdfghj数列表--"+o+"--");
        }

        System.out.println(Arrays.toString(param));
        System.out.println("----------------");
        return param;
    }

    //通过传递过来的参数获得
    private Object _getParam(Parameter parameter, HttpServletRequest req) throws InstantiationException, IllegalAccessException, IOException {

        if(parameter.isAnnotationPresent(RequestBody.class)){
            return inflateJsonParam(parameter.getType(),req);
        }

        System.out.println("解析表单传过来的数据");

        //如果这个参数上加了requestparam
        if(parameter.isAnnotationPresent(RequestParam.class)){
            RequestParam annotation = parameter.getAnnotation(RequestParam.class);

            System.out.println("请求中的数据          >"+req.getParameterMap().get(annotation.value()));

           return req.getParameterMap().get(annotation.value());
        }

        return inflateFormParam(parameter.getType(),req);
    }

    private Object inflateJsonParam(Class<?> type, HttpServletRequest req) throws IOException {
        return new ObjectMapper().readValue(req.getQueryString().replaceAll("%20","\\"),type);
    }

    private Object inflateFormParam(Class<?> type, HttpServletRequest req) throws IllegalAccessException, InstantiationException {
        Object o = type.newInstance();

        System.out.println();

        for(Field f:o.getClass().getDeclaredFields()){

            System.out.println("req中的数据"+f.getName()+"   "+req.getAttribute(f.getName()));
            System.out.println(req);

            f.setAccessible(true);
            //我记得请求参数是放到这个map中的

            //如果请求中没有填这个参数就跳过
            if(req.getParameterMap().get(f.getName())==null){
                continue;
            }

            f.set(o, req.getParameterMap().get(f.getName())[0]);
        }
        return o;
    }


    private void handleResponse(Object result, Method method, HttpServletRequest req, HttpServletResponse resp) throws Exception {
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

        resp.setContentType("text/html;charset=utf-8");
        resp.getWriter().write(getAfterPageStr(req,read));
    }

    //获得解析后的页面
    private String getAfterPageStr(HttpServletRequest request,Document read) throws NoSuchFieldException, IllegalAccessException {
        Element rootElement = read.getRootElement();

        recursion(request,rootElement);

        return read.asXML();
    }

    public void recursion(HttpServletRequest req,Element e) throws NoSuchFieldException, IllegalAccessException {
        List<Element> elements = e.elements();
        for(Element ee:elements){
            recursion(req,ee);
        }
        System.out.println(e);

        //如果当前元素有item标签 ，就从request获得对象,并遍历子元素,获得里面的子元素并赋值

        if(e.attribute("items")!=null){
            Element parent = e.getParent();
            for(Object o:(List<Object>)getObject(req,"items")){
            Element copy = e.createCopy();

            System.out.println(e.createCopy()+"   复制的     一份");

            //遍历里面的元素

            for(Element ele:(List<Element>)copy.elements()){
                    //这是标签里面的文字username
                    for(String s:getName(ele.getText())) {
                        Field username = o.getClass().getDeclaredField(s);
                        username.setAccessible(true);
                        Object o1 = username.get(o);
                        String str = ele.getText();

                        System.out.println(ele.asXML());

                        System.out.println("----"+str+" <:> "+String.valueOf(o1)+"----");

                        str=str.replace("$[" +s+"]",String.valueOf(o1));
                        ele.setText(str);
                        copy.addElement(ele.asXML());
                    }
                }

                parent.addElement(copy.asXML());
            }

        }

    }

    public Object getObject(HttpServletRequest req,String name){

        System.out.println("获得req中的树形"+name);

        String s="\\+\\[*]\\+";

        // 创建 Pattern 对象
        Pattern r = Pattern.compile(s);

        // 现在创建 matcher 对象
        Matcher m = r.matcher(name);
        if (m.find( )) {

            System.out.println("-------->匹配楼 -------------");

            for(int i=0;i<m.groupCount();i++){
              return m.group(i);
            }
        } else {
            System.out.println("NO MATCH");
        }


        return req.getAttribute("studentList");
    }

    //通过对象和表示字段的字符串获得对象里面的字段值
    public String getString(Object obj,String name) throws Exception {
        Object lastObj=obj;

        for(String s:name.split(".")){
            lastObj=obj.getClass().getField(s).get(obj);
        }
        return String.valueOf(lastObj);
    }


    public List<String> getName(String name){
        //返回通过正则匹配的字符串\

        System.out.println("需要匹配的字符串");
        System.out.println("----------------");
        System.out.println(name);
        System.out.println("-------------");
        List<String> result=new ArrayList<>();

        String s="\\+\\[*]\\+";

        // 创建 Pattern 对象
        Pattern r = Pattern.compile(s);

        // 现在创建 matcher 对象
        Matcher m = r.matcher(name);
        if (m.find( )) {

            System.out.println("-------->匹配楼 -------------");

            for(int i=0;i<m.groupCount();i++){
                result.add(m.group(i));
            }
        } else {
            System.out.println("NO MATCH");
        }



        result.add("username");
        result.add("password");

        return result;
    }

    private void writeJson(Object result, HttpServletResponse resp) throws IOException {

        if(result==null){
            return;
        }

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

        if(instanceMap.get(c.getName())==null){
            instanceMap.put(c.getName(),c.newInstance());
        }

        //因为类全名包含key,应该是模糊匹配，而不是全匹配
        AspectBean aspectBean = getAspectBean(c.getName());
        if(c.getInterfaces()!=null&&c.getInterfaces().length!=0){
            //使用jdk的动态代理

            List<Invoke> before=aspectBean!=null?aspectBean.getBefore():null;
            List<Invoke> after=aspectBean!=null?aspectBean.getAfter():null;
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
                //这里还要进行page处理
                //生成处理page之后的代理类
                //Object oo=getPageProxyObject(o);
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
        Pattern pattern=Pattern.compile("\\+\\[*\\]\\+");
        Matcher matcher=pattern.matcher(str);
        if(matcher.find()){
            System.out.println(matcher.group(1));
        }
        return null;
    }

}
