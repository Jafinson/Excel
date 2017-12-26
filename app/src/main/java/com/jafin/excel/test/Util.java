package com.jafin.excel.test;

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
                if (field.getType().getName().equals("int")) {
                    field.set(student, i % 5);
                }
                if (field.getType().getName().equals("double")) {
                    field.set(student, 0.0 + i);
                }
                if (field.getType().getName().equals("float")) {
                    field.set(student, 0.0f + i);
                }
                if (field.getType() == String.class) {
                    field.set(student, field.getName() + i % 8);
                }
            }
            rslt.add(student);
        }
        return rslt;
    }

    public static List<Column> getColumn() {
        return Column.createByClz(Student.class);
    }
}
