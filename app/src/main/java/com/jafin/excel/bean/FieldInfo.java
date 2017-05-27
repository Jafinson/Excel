package com.jafin.excel.bean;

import com.jafin.excel.util.Reflector;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by 何锦发 on 2017/5/27.
 */

/**
 * 某个属性的get set 和field，放到Column中容易得到改列的内容
 */
public class FieldInfo {
    public Method getMethod;
    public Method setMethod;
    public Field field;

    public FieldInfo(String fieldName, Reflector reflector) {
        this.field = (Field) reflector.fields.get(fieldName);
        this.getMethod = (Method) reflector.getter.get(fieldName);
        this.setMethod = (Method) reflector.setter.get(fieldName);
    }
}
