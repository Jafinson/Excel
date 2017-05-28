package com.jafin.excel.bean;

import android.support.annotation.NonNull;

import com.jafin.excel.enums.ViewTypeEnum;
import com.jafin.excel.util.Reflector;

import java.lang.reflect.Field;

/**
 * Created by 何锦发 on 2017/5/24.
 */
public class Column implements Comparable {
   /* public enum ViewTypeEnum {
        TEXT, EDIT, CHECK
    }*/

    /**
     * +
     * 预留字段
     */
    public boolean flag;
    /**
     * 改列是textview还是edittext还是checkbox
     */
    public ViewTypeEnum type;
    /***
     * 该列所占的宽度比例
     */
    private float width;
    /**
     * 该列的中文名，表头
     */
    private String title;
    /**
     * 该列对应的对象属性名
     */
    @NonNull
    private String name;
    /**
     * 显示顺序；
     */
    private int order;
    /**
     * 该列的get set 方法
     */
    private Field field;

    public Column(String title, @NonNull String name, float width, ViewTypeEnum type) {
        this.width = width;
        this.title = title;
        this.name = name;
        this.type = type;
    }

    public Column(String title, String name, ViewTypeEnum type) {
        this(title, name, 1.0f, type);
    }

    public Column(String title, @NonNull String name, float width) {
        this(title, name, width, ViewTypeEnum.TEXT);
    }

    public Column(String title, @NonNull String name) {
        this(title, name, 1.0f, ViewTypeEnum.TEXT);
    }

    public Column(@NonNull String name) {
        this(null, name, 1.0f, ViewTypeEnum.TEXT);
    }


    public String getTitle() {
        return title;
    }

    @NonNull
    public String getName() {
        return name;
    }


    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setField(Reflector reflector) {
        this.field = (Field) reflector.fields.get(name);
    }

    public void setField(Field field) {
        this.field = field;
    }

    public Field getField() {
        return this.field;
    }

    @Override
    public int compareTo(Object o) {
        if (((Column) o).getOrder() < this.order) {
            return 1;
        } else if ((((Column) o).getOrder() > this.order)) {
            return -1;
        } else {
            return 0;
        }
    }
}
