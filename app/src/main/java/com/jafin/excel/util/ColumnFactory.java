package com.jafin.excel.util;


import com.jafin.excel.annotation.AColumn;
import com.jafin.excel.bean.Column;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by 何锦发 on 2017/6/13.
 * 生产Column列表
 */
public class ColumnFactory {
    @SuppressWarnings("unchecked")
    public static List<Column> createByClz(Class clz) throws Exception {
        Field[] declaredFields = clz.getDeclaredFields();
        String[] fields = new String[declaredFields.length];
        for (int i = 0; i < declaredFields.length; i++) {
            fields[i] = declaredFields[i].getName();
        }
        List<Column> rslt = createByFields(clz, fields);
        Collections.sort(rslt);//重定顺序
        return rslt;
    }

    @SuppressWarnings("unchecked")
    public static List<Column> createByFields(Class clz, String[] fields) throws Exception {
        List<Column> rslt = new ArrayList<>();
        for (String field : fields) {
            Field declaredField;
            try {
                declaredField = clz.getDeclaredField(field);
            } catch (NoSuchFieldException e) {
                throw new Exception("没有对应的字段名:" + field);
            }
            if (checkType(declaredField, "int", "java.lang.String", "double", "float","boolean")) {
                AColumn annotation = declaredField.getAnnotation(AColumn.class);
                if (annotation != null && !annotation.ignore()) {
                    float width = checkType(declaredField,"boolean")?0.5f:annotation.width();
                    Column column = new Column(annotation.name(), declaredField.getName(), width);
                    column.setOrder(annotation.order());
                    rslt.add(column);
                }
            }
        }
        //Collections.sort(rslt);
        return rslt;
    }

    private static boolean checkType(Field field, String... arg) {
        boolean rslt = false;
        for (String anArg : arg) {
            if (anArg.equals(field.getType().getName())) {
                rslt = true;
                break;
            }
        }
        return rslt;
    }
}
