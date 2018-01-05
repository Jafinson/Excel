package com.jafin.excel.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface AColumn {
    /**
     *
     * @return 中文名，表头
     */
    String name() default "默认";

    /**
     *
     * @return 是否忽略
     */
    boolean ignore() default false;

    /**
     *
     * @return 字符长度
     */
     int length() default 1;

    /**
     *
     * @return 列的排列顺序
     */
     int order() default 0;
}
