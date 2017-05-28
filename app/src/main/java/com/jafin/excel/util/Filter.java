package com.jafin.excel.util;

import com.jafin.excel.bean.Condition;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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
    private Map<Condition.Key, List<T>> conditions;
    /**
     * 上一次进行筛选的条件集，如果为空则为第一次筛选；
     */
    private Set<Condition.Key> lastConditions;
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
        this.data = new ArrayList<>();
        this.data.addAll(data);
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
                Condition.Key key = new Condition.Key(field, value);
                List<T> ts;
                if (conditions.containsKey(key)) {
                    ts = conditions.get(key);
                } else {
                    ts = new ArrayList<>();
                    conditions.put(key, ts);
                }
                ts.add(t);
                //初始化noRepeatedValues
                if (!noRepeatedValues.containsKey(field)) {
                    noRepeatedValues.put(field, new TreeSet());
                }
                noRepeatedValues.get(field).add(value);
            }
        }
    }

    //获取所有的可选择的不重复的值列表
    public List getValueSet(Field field, boolean isAll) {
        List rslt = new ArrayList();
        if (isAll) {
            Set set = noRepeatedValues.get(field);
            for (Object o : set) {
                rslt.add(o);
            }
        } else {
            if (lastData == null) {
                return this.getValueSet(field, true);
            }
            if (lastData.size() == 0) {
                return lastData;
            }
            Set set = new HashSet();
            for (T t : lastData) {
                Object value = Reflector.getValue(field, t);
                if(value!=null){
                    set.add(value);
                }
            }
            rslt.addAll(set);
        }
        return rslt;
    }

    /**
     * 传入条件进行筛选
     *
     * @param keys 筛选条件
     * @return 筛选结果
     */
    @SuppressWarnings("unchecked")
    public List<T> filter(Set<Condition.Key> keys) {
        if (keys == null || keys.size() == 0) {
            return data;
        }
        List<T> rslt = data;
        //keys按照field分类，得出condition列表
        Map<Field, List<T>> rsltByField = new HashMap<>();
        for (Condition.Key key : keys) {
            Field field = key.field;
            if (rsltByField.containsKey(field)) {
                rsltByField.get(field).addAll(conditions.get(key));
            } else {
                ArrayList<T> temp = new ArrayList<>();
                temp.addAll(conditions.get(key));
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
        lastConditions = keys;
        return rslt;
    }

    public Map<String, Object> getCache() {
        Map<String, Object> rslt = new HashMap<>();
        rslt.put("condition", lastConditions);
        rslt.put("data", lastData);
        return rslt;
    }
}
