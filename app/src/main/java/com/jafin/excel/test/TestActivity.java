package com.jafin.excel.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jafin.excel.R;
import com.jafin.excel.util.ColumnFactory;
import com.jafin.excel.widget.Excel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Excel excel = findViewById(R.id.excel);
        excel.setColumns(ColumnFactory.createByClz(Student.class));
        excel.show(createData());
    }

    public static List<Student> createData() {
        List<Student> result = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            Student student = new Student();
            student.setName("student" + i);
            student.setAge(i);
//            student.setAge(random.nextInt(25) + 10);
            student.setAddress("address" + i);
            student.setPhone("phone" + i);
            student.setScore(random.nextFloat() * 50 + 50);
            result.add(student);
        }
        return result;
    }
}
