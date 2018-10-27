package org.zj.winterbatis.core.util;

/**
 * Created by ZhangJun on 2018/9/7.
 */


import org.zj.winterbatis.bean.Student;
import org.zj.winterbatis.core.redis.RedisTemplate;

import javax.tools.*;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

/**
 * 反射工具
 */
public class ClassUtil {
    /**
     * 看方法的返回值是否是List
     *
     * @param method
     * @return
     */
    static public boolean isListReturn(Method method) {/*
        Type genericReturnType = method.getGenericReturnType();
        return ((ParameterizedType)genericReturnType).getRawType()==java.util.List.class;*/

        Class<?> returnType = method.getReturnType();

        System.out.println(returnType.getName() + "类型");

        return returnType.getName().contains("List");

    }

    /**
     * 通过class文件和类名来获得类
     * @param className
     * @param file
     * @return
     */
    public static Class getClass(String className,File file) throws Exception {
        return new BaseClassLoader().getClass(className,file);
    }

    /**
     * 获得方法返回list中的泛型类
     *
     * @param method
     * @return
     */
    static public Class getListGeneric(Method method) throws ClassNotFoundException {

        System.out.println(method);

        try {
            Type genericReturnType = method.getGenericReturnType();
            //获得泛型名
            String name = genericReturnType.toString().substring(genericReturnType.toString().indexOf("<") + 1, genericReturnType.toString().length() - 1);
            System.out.println(name + "  泛型 名");

            Class type = Class.forName(name);
            return type;
        } catch (Exception e) {
            e.printStackTrace();
            //主要应对没有写泛型的情况
            return Object.class;
        }
    }


    /**
     * 获得方法的返回类型
     *
     * @param method
     * @return
     * @throws ClassNotFoundException
     */
    static public Class getReturnType(Method method) throws ClassNotFoundException {
        String s = method.getGenericReturnType().toString();
        if (s.equals("void"))
            return null;

        return Class.forName(s);
    }

    static public Method getMethod(Class c, String methodName, Class[] objects) throws NoSuchMethodException {
        return (Method) c.getMethod(methodName, c);
    }

    /**
     * 获得指定对象中的指定值
     *
     * @param fieldName
     * @param o
     * @return
     */
    static public Object getVal(String fieldName, Object o) throws NoSuchFieldException, IllegalAccessException {
        Field declaredField = o.getClass().getDeclaredField(fieldName);
        declaredField.setAccessible(true);
        return declaredField.get(o);
    }


    /**
     * 获得返回的泛型
     *
     * @param method
     * @return
     */
    static public Class getGeneric(Method method) throws ClassNotFoundException {
        if (isListReturn(method)) {
            return getListGeneric(method);
        }
        return getReturnType(method);
    }

    /**
     * 获得接口的泛型
     *
     * @param c
     * @return
     */
    static public Class getInterfaceGeneric(Class c) {
        Type[] types = c.getGenericInterfaces();

        for (Type type : types) {
            if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                for (Type actualTypeArgument : actualTypeArguments) {
                    String typeName = actualTypeArgument.getTypeName();
                    try {
                        return Class.forName(typeName);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            }
        }
        return null;
    }


    /**
     * 递归获得指定文件夹下的所有类
     *
     * @param classes
     * @param file
     */
    public static void inflateClassByClassLoader(List<Class> classes, File file) throws IOException, ClassNotFoundException {
        BaseClassLoader baseClassLoader = new BaseClassLoader();

        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                inflateClassByClassLoader(classes, f);
            }
        }
        if (file.isFile() && file.getName().endsWith(".class"))
            classes.add(baseClassLoader.getClass(getClassName(file), file));
    }

    /**
     * 将当前项目的所有class填充到指定容器,通过类名
     *
     * @param classes
     * @throws IOException
     */
    public static void inflateClass(List<Class> classes) throws IOException, ClassNotFoundException {
        //inflateClassByClassLoader(classes,new File(getClassPath()));
        inflateClassByName(classes, new File(getClassPath()));
    }


    private static void inflateClassByName(List<Class> classes, File file) throws ClassNotFoundException {

        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                inflateClassByName(classes, f);
            }
        }
        if (file.isFile() && file.getName().endsWith(".class"))
            classes.add(Class.forName(getClassName(file)));
    }

    /**
     * 获得当前class文件的类全名
     *
     * @param file
     * @return
     */
    private static String getClassName(File file) {
        String absolutePath = file.getAbsolutePath();

        //System.out.println("之前 "+absolutePath);

        absolutePath = absolutePath.replace(getClassPath(), "");

        System.out.println("之后  " + absolutePath);

        System.out.println(absolutePath);
        absolutePath = absolutePath.replace("\\", ".");

        absolutePath = absolutePath.replace("/", ".");
        String substring = absolutePath.substring(0, absolutePath.lastIndexOf("."));
        System.out.println(substring);
        return substring;
    }


    /**
     * 获得编译后的Class文件的绝对路径
     *
     * @return
     */
    public static String getClassPath() {
        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        path = path.substring(1);
        path = path.replace("/", "\\");

        //System.out.println(path+"  class路径");

        return path;
    }

    public static void main(String[] args) throws Exception {

       /* ArrayList<Class> classes = new ArrayList<>();
        //inflateClass(classes,new File(getClassPath()));
        //getInterfaceGeneric(TeacherMapper.class);

        System.out.println(classes.size());
*/

        Class compile = getCompile("org.zj.winterbatis.core.util.AnnotationUtil", new File("D:\\java\\base\\WinterBatis\\src\\main\\java\\org\\zj\\winterbatis\\util\\AnnotationUtil.java"), new File("d:/cc/"));
        System.out.println(compile.getName());
    }

    /**
     * 把指定的java文件编译成class然后放到指定路径
     *
     * @param from
     * @param to
     * @return
     * @throws Exception
     */
    public static Class getCompile(String className,File from, File to) throws Exception {

        if(!to.exists())
            to.mkdirs();

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        DiagnosticCollector diagnostics = new DiagnosticCollector();

        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);

        Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromStrings(Arrays
                .asList(from.getAbsolutePath()));


        Iterable<String> options = Arrays.asList("-d",
                to.getAbsolutePath());// 指定的路径一定要存在，javac不会自己创建文件夹
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, options, null,
                compilationUnits);

        //编译java文件
        boolean success = task.call();
        fileManager.close();
        System.out.println((success) ? "编译成功" : "编译失败");
        return getClass(className,new File(getClassPath(to,className)));
    }

    /**
     * 根据类名和放的路径获得class绝对路径
     * @param to
     * @param name
     * @return
     */
    public static String getClassPath(File to,String name){
        name=name.replace(".","\\");
        return to.getAbsolutePath()+"\\"+name+".class";
    }

}
