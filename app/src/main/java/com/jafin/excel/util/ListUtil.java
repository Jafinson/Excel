package com.jafin.excel.util;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by 何锦发 on 2017/4/7.
 * list的处理工具类
 */
public class ListUtil {
    /**
     * 深度复制,性能太差
     *
     * @param bean 要复制的list
     * @return 复制后得list
     */
    @SuppressWarnings("unchecked")
    public static <T extends Serializable> List<T> copy(List<T> bean) {
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
    public static <T> List<T> filter(List<T> source, Class clz, Map<String, Set> condition) {
        if (condition.size() == 0) {
            return source;
        }
        boolean[] checked = new boolean[source.size()];
        for (int i = 0; i < checked.length; i++) {
            checked[i]=true;
        }
        for (int i = 0; i < source.size(); i++) {
            T t = source.get(i);
            for (Map.Entry<String, Set> entry : condition.entrySet()) {
                try {
                    String field = entry.getKey();
                    Field declaredField = clz.getDeclaredField(field);
                    declaredField.setAccessible(true);
                    Object o = declaredField.get(t);
                    if(!entry.getValue().contains(o)){
                        checked[i]=false;
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
        }
        return getAfterFilter(source, checked);
    }

    private static <T> List<T> getAfterFilter(List<T> source, boolean[] checked) {
        List<T> result = new ArrayList<>();
        for (int i = 0; i < checked.length; i++) {
            if (checked[i]) {
                result.add(source.get(i));
            }
        }
        return result;
    }


    /***
     * 获取列表中某个字段的不重复值
     *
     * @param source 数据源
     * @param field 要获取哪个字段
     */
    @SuppressWarnings("unchecked")
    public static <T> List getNoRepeatValue(List<T> source, Field field) throws InvocationTargetException, IllegalAccessException {
        List result = new ArrayList();
        Set temp = new TreeSet();
        field.setAccessible(true);
        for (T t : source) {
            Object invoke = field.get(t);
            if (invoke != null && !"".equals(invoke)) {
                temp.add(invoke);
            }
        }
        result.addAll(temp);
        return result;
    }

}
