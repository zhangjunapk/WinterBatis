package org.zj.winterbatis.util;

/**
 * Created by ZhangJun on 2018/9/7.
 */
//用于处理字符格式
public class FormateUtil {

    //下划线转驼峰
    static public String toCamelCase(int flag, String name) {
        int index = 0;
        for (int i = 0; i < name.length(); i++) {
            if (name.substring(i, i + 1).equals("_")) {
                System.out.println("前面:" + name.substring(0, i));

                /*System.out.println("后面"+name.substring(i+1));
                 */
                //判断_后面是否还有字符

                System.out.println(name.substring(i + 1) + "----0000");

                if (name.substring(i + 1).length() == 0) {
                    name = name = name.substring(0, i);
                } else {
                    System.out.println("后面的哦" + name.substring(i + 1) + "---");
                    name = name.substring(0, i) + name.substring(i + 1).substring(0, 1).toUpperCase() + name.substring(i + 2);
                }
            }
        }
        if (flag == 0) {
            if (name.length() > 1) {
                name = name.substring(0, 1).toLowerCase() + name.substring(1);
            } else {
                name = name.substring(0).toLowerCase();
            }
        } else {
            //如果flag=1 大驼峰

            if (name.length() > 1) {
                name = name.substring(0, 1).toUpperCase() + name.substring(1);
            } else {
                name = name.substring(0).toUpperCase();
            }
        }
        return name;
    }

    /**
     * 驼峰转下划线
     *
     * @param name
     * @return
     */
    static public String toLine(String name) {

        System.out.println(name + "    需要转换成下划线");
        name=name.substring(0,1).toLowerCase()+name.substring(1);
        for (int i = 0; i < name.length(); i++) {
            if (name.substring(i, i + 1).equals(name.substring(i, i + 1).toUpperCase())) {
                //说明当前字符是大写的
                name = name.substring(0, i) + "_" + name.substring(i, i + 1).toLowerCase() + name.substring(i + 1);
                System.out.println(name);
            }
        }
        return name;
    }

}
