package com.jafin.excel.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jafin.excel.R;
import com.jafin.excel.view.Excel;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Excel excel = (Excel) findViewById(R.id.excel);
        excel.setColumn(Util.getColumn());
        try {
            excel.show(Util.getList());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
