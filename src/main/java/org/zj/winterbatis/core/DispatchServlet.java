package org.zj.winterbatis.core;

import com.alibaba.druid.pool.DruidDataSource;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.tools.doclets.internal.toolkit.NestedClassWriter;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.quartz.SchedulerException;
import org.zj.winterbatis.Config;
import org.zj.winterbatis.annotation.*;
import org.zj.winterbatis.classhandler.*;
import org.zj.winterbatis.util.ClassUtil;
import org.zj.winterbatis.util.RabbitMQUtil;
import org.zj.winterbatis.util.ValUtil;

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
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ZhangJun on 2018/7/7.
 */
public class DispatchServlet extends HttpServlet {
    boolean isMaven = false;
    Class config = Config.class;

    String webName;

    String viewPrefix;
    String viewSuffix;

    List<Class> classes = new ArrayList<>();
    DruidDataSource druidDataSource = new DruidDataSource();

    Map<String, Object> instanceMap = new HashMap<>();

    Map<String, Invoke> requestMappingMap = new HashMap<>();

    Map<String, AspectBean> aspectBeanMap = new HashMap<>();

    private Rabbit rabbit;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            handleMapping(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            handleMapping(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void handleMapping(HttpServletRequest req, HttpServletResponse resp) throws Exception {

        //处理request注入
        Invoke invoke2 = requestMappingMap.get(req.getRequestURI());
        Object controllerInstance = invoke2 == null ? null : invoke2.getObj();
        handleRequestResponseDI(controllerInstance, req, resp);

        Invoke invoke = requestMappingMap.get(req.getRequestURI());

        //这里不光要根据访问url获得,还要根据请求方法来获得
        System.out.println("这是请求方法        >>>>" + req.getMethod());
        System.out.println("请求:" + req.getRequestURI());

        if (invoke == null) {

            System.out.println("没有在mappingMap中找到");

            resp.setStatus(404);
            return;
        }
        //方法不匹配 比如请求的是get 方法，但是uriMappingMap中并没有这个方法
        if (!invoke.getRequestMethod().equals(req.getMethod())) {
            System.out.println("方法不匹配");
            resp.setStatus(404);
            return;
        }

        //看你是返回rest响应 还是json还是页面
        handleResponse(invoke.getObj(), invoke, req, resp);
    }

    private void handleRequestResponseDI(Object obj, HttpServletRequest req, HttpServletResponse resp) throws IllegalAccessException, InstantiationException {
        if (obj == null) {
            return;
        }

        System.out.println(obj.getClass().getName() + "    类名");

        //遍历所有字段，如果找到request 和 responce 就注入
        for (Field f : obj.getClass().getDeclaredFields()) {
            //如果你爹是HttpServletRequest住进去

            System.out.println("controller 里面的字段:" + f.getName() + "  我爹是request吗" + (HttpServletRequest.class.isAssignableFrom(obj.getClass())));
            System.out.println("controller 里面的字段:" + f.getName() + "  我爹是response吗" + (HttpServletResponse.class.isAssignableFrom(obj.getClass())));


            /*if(f.getType().getName().contains("HttpServletRequest")){
                f.setAccessible();
            }*/

            System.out.println("        >" + f.getType().getName() + "   :   " + f.getType().getName().contains("HttpServletRequest"));

            if (ServletRequest.class.isAssignableFrom(obj.getClass())
                    || ServletRequest.class.getName().equals(f.getType().getClass().getName()) || f.getType().getName().contains("HttpServletRequest")) {
                f.setAccessible(true);
                System.out.println("给" + obj.getClass().getName() + " 注入" + f.getName());
                f.set(obj, req);
            }
            if (ServletResponse.class.isAssignableFrom(obj.getClass())
                    || ServletResponse.class.getName().equals(f.getType().getClass().getName()) || f.getType().getName().contains("HttpServletResponse")) {
                f.setAccessible(true);
                f.set(obj, resp);
                System.out.println("给" + obj.getClass().getName() + " 注入" + f.getName());

            }
        }
    }

    private Object[] getParamer(Method method, HttpServletRequest req) throws IllegalAccessException, InstantiationException, IOException {

        Object[] param = new Object[method.getParameters().length];

        System.out.println("一共有" + method.getParameters().length + "   个参数");

        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            param[i] = _getParam(parameters[i], req);
        }

        System.out.println(Arrays.toString(param));
        System.out.println("----------------");
        return param;
    }

    //通过传递过来的参数获得
    private Object _getParam(Parameter parameter, HttpServletRequest req) throws InstantiationException, IllegalAccessException, IOException {

        if (parameter.isAnnotationPresent(RequestBody.class)) {
            return inflateJsonParam(parameter.getType(), req);
        }

        System.out.println("解析表单传过来的数据");

        RequestParam annotation = parameter.getAnnotation(RequestParam.class);

        System.out.println("请求中的数据          >" + req.getParameterMap().get(annotation.value()));

        System.out.println("请求中的数据   " + req.getParameterMap().get(annotation.value()));

        System.out.println("key :" + annotation.value() + "  values:" + req.getParameterMap().get(annotation.value()));


        Object[] valueArr = null;
        for (Map.Entry<String, String[]> entry : req.getParameterMap().entrySet()) {
            if (entry.getKey().equals(annotation.value())) {
                valueArr = entry.getValue();
            }
        }
        System.out.println("这是req中获取的数据  " + Arrays.toString(valueArr));
        //如果这个参数上加了requestparam
        if (parameter.isAnnotationPresent(RequestParam.class)) {

            System.out.println("加了requestparam注解");

            //判断参数类型,然后从req获得参数并做类型转换
            //如果当前参数的类型数组的话就转换每个元素
            if (parameter.getType().isArray()) {
                if (parameter.getType() == String[].class) {
                    //如果是字符串数组直接返回就行了哦
                    return valueArr;
                }
                if (parameter.getType() == Float[].class || parameter.getType() == float[].class) {
                    return ValUtil.convertToFloatArr(valueArr);
                }
                if (parameter.getType() == Double[].class || parameter.getType() == double[].class) {
                    return ValUtil.convertToDoubleArr(valueArr);
                }

                if (parameter.getType() == Integer[].class || parameter.getType() == int[].class) {
                    return ValUtil.convertToIntegerArr(valueArr);
                }

                return valueArr;
            }
            if (parameter.getType() == Integer.class || parameter.getType() == int.class) {
                return Integer.parseInt((String) valueArr[0]);
            }
            if (parameter.getType() == Float.class || parameter.getType() == float.class) {
                return Float.parseFloat((String) valueArr[0]);
            }
            if (parameter.getType() == Double.class || parameter.getType() == double.class) {
                return Double.parseDouble((String) valueArr[0]);
            }

//           return req.getParameterMap().get(annotation.value());
            return null;
        }
        //如果没有加注解，直接获得方法的参数类型，然后通过反射对里面的字段进行注入
        return inflateFormParam(parameter.getType(), req);
    }

    private Object inflateJsonParam(Class<?> type, HttpServletRequest req) throws IOException {
        return new ObjectMapper().readValue(req.getQueryString().replaceAll("%20", "\\"), type);
    }

    private Object inflateFormParam(Class<?> type, HttpServletRequest req) throws IllegalAccessException, InstantiationException {
        Object o = type.newInstance();

        System.out.println();

        for (Field f : o.getClass().getDeclaredFields()) {

            System.out.println("req中的数据" + f.getName() + "   " + req.getAttribute(f.getName()));
            System.out.println(req);

            f.setAccessible(true);
            //我记得请求参数是放到这个map中的

            //如果请求中没有填这个参数就跳过
            if (req.getParameterMap().get(f.getName()) == null) {
                continue;
            }

            f.set(o, req.getParameterMap().get(f.getName())[0]);
        }
        return o;
    }


    private void handleResponse(Object obj, Invoke invoke, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Method method = invoke.getMethod();
        //如果加了@AutoRestfulResponce
        if (method.isAnnotationPresent(AutoRestfulResponce.class)) {
            int successCode = -1;
            if (method.isAnnotationPresent(GetMapping.class)) {
                successCode = 200;
            }
            if (method.isAnnotationPresent(DeleteMapping.class)) {
                successCode = 200;
            }
            if (method.isAnnotationPresent(PostMapping.class)) {
                successCode = 201;
            }
            if (method.isAnnotationPresent(PutMapping.class)) {
                successCode = 200;
            }
            //尝试执行代码并把返回值进行json处理
            try {
                Object invoke1 = invoke.getMethod().invoke(invoke.getObj(), getParamer(invoke.getMethod(), req));
                writeJson(invoke1, resp);
                resp.setStatus(successCode);
                return;
            } catch (Exception e) {
                e.printStackTrace();
                resp.setStatus(500);
            }
        }

        //如果方法上加了ResponseBody注解或者所在的类加了RestController注解就返回json
        if (method.isAnnotationPresent(ResponceBody.class) || obj.getClass().isAnnotationPresent(RestController.class)) {
            //往响应写入json
            writeJson(invoke.getMethod().invoke(invoke.getObj(), getParamer(invoke.getMethod(), req)), resp);
            return;
        }
        //往响应写入页面
        writePage(invoke.getMethod().invoke(invoke.getObj(), getParamer(invoke.getMethod(), req)), req, resp);
    }

    private void writePage(Object result, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        //模板引擎
        String midPath = String.valueOf(result);

        String viewPath = getWebPath() + viewPrefix + midPath + viewSuffix;

        writeStaticView(viewPath, req, resp);

        System.out.println("这是页面");
    }

    private void writeStaticView(String viewPath, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        SAXReader saxReader = new SAXReader();
        Document read = saxReader.read(viewPath);

        System.out.println(resp + "    响应");

        resp.setContentType("text/html;charset=utf-8");
        resp.getWriter().write(getAfterPageStr(req, read));
    }

    //获得解析后的页面
    private String getAfterPageStr(HttpServletRequest request, Document read) throws NoSuchFieldException, IllegalAccessException {
        Element rootElement = read.getRootElement();

        recursion(request, rootElement);

        return read.asXML();
    }

    public void recursion(HttpServletRequest req, Element e) throws NoSuchFieldException, IllegalAccessException {
        List<Element> elements = e.elements();
        for (Element ee : elements) {
            recursion(req, ee);
        }
        System.out.println(e);

        //如果当前元素有item标签 ，就从request获得对象,并遍历子元素,获得里面的子元素并赋值

        if (e.attribute("items") != null) {
            Element parent = e.getParent();
            for (Object o : (List<Object>) getObject(req, "items")) {
                Element copy = e.createCopy();

                System.out.println(e.createCopy() + "   复制的     一份");

                //遍历里面的元素

                for (Element ele : (List<Element>) copy.elements()) {
                    //这是标签里面的文字username
                    for (String s : getName(ele.getText())) {
                        Field username = o.getClass().getDeclaredField(s);
                        username.setAccessible(true);
                        Object o1 = username.get(o);
                        String str = ele.getText();

                        System.out.println(ele.asXML());

                        System.out.println("----" + str + " <:> " + String.valueOf(o1) + "----");

                        str = str.replace("$[" + s + "]", String.valueOf(o1));
                        ele.setText(str);
                        copy.addElement(ele.asXML());
                    }
                }

                parent.addElement(copy.asXML());
            }

        }

    }

    public Object getObject(HttpServletRequest req, String name) {

        System.out.println("获得req中的树形" + name);

        String s = "\\+\\[*]\\+";

        // 创建 Pattern 对象
        Pattern r = Pattern.compile(s);

        // 现在创建 matcher 对象
        Matcher m = r.matcher(name);
        if (m.find()) {

            System.out.println("-------->匹配楼 -------------");

            for (int i = 0; i < m.groupCount(); i++) {
                return m.group(i);
            }
        } else {
            System.out.println("NO MATCH");
        }


        return req.getAttribute("studentList");
    }

    //通过对象和表示字段的字符串获得对象里面的字段值
    public String getString(Object obj, String name) throws Exception {
        Object lastObj = obj;

        for (String s : name.split(".")) {
            lastObj = obj.getClass().getField(s).get(obj);
        }
        return String.valueOf(lastObj);
    }


    public List<String> getName(String name) {
        //返回通过正则匹配的字符串\

        System.out.println("需要匹配的字符串");
        System.out.println("----------------");
        System.out.println(name);
        System.out.println("-------------");
        List<String> result = new ArrayList<>();

        String s = "\\+\\[*]\\+";

        // 创建 Pattern 对象
        Pattern r = Pattern.compile(s);

        // 现在创建 matcher 对象
        Matcher m = r.matcher(name);
        if (m.find()) {

            System.out.println("-------->匹配楼 -------------");

            for (int i = 0; i < m.groupCount(); i++) {
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
        if (result == null) {
            System.out.println("返回值为空");
            return;
        }

        System.out.println(result + "  这是响应的内容哦");

        resp.setCharacterEncoding("utf-8");

        System.out.println("写入json" + new ObjectMapper().writeValueAsString(result));
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
        try {
            initClass();
        } catch (Exception e) {
            e.printStackTrace();
        }

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
        try {
            doDI();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        //扫描所有controller/然后将RequestMapping和对象/方法的对应关系放到容器里
        setUrlMapping();

        System.out.println("finish init");
    }

    private void initAspectContainer() throws IllegalAccessException, InstantiationException {

        //先对切面进行初始化
        for (Class c : classes) {
            if (c.isAnnotationPresent(Aspect.class) && c.isAnnotationPresent(Condition.class)) {
                List<Invoke> before = new ArrayList<>();
                List<Invoke> after = new ArrayList<>();

                instanceMap.put(c.getName(), c.newInstance());
                Condition example = (Condition) c.getAnnotation(Condition.class);
                for (Method m : c.getDeclaredMethods()) {
                    if (m.isAnnotationPresent(Before.class)) {
                        before.add(new Invoke(instanceMap.get(c.getName()), m));
                    }
                    if (m.isAnnotationPresent(After.class)) {
                        after.add(new Invoke(instanceMap.get(c.getName()), m));
                    }
                }
                //添加到aspect容器里

                aspectBeanMap.put(example.value(), new AspectBean(before, after));
            }
        }
    }

    private void initClass() throws IOException, ClassNotFoundException {

        //将当前项目中的所有类填充到容器里
        ClassUtil.inflateClass(classes);
    }

    private void doDI() throws IllegalAccessException {

        //System.out.println("----------------");
        for (String s : instanceMap.keySet()) {
            System.out.println(s);
        }
        //System.out.println("-----------------");
        for (Class c : classes) {

            //System.out.println("       当前是" + c.getName());

            for (Field field : c.getDeclaredFields()) {
                if (field.isAnnotationPresent(Autofired.class)) {
                    field.setAccessible(true);
                    //处理rabbitMQ生产者的注入
                    if(field.isAnnotationPresent(RabbitProducter.class)){
                        //获得这个上面的注解
                        RabbitProducter rabbitProducter=field.getAnnotation(RabbitProducter.class);

                        //System.out.println("------------------找到了 ，生产者 然后给你注入进去");

                        field.set(instanceMap.get(c.getName()), RabbitMQUtil.getProxyObject(rabbit,RabbitMQProducter.class,rabbitProducter));
                    }

                    try {
                        //System.out.println("       为" + c.getName() + "注入" + field.getName());


                       // System.out.println("-------");
                        System.out.println(instanceMap.get(c.getName()) != null);
                        System.out.println(instanceMap.get(field.getType().getName()) != null);
                        //System.out.println("----------");

                        if (instanceMap.get(field.getType().getName()) == null)
                            continue;

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
        for (Class c : classes) {

            System.out.println(c.getName());

            if (c.isAnnotationPresent(Controller.class) || c.isAnnotationPresent(RestController.class)) {

                System.out.println(c.getName());

                RequestMapping requestMapping = (RequestMapping) c.getAnnotation(RequestMapping.class);
                String prefixMapping = "";
                if (requestMapping != null)
                    prefixMapping = requestMapping.value();

                System.out.println("没报错----");
                for (Method m : c.getDeclaredMethods()) {
                    String methodMappingStr = "";
                    String requestMethod = "";
                    if (m.isAnnotationPresent(RequestMapping.class)) {
                        RequestMapping methodMapping = m.getAnnotation(RequestMapping.class);
                        methodMappingStr = methodMapping.value();
                        requestMethod = "GET";
                    }
                    if (m.isAnnotationPresent(GetMapping.class)) {
                        GetMapping getMapping = m.getAnnotation(GetMapping.class);
                        methodMappingStr = getMapping.value();
                        requestMethod = "GET";
                    }

                    if (m.isAnnotationPresent(PostMapping.class)) {
                        PostMapping postMapping = m.getAnnotation(PostMapping.class);
                        methodMappingStr = postMapping.value();
                        requestMethod = "POST";
                    }

                    if (m.isAnnotationPresent(PutMapping.class)) {
                        PutMapping putMapping = m.getAnnotation(PutMapping.class);
                        methodMappingStr = putMapping.value();
                        requestMethod = "PUT";
                    }

                    if (m.isAnnotationPresent(DeleteMapping.class)) {
                        DeleteMapping deleteMapping = m.getAnnotation(DeleteMapping.class);
                        methodMappingStr = deleteMapping.value();
                        requestMethod = "DELETE";
                    }

                    //System.out.println("----------放进去:   "+prefixMapping+methodMappingStr);

                    requestMappingMap.put(prefixMapping + methodMappingStr, new Invoke(instanceMap.get(c.getName()), m, requestMethod));

                }
            }

        }
    }

    private void instanceObject() throws Exception {
        instanceMapper();
        instanceService();
        instanceController();
        instanceTask();
        instanceCustomer();
    }
    //rabbitmq消费者的监听处理
    private void instanceCustomer() throws IOException, TimeoutException, InstantiationException, IllegalAccessException {
        for (Class c : classes) {
            if(!c.isAnnotationPresent(Component.class))
                continue;
            for(Method m:c.getDeclaredMethods()) {
                if(!m.isAnnotationPresent(RabbitListener.class))
                    continue;
                RabbitListener annotation = m.getAnnotation(RabbitListener.class);
                RabbitMQCustomerClassHandler rabbitMQCustomerClassHandler=new RabbitMQCustomerClassHandler(annotation);
                rabbitMQCustomerClassHandler.handleClass(c);
            }
        }
    }

    private void instanceTask() throws Exception {
        TaskClassHandler taskClassHandler=new TaskClassHandler();
        for (Class c : classes) {
            taskClassHandler.handleClass(c);
        }
    }

    private void instanceController() throws Exception {
        ControllerClassHandler controllerClassHandler=new ControllerClassHandler(instanceMap,aspectBeanMap);
        for (Class c : classes) {
            controllerClassHandler.handleClass(c);
        }
    }

    private void instanceService() throws Exception {
        ServiceClassHandler serviceClassHandler=new ServiceClassHandler(instanceMap,aspectBeanMap);
        for (Class c : classes) {
            serviceClassHandler.handleClass(c);
        }
    }

    private void instanceMapper() throws ParseException, InstantiationException, IllegalAccessException, SchedulerException, IOException {

        MapperClassHandler mapperClassHandler = new MapperClassHandler(druidDataSource, instanceMap);

        for (Class c : classes) {
            mapperClassHandler.handleClass(c);
        }
    }



    private void initAnnotation() {

        rabbit= (Rabbit) config.getAnnotation(Rabbit.class);

        MapperScan mapperScan = (MapperScan) config.getAnnotation(MapperScan.class);
        WebPath webPath = (WebPath) config.getAnnotation(WebPath.class);
        this.webName = webPath.value();

        ViewPrefix viewPrefix = (ViewPrefix) config.getAnnotation(ViewPrefix.class);
        ViewSuffix viewSuffix = (ViewSuffix) config.getAnnotation(ViewSuffix.class);

        this.viewSuffix = viewSuffix.value();
        this.viewPrefix = viewPrefix.value();


        DataSource dataSource = (DataSource) config.getAnnotation(DataSource.class);

        druidDataSource.setDriverClassName(dataSource.driver());
        druidDataSource.setUsername(dataSource.username());
        druidDataSource.setUrl(dataSource.url());
        druidDataSource.setPassword(dataSource.password());

        IsMaven isMaven = (IsMaven) config.getAnnotation(IsMaven.class);
        this.isMaven = isMaven.value();
    }

    public String getProjectPath() {
        return "D:\\java\\base\\WinterBatis";
        //return System.getProperty("user.dir");
    }

    public String getWebPath() {

        if (isMaven) {
            return getProjectPath() + "/src/main/" + webName;
        }
        return null;
    }


    //通过解析el表达式来获得对象
    public Object getObject(String str) {
        Pattern pattern = Pattern.compile("\\+\\[*\\]\\+");
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            System.out.println(matcher.group(1));
        }
        return null;
    }

}
