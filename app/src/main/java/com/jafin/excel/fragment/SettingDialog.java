package com.jafin.excel.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.jafin.excel.R;
import com.jafin.excel.bean.Column;
import com.jafin.excel.util.ListUtil;
import com.jafin.excel.widget.Excel;

import java.util.Arrays;
import java.util.List;

public class SettingDialog extends DialogFragment implements View.OnClickListener {
    private List<Column> mColumns;//复制的用于缓存
    private List<Column> mSource;//外面传的源数据
    private Excel excel;
    private List<Column> myColumns;//本dialog显示的布局，自己构建
    {
        myColumns= Arrays.asList(
                new Column("名称","name"),
                new Column("字段","field"),
                new Column("宽度","width",true),
                new Column("可视","isShow"),
                new Column("冻结","frozen")
                );
    }
    public SettingDialog() {
    }

    @SuppressLint("ValidFragment")
    public SettingDialog(List<Column> columns, Excel excel) {
        this.mSource = columns;
        this.mColumns = ListUtil.columnClone(columns);
        this.excel = excel;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_column, null);
        Excel<Column> mExcel = (Excel<Column>) view.findViewById(R.id.excel);
        View bt_confirm = view.findViewById(R.id.bt_confirm);
        bt_confirm.setOnClickListener(this);
        try {
            mExcel.initColumns(myColumns, Column.class, Excel.CHOICE_MODE_NONE);
            mExcel.show(mColumns);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog mDialog = super.onCreateDialog(savedInstanceState);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return mDialog;
    }

    @Override
    public int show(FragmentTransaction transaction, String tag) {
        return super.show(transaction, tag);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_confirm:
                mSource.clear();
                mSource.addAll(mColumns);
                excel.refreshColumns(mSource);
                dismiss();
                break;
        }
    }
}
