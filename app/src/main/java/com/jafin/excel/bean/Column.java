package com.jafin.excel.bean;

import android.support.annotation.NonNull;

import com.jafin.excel.annotation.AColumn;
import com.jafin.excel.enums.FieldTypeEnum;
import com.jafin.excel.enums.ViewTypeEnum;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 何锦发 on 2017/5/24.
 * 列的信息,javaBean的属性只支持int，double,String,float其他忽略
 */
public class Column implements Comparable {
    /**
     * 是否被选中
     */
    public boolean isChecked;
    /**
     * 该列的控件类型
     */
    public ViewTypeEnum type;
    /***
     * 该列所占的宽度比例
     */
    private float width;
    /**
     * 该列的中文名，表头
     */
    private String name;
    /**
     * 该列对应的对象属性名
     */
    @NonNull
    private Field field;
    /**
     * 显示顺序；
     */
    private int order;
    /**
     * 是否冻结
     */
    private boolean frozen;
    /**
     * shi否显示
     */
    private boolean isShow;
    /**
     * @param name  表头
     * @param field 属性
     * @param width 宽度
     * @param type  控件类型
     */
    public Column(String name, @NonNull Field field, float width, ViewTypeEnum type) {
        this.width = width;
        this.name = name;
        this.name = name;
        this.type = type;
        this.field = field;
    }

    public Column(String name, @NonNull Field field) {
        this(name, field, 1.0f, ViewTypeEnum.TEXT);
    }

    public Column(String name, @NonNull Field field, float width) {
        this(name, field, width, ViewTypeEnum.TEXT);
    }

    public Column(String name, @NonNull Field field, ViewTypeEnum type) {
        this(name, field, 1.0f, type);
    }

    /**
     * @param o 该行的对象
     * @return 获取该单元格的值
     */
    public Object getValue(Object o) {
        Object rslt = null;
        try {
            field.setAccessible(true);
            rslt = field.get(o);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if (null == rslt) {
            rslt = "";
        }
        return rslt;
    }

    public void setValue(Object o, Object value) {
        try {
            field.set(o, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过String转成改字段的值
     *
     * @param o     该行的对象
     * @param value 用户输入的值，一般为EditText.getText();
     */
    public void setValue(Object o, String value) {
        if (value != null && !value.isEmpty()) {
            setValue(o, getValueByString(value));
        }
    }

    public String getName() {
        return name;
    }

    @NonNull
    public Field getField() {
        return field;
    }

    public float getWidth() {
        return width;
    }

    public int getOrder() {
        return order;
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
     * 把一个类的所有字段转成Column，跳过属性注解ignore为true的和非float、double、String、int的属性
     *
     * @param clz 要转化的类
     * @return Column的集合
     */
    public static List<Column> createByClz(Class clz) {
        Field[] declaredFields = clz.getDeclaredFields();
        String[] fields = new String[declaredFields.length];
        for (int i = 0; i < declaredFields.length; i++) {
            fields[i] = declaredFields[i].getName();
        }
        try {
            return createByFields(clz, fields);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    /**
     * 指定bean的属性成为列，列的属性均为默认值
     *
     * @param clz    表格对应的bean
     * @param fields 指定的属性
     * @return 列
     * @throws NoSuchFieldException 如果bean没有名为field的属性则抛出异常
     */
    public static List<Column> createByFields(Class clz, String[] fields) throws NoSuchFieldException {
        List<Column> rslt = new ArrayList<>();
        for (String field : fields) {
            Field declaredField;
            try {
                declaredField = clz.getDeclaredField(field);
            } catch (NoSuchFieldException e) {
                throw new NoSuchFieldException("没有对应的字段名:" + field);
            }
            if (checkType(declaredField, "int", "java.lang.String", "double", "float")) {
                AColumn annotation = declaredField.getAnnotation(AColumn.class);
                if (annotation != null && !annotation.ignore()) {
                    rslt.add(new Column(annotation.name(), declaredField, annotation.width()));
                }
            }
        }
        return rslt;
    }

    /**
     * 检查属性是否属于哪些基本数据类型
     *
     * @param field 被检查的属性
     * @param arg   基本数据类型,一般为 :"int", "java.lang.String", "double", "float"
     * @return 是否符合基本数据类型
     */
    public static boolean checkType(Field field, String... arg) {
        boolean rslt = false;
        for (String anArg : arg) {
            if (anArg.equals(field.getType().getName())) {
                rslt = true;
                break;
            }
        }
        return rslt;
    }

    /**
     * 根据Field获取类型
     *
     * @param field 改列的field
     * @return 数据类型
     */
    public static FieldTypeEnum getFieldType(Field field) {
        if ("int".equals(field.getType().getName())) {
            return FieldTypeEnum.INT;
        } else if ("double".equals(field.getType().getName())) {
            return FieldTypeEnum.DOUBLE;
        } else if ("float".equals(field.getType().getName())) {
            return FieldTypeEnum.FLOAT;
        } else if ("java.lang.String".equals(field.getType().getName())) {
            return FieldTypeEnum.STRING;
        } else {
            return FieldTypeEnum.OBJECT;
        }
    }

    /**
     *
     * @return field的参数类型
     */
    public FieldTypeEnum getFieldType() {
        return getFieldType(field);
    }

    /**
     * 把String转成所需要的类型
     *
     * @param content 内容
     * @return 需要类型的值
     */
    private Object getValueByString(String content) {
        Object rslt = null;
        switch (getFieldType(field)) {
            case INT:
                try {
                    rslt = Integer.parseInt(content);
                } catch (NumberFormatException e) {
                    rslt = 0;
                }
                break;
            case DOUBLE:
                try {
                    rslt = Double.parseDouble(content);
                } catch (NumberFormatException e) {
                    rslt = 0.0;
                }
                break;
            case STRING:
                rslt = content;
                break;
            case FLOAT:
                try {
                    rslt = Float.parseFloat(content);
                } catch (NumberFormatException e) {
                    rslt = 0.0f;
                }
                break;
        }
        return rslt;
    }

    /**
     *
     * @return 获取一堆被选中的列
     */
    public static List<Column> getChecked(List<Column> columns){
        List<Column> rslt=new ArrayList<>();
        for (Column column : columns) {
            if (column.isChecked) {
                rslt.add(column);
            }
        }
        return rslt;
    }

    /***
     * 全选
     * @param columns 操作的列
     */
    public static void check(List<Column> columns){
        for (Column column : columns) {
            column.isChecked=true;
        }
    }

    /**
     * 全不选
     * @param columns 操作的列
     */
    public static void uncheck(List<Column> columns){
        for (Column column : columns) {
            column.isChecked=false;
        }
    }

    /**
     * 反选
     * @param columns 操作的列
     */
    public static void inverse(List<Column> columns){
        for (Column column : columns) {
            column.isChecked=! column.isChecked;
        }
    }
}
