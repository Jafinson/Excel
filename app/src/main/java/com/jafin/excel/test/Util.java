package com.jafin.excel.test;

import com.jafin.excel.annotation.Name;
import com.jafin.excel.bean.Column;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 何锦发 on 2017/5/26.
 */
public class Util {
    public static List<Student> getList() throws Exception {
        List<Student> rslt = new ArrayList<>();
        Class<Student> clz = Student.class;
        Field[] fields = clz.getDeclaredFields();
        for (int i = 0; i < 100; i++) {
            Student student = new Student();
            for (Field field : fields) {
                field.setAccessible(true);
                if (field.getType() == Integer.class) {
                    field.set(student, i%5);
                }
                if (field.getType() == Double.class) {
                    field.set(student, 0.0 + i);
                }
                if (field.getType() == Float.class) {
                    field.set(student, 0.0f + i);
                }
                if (field.getType() == String.class) {
                    field.set(student, field.getName() + i%8);
                }
            }
            rslt.add(student);
        }
        return rslt;
    }

    public static List<Column> getColumn() {
        List<Column> rslt = new ArrayList<>();
        Field[] fields = Student.class.getDeclaredFields();
        for (Field field : fields) {
            Name name = field.getAnnotation(Name.class);
            if (name != null) {
                rslt.add(new Column(name.name(), field.getName()));
            }
        }
        rslt.get(2).type= Column.Type.CHECK;
        rslt.get(5).type= Column.Type.EDIT;
        return rslt;
    }
}
