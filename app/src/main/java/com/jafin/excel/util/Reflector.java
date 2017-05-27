package com.jafin.excel.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 何锦发 on 2017/5/10.
 */

/**
 * JavaBean的反射器，必须符合JavaBean的规则
 * @param <T>
 */
public class Reflector<T> {
    private Class<T> clz;
    /**
     * 属性集合，以属性名作为主键
     */
    public Map<String, Field> fields;
    /**
     * get方法集合，以属性名作为主键
     */
    public Map<String, Method> getter;
    /**
     * set方法集合，以属性名作为主键
     */
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

    /**
     * 某个属性的get set 和field，放到Column中容易得到改列的内容
     */

    public class FieldInfo{
        private Method getMethod;
        private Method setMethod;
        private Field field;

        public FieldInfo(String fieldName) {
            this.field = field;
            this.getMethod = getMethod;
            this.setMethod = setMethod;
        }
    }
}
