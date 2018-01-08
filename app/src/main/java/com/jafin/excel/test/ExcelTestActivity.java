package com.jafin.excel.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.jafin.excel.R;
import com.jafin.excel.bean.Column;
import com.jafin.excel.bean.Student;
import com.jafin.excel.fragment.SettingDialog;
import com.jafin.excel.util.ColumnFactory;
import com.jafin.excel.widget.Excel;

import java.util.ArrayList;
import java.util.List;


public class ExcelTestActivity extends AppCompatActivity implements View.OnClickListener {

    private Excel<Student> excel;
    private List<Student> mData;
    private List<Column> mColumns;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excel_test);
        excel = (Excel) findViewById(R.id.excel);
        View bt_setting = findViewById(R.id.bt_setting);
        bt_setting.setOnClickListener(this);
        View bt_all = findViewById(R.id.bt_all);
        bt_all.setOnClickListener(this);
        View bt_clear = findViewById(R.id.bt_clear);
        bt_clear.setOnClickListener(this);
        View bt_inverse = findViewById(R.id.bt_inverse);
        bt_inverse.setOnClickListener(this);
        View bt_count = findViewById(R.id.bt_count);
        bt_count.setOnClickListener(this);
        try {
            mColumns = new ArrayList<>();
            String[] fields = {"address", "school", "address", "name", "age", "score"};
         //   mColumns.addAll(ColumnFactory.createByFields(Student.class, fields));
            mColumns.addAll(ColumnFactory.createByClz(Student.class));
           // mColumns.get(5).editable = true;
            excel.initColumns(mColumns, Student.class, 2);
            mData = getData();
            excel.show(mData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<Student> getData() {
        List<Student> result = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            int i1 = i % 3;
            int i2 = i % 7;
            int i3 = i % 8;
            int i4 = i % 3 * 3 + 50;
            Student student = new Student();
            student.setAddress("address" + i1);
            student.setSchool("school" + i2);
            student.setName("name" + i);
            student.setAge(i3);
            student.setScore(i4);
            result.add(student);
        }
        return result;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_setting:
                SettingDialog dialog = new SettingDialog(mColumns, excel);
                dialog.show(getSupportFragmentManager(), "ColumnSetting");
                break;
            case R.id.bt_all:
                excel.checkAll();
                break;
            case R.id.bt_clear:
                excel.clear();
                break;
            case R.id.bt_inverse:
                excel.inverse();
                break;
            case R.id.bt_count:
                Toast.makeText(this, mData.get(excel.getCheckedPosition()).getAddress() + "", Toast.LENGTH_SHORT).show();
//                Toast.makeText(this, excel.getCheckedData().size() + "", LENGTH_LONG).show();
                break;
        }
    }
}
