package com.jafin.excel.widget;

import java.util.List;

/**
 * Created by 何锦发 on 2017/12/26.
 */
public interface ExcelInterface {
    /**
     * 数据加载完之后显示操作
     * @param data 要显示的数据
     */
    void show(List data);
    void filter();
}
