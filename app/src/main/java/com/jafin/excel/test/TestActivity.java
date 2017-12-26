package com.jafin.excel.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jafin.excel.R;
import com.jafin.excel.bean.Column;
import com.jafin.excel.enums.ViewTypeEnum;
import com.jafin.excel.widget.Excel;

import java.util.List;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Excel excel = (Excel) findViewById(R.id.excel);
        List<Column> columns = Column.createByClz(Student.class);
        columns.get(1).type= ViewTypeEnum.EDIT;
        excel.initColumns(columns);
        try {
            excel.show(Util.getList());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
