package com.longjoe.ui.grid.bean;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 何锦发 on 2017/4/7.
 */
public class LjBean implements Serializable {
    private boolean isChecked;//是否选中
    private boolean isFiltered;//是否筛掉

    public boolean isFiltered() {
        return isFiltered;
    }

    public void setFiltered(boolean filtered) {
        isFiltered = filtered;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public static <T extends LjBean> void setAllFilter(List<T> source, boolean isFiltered) {
        for (T t : source) {
            t.setFiltered(isFiltered);
        }
    }

    public static <T extends LjBean> void setAllChecked(List<T> source, boolean isChecked) {
        for (T t : source) {
            t.setChecked(isChecked);
        }
    }

    public static <T extends LjBean> void inverse(List<T> source) {
        for (T t : source) {
            t.setChecked(!t.isChecked());
        }
    }


    public static <T extends LjBean> List<T> getChecked(List<T> mData) {
        List rslt = new ArrayList();
        for (T t : mData) {
            if (t.isChecked()) {
                rslt.add(t);
            }
        }
        return rslt;
    }
    public static <T> List<T> deepCopy(List<T> src) throws IOException, ClassNotFoundException {  
    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();  
    ObjectOutputStream out = new ObjectOutputStream(byteOut);  
    out.writeObject(src);  
  
    ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());  
    ObjectInputStream in = new ObjectInputStream(byteIn);  
    @SuppressWarnings("unchecked")  
    List<T> dest = (List<T>) in.readObject();  
    return dest;  
    }  
}
