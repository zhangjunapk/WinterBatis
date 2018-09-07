package org.zj.winterbatis.util;

/**
 * Created by ZhangJun on 2018/9/1.
 */

/**
 * 值工具，用于处理值
 */
public class ValUtil {
    static public String parseString(Object obj){
        return String.valueOf(obj);
    }
    static public Float parseFloat(Object obj){
        return Float.parseFloat((String) obj);
    }
    static public Double parseDouble(Object obj){
        return Double.parseDouble((String) obj);
    }
    static public Integer parseInteger(Object obj){
        return Integer.parseInt((String) obj);
    }
    static public Integer[] convertToIntegerArr(Object[] valueArr){
        Integer[] result=new Integer[valueArr.length];
        for(int i=0;i<valueArr.length;i++){
            result[i]= ValUtil.parseInteger(valueArr[i]);
        }
        return result;
    }
    static public Double[] convertToDoubleArr(Object[] valueArr){
        Double[] result=new Double[valueArr.length];
        for(int i=0;i<valueArr.length;i++){
            result[i]= parseDouble(valueArr[i]);
        }
        return result;
    }
    static public Float[] convertToFloatArr(Object[] valueArr){
        Float[] result=new Float[valueArr.length];
        for(int i=0;i<valueArr.length;i++){
            result[i]= parseFloat(valueArr[i]);
        }
        return result;
    }
    //判断指定字符串是否为空(包括空格和null)
    public static boolean isBlank(String str){
        if(str==null||str.equals("")){
            return true;
        }
        str.replaceAll(" ","");
        return str.equals("");
    }
}
