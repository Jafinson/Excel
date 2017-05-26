package com.jafin.excel.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 何锦发 on 2017/5/10.
 */
public class Reflector<T> {
    private Class<T> clz;
    public Map<String, Field> fields;
    public Map<String, Method> getter;
    public Map<String, Method> setter;

    @SuppressWarnings("unchecked")
    public Reflector(String path) throws Exception {
        this((Class<T>) Class.forName(path));
    }

    public Reflector(Class<T> clz) {
        fields = new HashMap<>();
        getter = new HashMap<>();
        setter = new HashMap<>();
        Field[] temp = clz.getDeclaredFields();
        for (Field field : temp) {
            try {
                String name = field.getName();
                fields.put(name, field);
                getter.put(name, clz.getDeclaredMethod(getterMethod(name)));
                setter.put(name, clz.getDeclaredMethod(setterMethod(name), field.getType()));
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

    //首字母转大写
    private String toUpperCaseFirstOne(String s) {
        if (Character.isUpperCase(s.charAt(0))) return s;
        else return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
    }

    //get方法名
    private String getterMethod(String field) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("get");
        buffer.append(toUpperCaseFirstOne(field));
        return buffer.toString();
    }

    //set方法名
    private String setterMethod(String field) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("set");
        buffer.append(toUpperCaseFirstOne(field));
        return buffer.toString();
    }
}
