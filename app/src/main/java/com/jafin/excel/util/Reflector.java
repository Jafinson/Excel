package com.jafin.excel.util;

import com.jafin.excel.enums.FieldTypeEnum;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 何锦发 on 2017/5/10.
 */

/**
 * JavaBean的反射器，必须符合JavaBean的规则
 *
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
    //public Map<String, Method> getter;

    /**
     * set方法集合，以属性名作为主键
     */
    //public Map<String, Method> setter;
    public Reflector(Class<T> clz) {
        fields = new HashMap<>();
        //getter = new HashMap<>();
        //setter = new HashMap<>();
        Field[] temp = clz.getDeclaredFields();
        for (Field field : temp) {
            String name = field.getName();
            if (name.equals("$change")) {
                continue;
            }
            fields.put(name, field);
            //getter.put(name, clz.getDeclaredMethod(getterMethod(name)));
            //setter.put(name, clz.getDeclaredMethod(setterMethod(name), field.getType()));
        }
    }

    public static Object getValue(Field field, Object data) {
        if (data == null) {
            return null;
        }
        Object rslt = null;
        try {
            field.setAccessible(true);
            rslt = field.get(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rslt;
    }

    public static void setValue(Field field, Object data, Object value) {
        if (data == null || value == null) {
            return;
        }
        try {
            field.set(data, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void setValue(Field field, Object data, String value) throws Exception {
        if (field.getType() == String.class) {
            setValue(field, data, (Object) value);
        }
        Object v = null;
        switch (getType(field)) {
            case INT:
                v = Integer.parseInt(value);
                break;
            case DOUBLE:
                v = Double.parseDouble(value);
                break;
            case FLOAT:
                v = Float.parseFloat(value);
                break;
            case LONG:
                v = Long.parseLong(value);
                break;
        }
        setValue(field, data, v);
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

    public static FieldTypeEnum getType(Field field) {
        if (field.getType() == String.class) {
            return FieldTypeEnum.STRING;
        } else if (field.getType().getName().equals("int")) {
            return FieldTypeEnum.INT;
        } else if (field.getType().getName().equals("double")) {
            return FieldTypeEnum.DOUBLE;
        } else if (field.getType().getName().equals("float")) {
            return FieldTypeEnum.FLOAT;
        } else if (field.getType().getName().equals("boolean")) {
            return FieldTypeEnum.BOOLEAN;
        } else if (field.getType().getName().equals("long")) {
            return FieldTypeEnum.LONG;
        } else {
            return FieldTypeEnum.OBJECT;
        }
    }
}
