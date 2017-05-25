package com.jafin.excel.bean;

import android.support.annotation.NonNull;

/**
 * Created by 何锦发 on 2017/5/24.
 */
public class Column implements Comparable {
    public enum Type {
        TEXT, EDIT, CHECK
    }

    /**
     * +
     * 是否部件列需要跳转
     */
    public boolean isSkip;
    /**
     * 改列是textview还是edittext还是checkbox
     */
    public Type type;
    /***
     * 该列所占的宽度比例
     */
    private float width;
    /**
     * 该列的中文名，表头
     */
    private String name;
    /**
     * 该列对应的对象属性
     */
    @NonNull
    private String field;
    /**
     * 显示顺序；
     */
    private int order;

    public Column(String name, @NonNull String field, float width, Type type) {
        this.width = width;
        this.name = name;
        this.field = field;
        this.type = type;
    }

    public Column(String name, String field, Type type) {
        this(name, field, 1.0f, type);
    }

    public Column(String name, @NonNull String field, float width) {
        this(name, field, width, Type.TEXT);
    }

    public Column(String name, @NonNull String field) {
        this(name, field, 1.0f, Type.TEXT);
    }

    public Column(@NonNull String field) {
        this(null, field, 1.0f, Type.TEXT);
    }


    public String getName() {
        return name;
    }

    @NonNull
    public String getField() {
        return field;
    }


    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setField(@NonNull String field) {
        this.field = field;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
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
