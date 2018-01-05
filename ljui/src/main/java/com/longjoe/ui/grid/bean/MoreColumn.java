package com.longjoe.ui.grid.bean;

import com.longjoe.ui.grid.enums.ItemTypeEnum;
import com.longjoe.ui.grid.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by 何锦发 on 2017/5/26.
 */
public class MoreColumn extends LjBean {
    public Class itemClass;
    private Class columnClass;
    public ItemTypeEnum titleType;
    public ItemTypeEnum contentType;
    private String listName;
    private String titleField;
    public Method listGet;
    public Method titleGet;
    public Method contentGet;
    public Method contentSet;
    public Field contentField;

    @SuppressWarnings("unchecked")
    public MoreColumn(Class itemClass, Class columnClass, String listName, String titleField, ItemTypeEnum titleType,
                      String contentField, ItemTypeEnum contentType) throws Exception {
        this.itemClass = itemClass;
        this.columnClass = columnClass;
        this.listName = listName;
        this.titleField = titleField;
        this.titleType = titleType;
        this.contentType = contentType;
        listGet = itemClass.getDeclaredMethod(StringUtils.getterMethod(listName));
        titleGet = columnClass.getDeclaredMethod(StringUtils.getterMethod(titleField));
        this.contentField = columnClass.getDeclaredField(contentField);
        contentGet = columnClass.getDeclaredMethod(StringUtils.getterMethod(contentField));
        contentSet = columnClass.getDeclaredMethod(StringUtils.setterMethod(contentField), this.contentField.getType());
    }
}
