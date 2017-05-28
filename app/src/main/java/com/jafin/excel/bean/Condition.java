package com.jafin.excel.bean;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 何锦发 on 2017/5/27.
 */
public class Condition<T> {
    private Method getter;
    private Object value;
    private List<T> rslt;
    private Field field;
    private Class<T> clz;

    public Condition(Field field, Method getter, Object value) {
        this.field = field;
        this.getter = getter;
        this.value = value;
        rslt = new ArrayList<>();
    }

    /**
     * 获取主键，把字符串 "属性=值" 作为condition的主键
     *
     * @return condition的主键
     */
    public static int getKey(Field field, Object value) {
        String code = field.getName() + "=" + value.toString();
        return code.hashCode();
    }

    @Override
    public int hashCode() {
        String code = field.getName() + "=" + value.toString();
        return code.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this.field.equals(((Condition) obj).getField()) && this.value.equals(((Condition) obj).getValue());
    }

    public List<T> getRslt() {
        return rslt;
    }

    public Field getField() {
        return field;
    }

    public Object getValue() {
        return value;
    }
}
