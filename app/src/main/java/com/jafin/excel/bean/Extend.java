package com.jafin.excel.bean;

/**
 * Created by 何锦发 on 2017/5/25.
 */

import java.util.ArrayList;
import java.util.List;

/**
 * 重新封装对象，加一个是否被选择的字段
 */
public class Extend {
    /***
     * 表示该对象是否被选中
     */
    public boolean flag;
    /**
     * 真实的list中的对象
     */
    public Object object;

    public Extend(Object object) {
        this.object = object;
    }

    /**
     * 将常规的list对象转成多一个属性的对象
     *
     * @param data 真实的对象list
     * @return 重新封装的对象list
     */
    public static List<Extend> toExtendList(List data) {
        List<Extend> rslt = new ArrayList<>();
        for (Object o : data) {
            rslt.add(new Extend(o));
        }
        return rslt;
    }

    /**
     * 拆封
     * @param data 封装好的对象lsit
     * @return 拆封后的对象list
     */
    public static List toOriginalList(List<Extend> data) {
        List rslt = new ArrayList();
        for (Extend extend : data) {
            rslt.add(extend.object);
        }
        return rslt;
    }

    /**
     * 获取一组flag全为真或全为假的list
     *
     * @param source 源数据
     * @param flag   全真或全假
     * @return 同一个flag的list
     */
    public static List<Extend> getSameFlagList(List<Extend> source, boolean flag) {
        List<Extend> rslt = new ArrayList<>();
        for (Extend extend : source) {
            if (extend.flag == flag) {
                rslt.add(extend);
            }
        }
        return rslt;
    }
}
