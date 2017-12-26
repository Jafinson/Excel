package com.jafin.excel.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by 何锦发 on 2017/5/26.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface AColumn {
    String name() default "默认";//中文名，表头
    boolean ignore() default false;//是否忽略
    float width() default 1.0f;//宽度比例
}
