package com.jafin.excel.bean;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by 何锦发 on 2017/5/27.
 */
public class Condition<T> {
    public Key key;
    public List<T> rslt;

    public Condition(Key key, List<T> rslt) {
        this.key = key;
        this.rslt = rslt;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return key.equals(obj);
    }

    public static  class Key {
        public Object value;
        public Field field;

        public Key(Field field, Object value) {
            this.field = field;
            this.value = value;
        }

        @Override
        public int hashCode() {
            String code = field.getName() + "=" + value.toString();
            return code.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return this.field.equals(((Key) obj).field) && this.value.equals(((Key) obj).value);
        }
    }
}
