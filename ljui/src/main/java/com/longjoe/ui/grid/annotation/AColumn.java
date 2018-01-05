package com.longjoe.ui.grid.annotation;

/**
 * Created by 何锦发 on 2017/5/26.
 */

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface AColumn {
    String name() default "默认";//列名
    float width() default 1.0f;//列宽
    boolean ignore() default false;//是否忽略字段
    int order() default 0;//列顺序
}
