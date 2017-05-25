package com.jafin.excel.ui;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.jafin.excel.R;
import com.jafin.excel.util.Reflector;

import java.util.List;

/**
 * Created by 何锦发 on 2017/5/10.
 */
public class Excel extends LinearLayout {
    /**
     * 是否有标题，默认有
     */
    private boolean hasHeader;
    /**
     * 是否支持筛选,默认支持
     */
    private boolean hasFilter;
    /**
     * 筛选窗体的高度，默认50
     */
    private int filterHeight;
    /**
     * 标题高度，默认50
     */
    private int headerHeight;
    /**
     * 行高度，默认50
     */
    private int itemHeight;
    /**
     * 总宽度，默认屏幕宽度
     */
    private int width;
    /**
     * 是否冻结标题，默认是
     */
    private boolean freezeHeader;

    //region 组合控件
    private LinearLayout mHeaderView;//固定标题
    private HorizontalListView mFilterView;//筛选显示
    private ListView mListView;//主表格
    private ViewGroup mView;
    //endregion

    private Activity mActivity;//控件所在的界面
    private Reflector mReflector;//反射器
    private List mData;//数据
    private MyAdapter mAdapter;

    public Excel(Context context) {
        super(context);
    }

    public Excel(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Excel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mActivity = (Activity) context;
        View root = LayoutInflater.from(context).inflate(R.layout.excel, this, true);
        mListView = (ListView) root.findViewById(R.id.body);
        mFilterView = (HorizontalListView) root.findViewById(R.id.filter);
        mHeaderView = (LinearLayout) root.findViewById(R.id.header);
        mView = (ViewGroup) root.findViewById(R.id.rv_main);
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.Excel);
        if (attributes != null) {
            filterHeight = attributes.getDimensionPixelSize(R.styleable.Excel_filterHeight, 150);
            headerHeight = attributes.getDimensionPixelSize(R.styleable.Excel_headerHeight, 150);
            width = attributes.getDimensionPixelSize(R.styleable.Excel_headerHeight, 2000);
            mFilterView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, filterHeight));
            mHeaderView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, headerHeight));
            mView.setLayoutParams(new LayoutParams(width, LayoutParams.WRAP_CONTENT));
            hasFilter = attributes.getBoolean(R.styleable.Excel_hasFilter, true);
            hasHeader = attributes.getBoolean(R.styleable.Excel_hasHeader, true);
            freezeHeader = attributes.getBoolean(R.styleable.Excel_freezeHeader, true);
            attributes.recycle();
        }
    }

    @SuppressWarnings("unchecked")
    public void show(List data) {
        if (data == null || (data.size() == 0 && mReflector == null)) {
            return;
        }
        if (mReflector == null) {
            mReflector = new Reflector(data.get(0).getClass());
        }
        if (mData == null) {
            mData = data;
        } else {
            mData.clear();
            mData.addAll(data);
        }
        if (mAdapter == null) {
            mAdapter = new MyAdapter();
            mListView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return null;
        }
    }

    public boolean isNUll(Object...args){
        for (Object arg : args) {
            if(arg==null){
                return true;
            }
        }
        return false;
    }
}
