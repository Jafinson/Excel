package com.jafin.excel.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jafin.excel.R;
import com.jafin.excel.bean.Column;
import com.jafin.excel.bean.Condition;
import com.jafin.excel.util.Filter;
import com.jafin.excel.util.Reflector;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    /**
     * 表格字体大小
     */
    private int textSize;
    //region 组合控件
    private LinearLayout mHeaderView;//固定标题
    private HorizontalListView mFilterView;//筛选显示
    private ListView mListView;//主表格
    private ViewGroup mView;
    //endregion


    private Activity mActivity;//控件所在的界面
    private Reflector mReflector;//反射器
    private Filter mFilter;//筛选器
    private List mData;//数据
    private MyAdapter mAdapter;
    private List<Column> mColumns;//列
    private Set<Condition> mCondition;

    public Excel(Context context) {
        this(context, null);
    }

    public Excel(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
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
            textSize = attributes.getDimensionPixelSize(R.styleable.Excel_android_textSize, 10);
            filterHeight = attributes.getDimensionPixelSize(R.styleable.Excel_filterHeight, 150);
            headerHeight = attributes.getDimensionPixelSize(R.styleable.Excel_headerHeight, 80);
            itemHeight = attributes.getDimensionPixelSize(R.styleable.Excel_itemHeight, 80);
            width = attributes.getDimensionPixelSize(R.styleable.Excel_width, 2000);
            mFilterView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, filterHeight));
            mHeaderView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    headerHeight));
            mListView.setLayoutParams(new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.MATCH_PARENT));
            hasFilter = attributes.getBoolean(R.styleable.Excel_hasFilter, true);
            hasHeader = attributes.getBoolean(R.styleable.Excel_hasHeader, true);
            freezeHeader = attributes.getBoolean(R.styleable.Excel_freezeHeader, true);
            attributes.recycle();
        }
    }

    /**
     * 必须要在show方法之前调用，否则会报错
     *
     * @param columns
     */
    public void setColumn(List<Column> columns) {
        this.mColumns = columns;
        initHeader();
    }

    /**
     * 获取好数据，在表格设计好后就可以显示了
     *
     * @param data 要显示的数据
     * @throws Exception 必先保证在setColumn方法之后调用，如果传入的data没有数据同样报错
     */
    @SuppressWarnings("unchecked")
    public void show(List data) throws Exception {
        if (mColumns == null || mColumns.size() == 0) {
            throw new Exception("列还没设计好或没有列可显示");
        }
        if (data == null || (data.size() == 0 && mReflector == null)) {
            throw new Exception("没有可显示的数据");
        }
        if (mReflector == null) {
            mReflector = new Reflector(data.get(0).getClass());
            if (this.mColumns != null) {
                for (Column column : mColumns) {
                    column.setInfo(mReflector);
                }
            }
        }
        mFilter = new Filter(data);
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

    /**
     * 参数检查，如果传入的参数中有空则返回true
     *
     * @param args 要检查的参数
     * @return 参数list中是否有空
     */
    public boolean isNUll(Object... args) {
        for (Object arg : args) {
            if (arg == null) {
                return true;
            }
        }
        return false;
    }

    private void initHeader() {
        for (final Column column : mColumns) {
            TextView text = getText(column, true);
            text.setText(column.getName());
            mHeaderView.addView(text);
            if (hasFilter && hasHeader) {
                text.setCompoundDrawablesWithIntrinsicBounds(null, null, mActivity.getResources().getDrawable(R
                        .drawable.arrow_drop_down), null);
            }
            text.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    showFilterDialog(column);
                }
            });
        }
    }

    @SuppressWarnings("unchecked")
    private void showFilterDialog(final Column column) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        final List valueSet = mFilter.getValueSet(column.info.field);
        String[] content = new String[valueSet.size()];
        for (int i = 0; i < content.length; i++) {
            content[i] = valueSet.get(i).toString();
        }
        final boolean[] check = new boolean[valueSet.size()];
        builder.setMultiChoiceItems(content, check, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                check[which] = isChecked;
            }
        });
        if (mCondition == null) {
            mCondition = new HashSet<>();
        }
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (int i = 0; i < check.length; i++) {
                    if (check[i]) {
                        Object o = valueSet.get(i);
                        mCondition.add(new Condition(column.info.field, column.info.getMethod, o));
                    }
                }
                mData = mFilter.filter(mCondition);
                mAdapter.notifyDataSetChanged();
            }
        });
        builder.show();
    }

    private LinearLayout getItemView() {
        LinearLayout rslt = new LinearLayout(mActivity);
        rslt.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, itemHeight));
        rslt.setOrientation(HORIZONTAL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            rslt.setBackgroundColor(getResources().getColor(R.color.lineColor, null));
        } else {
            rslt.setBackgroundColor(getResources().getColor(R.color.lineColor));
        }
        return rslt;
    }

    private TextView getText(Column column, boolean isHeader) {
        TextView text;
        if (isHeader) {
            text = new TextView(mActivity);
        } else {
            switch (column.type) {
                case EDIT:
                    text = new EditText(mActivity);
                    break;
                case CHECK:
                    text = new CheckBox(mActivity);
                    break;
                default:
                    text = new TextView(mActivity);

            }
        }
        LayoutParams lp = new LayoutParams(0, LayoutParams.MATCH_PARENT, column.getWidth());
        lp.setMargins(1, 1, 1, 1);
        lp.gravity = Gravity.CENTER;
        text.setGravity(Gravity.CENTER);
        text.setLayoutParams(lp);
        text.setBackgroundColor(Color.WHITE);
        text.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        text.setTextColor(Color.BLACK);
        return text;
    }

    private class MyAdapter extends BaseAdapter {
        private int column;//列数
        private int row;//行数

        public MyAdapter() {
            column = mColumns.size();
            row = mData.size();
        }

        @Override
        public int getCount() {
            return row;
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
            if (convertView == null) {
                LinearLayout item = getItemView();
                for (Column column : mColumns) {
                    TextView text = getText(column, false);
                    item.addView(text);
                }
                convertView = item;
            }
            Object o = mData.get(position);//要操作的对象
            for (int i = 0; i < mColumns.size(); i++) {
                try {
                    TextView text = (TextView) ((ViewGroup) convertView).getChildAt(i);
                    // String field = mColumns.get(i).getField();
                    //Method method = (Method) mReflector.getter.get(field);
                    Method method = mColumns.get(i).info.getMethod;
                    text.setText(method.invoke(o).toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return convertView;
        }


    }
}
