package com.jafin.excel.annotation;

/**
 * Created by 何锦发 on 2017/5/26.
 */
public @interface AColumn {
    String name() default "默认";//中文名，表头
    boolean ignore() default false;//是否忽略
    float width() default 1.0f;//宽度比例
}
