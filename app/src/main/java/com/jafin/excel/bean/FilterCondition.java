package com.jafin.excel.bean;

import android.support.annotation.NonNull;

import com.jafin.excel.annotation.AColumn;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * Created by 何锦发 on 2017/6/30.
 */
public class FilterCondition {
    @AColumn(name = "筛选的属性")
    @NonNull
    private Field field;//
    @AColumn(name = "筛选的值")
    private Set values;

    public FilterCondition(Field field, Set values) {
        this.field = field;
        this.values = values;
    }

    @NonNull
    public Field getField() {
        return field;
    }

    public Set getValues() {
        return values;
    }
}
