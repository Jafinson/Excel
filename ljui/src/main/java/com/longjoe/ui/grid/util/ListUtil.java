package com.longjoe.ui.grid.util;

import com.longjoe.ui.grid.bean.LjBean;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by 何锦发 on 2017/4/7.
 */
public class ListUtil {
    /**
     * 深度复制,性能太差
     *
     * @param bean 要复制的list
     * @return 复制后得list
     */
    @SuppressWarnings("unchecked")
    public static <T extends LjBean> List<T> copy(List<T> bean) {
        List<T> list = new ArrayList();
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(bean);
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
            list.addAll((List<T>) ois.readObject());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 筛选列表
     *
     * @param source    待筛选的列表
     * @param condition 筛选条件《属性，属性值列表》
     * @param <T>       要筛选的对象
     */
    public static <T extends LjBean> List<T> filter(List<T> source, Class clz, Map<String, List>
            condition) {
        LjBean.setAllFilter(source, false);
        if (condition.size() == 0) {
            return source;
        }
        //Map<String, Method> getters = reflector.getGetters();
        for (Map.Entry<String, List> entry : condition.entrySet()) {
            //Method getter = getters.get(entry.getKey());
            for (T t : getAfterFilter(source, false)) {
                List values = entry.getValue();
                int i = 0;
                while (i < values.size()) {
                    Object value = values.get(i);
                    try {
                        if (value.equals(clz.getDeclaredField(entry.getKey()).get(t))) {
                            t.setFiltered(false);
                            break;
                        } else {
                            t.setFiltered(true);
                        }
                    } catch (Exception e) {
                        t.setFiltered(false);
                    }
                    if (i == values.size() - 1) {
                        t.setFiltered(true);
                    }
                    i++;
                }
            }
            if (getAfterFilter(source, false).size() == 0) {
                return getAfterFilter(source, false);
            }
        }
        return getAfterFilter(source, false);
    }

    /**
     * 获取筛选部分或者非筛选部分
     * true为不筛掉   false 为筛掉
     *
     * @param isFiltered 是否被筛选
     * @param source     源列表
     * @param <T>        list的对象
     * @return 剔除筛选后的list
     */
    public static <T extends LjBean> List<T> getAfterFilter(List<T> source, boolean isFiltered) {
        List<T> reslut = new ArrayList<>();
        for (T t : source) {
            if (t.isFiltered() == isFiltered) {
                reslut.add(t);
            }
        }
        return reslut;
    }

    /**
     * 获取被选择的list
     *
     * @param source 源列表
     * @param <T>    list的对象
     * @return 被选中的list
     */
    public static <T extends LjBean> List<T> getChecked(List<T> source) {
        List<T> reslut = new ArrayList<>();
        for (T t : source) {
            if (t.isChecked()) {
                reslut.add(t);
            }
        }
        return reslut;
    }

    /***
     * 获取列表中某个字段的不重复值
     *
     * @param source 数据源
     * @param field 要获取哪个字段
     * @param <T>
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @SuppressWarnings("unchecked")
    public static <T extends LjBean> List getNoRepeatValue(List<T> source, Field field) throws
            InvocationTargetException, IllegalAccessException {
        List result = new ArrayList();
        Set temp = new TreeSet();
        field.setAccessible(true);
        for (T t : source) {
            Object invoke = field.get(t);
            if (invoke!=null&&!t.isFiltered() && !"".equals(invoke)) {
                temp.add(invoke);
            }
        }
        result.addAll(temp);
        return result;
    }

}
