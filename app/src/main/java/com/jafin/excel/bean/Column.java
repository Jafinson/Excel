package com.jafin.excel.bean;

import android.support.annotation.NonNull;

import com.jafin.excel.annotation.AColumn;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Column implements Comparable, Serializable,Cloneable {
    //region json字段
    /***
     * 该列所占的宽度比例
     */
    @AColumn(name = "宽度")
    private float width;
    /**
     * 该列对应的对象属性
     */
    @AColumn(name = "字段")
    private String field;
    /**
     * 该列是否显示
     */
    @AColumn(name = "可视")
    private boolean isShow;
    /**
     * 显示顺序；
     */
    @AColumn(name = "顺序",width = 0.5f)
    private int order;
    /**
     * 是否冻结
     */
    @AColumn(name = "冻结",width = 0.5f)
    private boolean frozen;
    /**
     * 显示格式
     */
    @AColumn(name = "格式",width = 0.5f)
    private int format;
    /**
     * 对齐方式
     */
    @AColumn(name = "对齐方式",width = 0.5f)
    private int alignment;
    /**
     * 该列的中文名，表头
     */
    @AColumn(name = "名称",width = 0.5f)
    private String name;

    public void setIsShow(boolean isShow) {
        this.isShow = isShow;
    }

    public boolean getFrozen() {
        return frozen;
    }

    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
    }

    public boolean getIsShow() {
        return this.isShow;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    @NonNull
    public String getField() {
        return field;
    }

    public void setField(@NonNull String field) {
        this.field = field;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getFormat() {
        return format;
    }

    public void setFormat(int format) {
        this.format = format;
    }

    public int getAlignment() {
        return alignment;
    }

    public void setAlignment(int alignment) {
        this.alignment = alignment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //endregion
    //region 私有字段
    private Field f;
    //是否可以编辑，true：textview false：edittext
    public boolean editable;
    /**
     * 列对应的javabean
     */
    private Class clz;
    /**
     * 选择模式，当属性值为boolean时，可设置这个模式为单选或者多选，默认多选，不是boolean的属性无效
     */
    public int choiceMode;
    /**
     * Normal list that does not indicate choices
     */
    public static final int CHOICE_MODE_NONE = 0;

    /**
     * The list allows up to one choice
     */
    public static final int CHOICE_MODE_SINGLE = 1;

    /**
     * The list allows multiple choices
     */
    public static final int CHOICE_MODE_MULTIPLE = 2;

    public String getFieldType() {
        return f.getType().getName();
    }

    public Column(String name, @NonNull String field, float width, boolean editable) {
        this.width = width;
        this.name = name;
        this.field = field;
        this.isShow = true;
        this.editable = editable;
    }

    public Column(String name, String field, boolean editable) {
        this(name, field, 1.0f, editable);
    }

    public Column(String name, @NonNull String field, float width) {
        this(name, field, width, false);
    }

    public Column(String name, @NonNull String field) {
        this(name, field, 1.0f, false);
    }

    public Column(@NonNull String field) {
        this(null, field, 1.0f, false);
    }

    //javabean 规范：无参构造
    public Column() {
    }

    @Override
    public int compareTo(@NonNull Object o) {
        if (((Column) o).getOrder() < this.order) {
            return 1;
        } else if ((((Column) o).getOrder() > this.order)) {
            return -1;
        } else {
            return 0;
        }
    }

    /**
     * 获取该单元格的值
     *
     * @param o 该行的对象
     * @return 该单元格的值
     */
    public Object getValue(Object o) throws IllegalAccessException {
        if (checkField(o))
            return null;
        f.setAccessible(true);
        return f.get(o);
    }

    /**
     * 设置改单元格的值
     *
     * @param o     该行的对象
     * @param value 要设置的值
     * @throws IllegalAccessException
     */
    public void setValue(Object o, Object value) throws IllegalAccessException {
        if (checkField(o))
            return;
        f.setAccessible(true);
        f.set(o, value);
    }

    public void setValue(Object o, String value) throws Exception {
        if (checkField(o))
            return;
        if ("double".equals(f.getType().getName())) {
            double v = Double.parseDouble(value);
            f.setAccessible(true);
            f.set(o, v);
        } else if ("float".equals(f.getType().getName())) {
            float v = Float.parseFloat(value);
            f.setAccessible(true);
            f.set(o, v);
        } else {
            setValue(o, (Object) value);
        }
    }

    public static List<Column> getShowColumns(List<Column> columns) {
        List<Column> result = new ArrayList<>();
        for (Column column : columns) {
            if (column.getIsShow()) {
                result.add(column);
            }
        }
        return result;
    }

    public static List<Column> getColumn() {
        return Arrays.asList(new Column("字段名", "field"), new Column("标题名", "name", 1.5f),
                //new Column("显示顺序","order"),
                new Column("宽度", "width", true), new Column("可视", "isShow", 0.8f), new Column("冻结", "frozen", 0.8f)
                //new Column("显示格式","format"),
                //new Column("对齐方式","alignment")
        );
    }

    public static void setAll(List<Column> columns, boolean isShow) {
        for (Column column : columns) {
            column.setIsShow(isShow);
        }
    }

    public static void invert(List<Column> columns) {
        for (Column column : columns) {
            column.setIsShow(!column.getIsShow());
        }
    }

    /**
     * 从其他地方获取的布局信息必须调用此方法进行置换
     * 置换两个columns，由于后台的属性参数跟前端不一致导致的，后边优化
     *
     * @param init   前端
     * @param others 后台
     */
    public static void change(List<Column> init, List<Column> others) {
        if (init.size() < others.size()) {
            System.out.println("后台返回的列跟初始化的不一样");
        }
        try {
            Map<String, Column> temp = new HashMap<>();
            for (Column column : init) {
                temp.put(column.getField(), column);
            }
            for (Column other : others) {
                Column column = temp.get(other.getField());
                column.setIsShow(other.isShow);
                column.setFrozen(other.frozen);
                column.setAlignment(other.getAlignment());
                column.setFormat(other.getFormat());
                column.setWidth(other.getWidth());
                column.setOrder(other.getOrder());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 在改类及其父类中寻找field
     *
     * @param clz
     * @return
     */
    private Field getFieldSpe(Class clz) {
        if (clz == null) {
            return null;
        }
        Field f = null;
        try {
            f = clz.getDeclaredField(field);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        if (f != null) {
            return f;
        } else {
            return getFieldSpe(clz.getSuperclass());
        }
    }

    /**
     * 检查field是否存在
     *
     * @param o 外传对象
     * @return 是否
     */
    private boolean checkField(Object o) {
        if (f == null) {
            try {
                this.f = getFieldSpe(o.getClass());
                f.setAccessible(true);
            } catch (Exception e) {
                e.printStackTrace();
                return true;
            }
        }
        return false;

    }

    /**
     * 根据column的信息判断应该用textview edittext checkbox
     *
     * @return 0-textview 1-edittext 2-checkbox 3-ratio
     */
    public int getItemType() {
        if (editable) {
            return 1;
        }
        String name;
        try {
            f.setAccessible(true);
            name = f.getType().getName();
            if ("boolean".equals(name)) {
                return 2;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
