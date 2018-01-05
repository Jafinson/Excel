package com.jafin.excel.util;

import com.jafin.excel.annotation.AColumn;
import com.jafin.excel.bean.Column;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 何锦发 on 2018/1/4.
 * 快速生成List<Column>
 */

public class ColumnFactory {
    public static List<Column> createByClz(Class clz) {
        List<Column> result = new ArrayList<>();
        for (Field field : clz.getDeclaredFields()) {
            AColumn annotation = field.getAnnotation(AColumn.class);
            if (!annotation.ignore()) {
                result.add(new Column(field.getName(), annotation.name(), annotation.length()));
            }
        }
        return result;
    }
}
