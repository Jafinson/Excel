package com.jafin.excel.util;

import com.jafin.excel.bean.Condition;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by 何锦发 on 2017/4/7.
 */

/**
 * 筛选器，有缓存操作
 */
public class Filter<T> {

    /**
     * 要筛选的列表
     */
    private List<T> data;
    /**
     * 条件集合,以field归类
     */
    private Map<Field, Set<Condition<T>>> conditions;
    /**
     * 上一次进行筛选的条件集，如果为空则为第一次筛选；
     */
    private Set<Condition<T>> lastConditions;
    /**
     * 上一次的筛选结果
     */
    private List<T> lastData;
    /**
     * 不重复值集合
     */
    private Map<Field, Set> noRepeatedValues;

    /**
     * @param data 要筛选的数据
     * @throws Exception 如果data长度为0则抛出异常
     */
    public Filter(List<T> data) throws Exception {
        init(data);
    }

    @SuppressWarnings("unchecked")
    public void init(List<T> data) throws Exception {
        this.data = data;
        if (data.size() == 0) {
            throw new Exception("没有数据可筛选");
        }
        conditions = new HashMap<>();
        noRepeatedValues = new HashMap<>();
        Reflector reflector = new Reflector(data.get(0).getClass());
        Collection<Field> fields = reflector.fields.values();
        for (Field field : fields) {
            for (T t : data) {
                //初始化condition
                Method get = (Method) reflector.getter.get(field.getName());
                Object value = get.invoke(t);
                Condition condition;
                if (conditions.containsKey(field)) {
                    condition = conditions.get(field);
                } else {
                    condition = new Condition(field, get, value);
                    conditions.put(field, condition);
                }
                condition.getRslt().add(t);
                //初始化noRepeatedValues
                if (!noRepeatedValues.containsKey(field)) {
                    noRepeatedValues.put(field, new TreeSet());
                }
                noRepeatedValues.get(field).add(value);
            }
        }
    }

    public List getValueSet(Field field) {
        Set set = noRepeatedValues.get(field);
        List rslt = new ArrayList();
        for (Object o : set) {
            rslt.add(o);
        }
        return rslt;
    }

    /**
     * 传入条件进行筛选
     *
     * @param conditions 筛选条件
     * @return 筛选结果
     */
    public List<T> filter(Set<Condition<T>> conditions) {
        if (conditions == null || conditions.size() == 0) {
            return data;
        }
        List<T> rslt = data;
        Map<Field, List<T>> rsltByField = new HashMap<>();//按
        for (Condition<T> condition : conditions) {
            Field field = condition.getField();
            if (rsltByField.containsKey(field)) {
                rsltByField.get(field).addAll(this.conditions.get(field).getRslt());
            } else {
                List<T> temp = new ArrayList<>();
                temp.addAll(this.conditions.get(field).getRslt());
                rsltByField.put(field, temp);
            }
        }
        for (List<T> ts : rsltByField.values()) {
            rslt.retainAll(ts);
            if (rslt.size() == 0) {
                break;
            }
        }
        lastData = rslt;
        lastConditions=conditions;
        return rslt;
    }

    public Map<String, Object> getCache() {
        Map<String, Object> rslt = new HashMap<>();
        rslt.put("condition", lastConditions);
        rslt.put("data", lastData);
        return rslt;
    }

    private class Key{
        public Field field;
        public Object value;

        public Key(Field field, Object value) {
            this.field = field;
            this.value = value;
        }
    }
}
