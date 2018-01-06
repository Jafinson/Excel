package com.jafin.excel.util;

import java.math.BigDecimal;

public class StringUtils {

    /***
     * 格式化qty workprice等字段，默认保留两位小数
     *
     * @param num 要格式化的数值
     * @return 格式化的String
     */
    public static String formatDouble(double num) {
        int i_qty = (int) num;
        if (i_qty - num == 0) {
            return i_qty + "";
        }
        return keepTwo(num, 2) + "";
    }

    /***
     * 格式化qty workprice等字段，默认保留两位小数
     *
     * @param num 要格式化的数值
     * @return 格式化的String
     */
    public static String formatFloat(float num) {
        int i_qty = (int) num;
        if (i_qty - num == 0) {
            return i_qty + "";
        }
        return keepTwo(num, 2) + "";
    }

    public static double keepTwo(double qty, int count) {
        return new BigDecimal(qty).setScale(count, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static double keepTwo(float qty, int count) {
        return new BigDecimal(qty).setScale(count, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    //get方法名
    public static String getterMethod(String field) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("getValue");
        buffer.append(toUpperCaseFirstOne(field));
        return buffer.toString();
    }

    //set方法名
    public static String setterMethod(String field) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("setValue");
        buffer.append(toUpperCaseFirstOne(field));
        return buffer.toString();
    }

    //首字母转大写
    public static String toUpperCaseFirstOne(String s) {
        if (Character.isUpperCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
    }
}
