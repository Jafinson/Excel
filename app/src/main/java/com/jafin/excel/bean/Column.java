package com.jafin.excel.bean;

import android.support.annotation.NonNull;

import com.jafin.excel.annotation.AColumn;

import java.lang.reflect.Field;

/**
 * Created by 何锦发 on 2017/5/24.
 * 列的信息
 */
public class Column implements Comparable {
    //region javabean
    @AColumn(name = "顺序")
    private int order;
    @AColumn(name = "标题")
    private String name;
    @AColumn(name = "字段")
    private String field;
    @AColumn(name = "宽度")
    private int width;
    @AColumn(name = "可视")
    private boolean isShow;
    @AColumn(name = "冻结")
    private boolean frozen;
    @AColumn(name = "对齐方式")
    private int alignment;
    @AColumn(name = "格式")
    private int format;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public boolean getIsShow() {
        return isShow;
    }

    public void setIsShow(boolean show) {
        isShow = show;
    }

    public boolean isFrozen() {
        return frozen;
    }

    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
    }

    public int getAlignment() {
        return alignment;
    }

    public void setAlignment(int alignment) {
        this.alignment = alignment;
    }

    public int getFormat() {
        return format;
    }

    public void setFormat(int format) {
        this.format = format;
    }

    //endregion
    @Override
    public int compareTo(@NonNull Object o) {
        int order = ((Column) o).getOrder();
        if (this.getOrder() > order) {
            return 1;
        } else if (this.getOrder() < order) {
            return -1;
        } else {
            return 0;
        }
    }

    /**
     * 用于反射
     */
    private Field fd;

    private Field getFieldInAll(Class clz) {
        if (clz == Object.class) {
            return null;
        }
        try {
            Field result = clz.getDeclaredField(field);
            if (result != null) {
                return result;
            }
        } catch (Exception e) {
            return getFieldInAll(clz.getSuperclass());
        }
        return null;
    }

    public boolean enable;//是否可以编辑

    public Column() {}

    public Column(String field, String name, int width) {
        this(field, name, width, false);
    }

    public Column(String field, String name) {
        this(field, name, 1, false);
    }

    public Column(@NonNull String field, @NonNull String name, int width, boolean enable) {
        this.field = field;
        this.name = name;
        this.width = width;
        this.enable = enable;
    }

    /**
     * 传入data获取该列的值
     *
     * @param o 该行对应的data
     * @return 该格的值
     * @throws Exception 如果field未初始化或者对象没有这个字段
     */
    public Object getValue(Object o) throws Exception {
        if (field == null) {
            throw new Exception("对象字段field还没设置");
        }
        if (fd == null) {
            fd = getFieldInAll(o.getClass());
            if (fd != null) {
                fd.setAccessible(true);
            } else {
                throw new Exception("对象" + o.getClass().getSimpleName() + "无法获取字段" + field);
            }
        }
        return fd.get(o);
    }

    /**
     * 传入data获取该列的值
     *
     * @param o     该行对应的data
     * @param value 要设置的值
     * @throws Exception 如果field未初始化或者对象没有这个字段
     */
    public void setValue(Object o, Object value) throws Exception {
        if (field == null) {
            throw new Exception("对象字段field还没设置");
        }
        if (fd == null) {
            fd = getFieldInAll(o.getClass());
            if (fd != null) {
                fd.setAccessible(true);
            } else {
                throw new Exception("对象" + o.getClass().getSimpleName() + "无法获取字段" + field);
            }
        }
        if (fd.getType() != o.getClass()) {
            throw new Exception("列对象类型与要设置的值类型不符");
        }
        fd.set(o, value);
    }
}
