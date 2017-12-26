package com.jafin.excel.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jafin.excel.R;
import com.jafin.excel.bean.Column;
import com.jafin.excel.bean.Condition;
import com.jafin.excel.fragment.SearchableFragment;
import com.jafin.excel.util.Filter;
import com.jafin.excel.util.Utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by 何锦发 on 2017/5/10.
 * 表格显示控件
 */
@SuppressWarnings("unchecked")
public class Excel<T> extends LinearLayout {
    //region 属性
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
    private int screenWidth;
    /**
     * 平均每列宽度
     */
    private int averageColumnWidth = 200;
    /**
     * 是否冻结标题，默认是
     */
    private boolean freezeHeader;
    /**
     * 表格字体大小
     */
    private int textSize;
    //endregion
    //region 组合控件
    private LinearLayout mHeaderView;//固定标题
    private HorizontalListView mFilterView;//筛选显示
    private ListView mListView;//主表格
    private ViewGroup mView;
    //endregion
    private Activity mActivity;//控件所在的界面
    //private Reflector mReflector;//反射器
    private Filter mFilter;//筛选器
    private List<T> mData;//数据
    private MyAdapter mAdapter;
    private List<Column> mColumns;//列
    private Set<Condition.Key> mConditions;
    /**
     * 软键盘管理器，用于控制软件的显示和隐藏
     * 反转：mInputManager.showSoftInput(view,InputMethodManager.SHOW_FORCED);
     * 显示：mInputManager.showSoftInput(view,InputMethodManager.SHOW_FORCED);
     * 隐藏：mInputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
     * 返回状态：boolean isOpen= mInputManager.isActive();
     */
    private InputMethodManager mInputManager;

    public Excel(Context context) {
        this(context, null);
    }

    public Excel(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Excel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mActivity = Utils.scanForActivity(context);
        mInputManager = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        View root = LayoutInflater.from(context).inflate(R.layout.excel, this, true);
        mListView = (ListView) root.findViewById(R.id.body);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListView.setItemChecked(position, ((ItemGroup) view).isChecked());
            }
        });
        mListView.setOnScrollListener(new MyScrollListener());
        mFilterView = (HorizontalListView) root.findViewById(R.id.filter);
        mHeaderView = (LinearLayout) root.findViewById(R.id.header);
        mView = (ViewGroup) root.findViewById(R.id.rv_main);
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.Excel);
        if (attributes != null) {
            textSize = attributes.getDimensionPixelSize(R.styleable.Excel_android_textSize, 10);
            filterHeight = attributes.getDimensionPixelSize(R.styleable.Excel_filterHeight, 150);
            headerHeight = attributes.getDimensionPixelSize(R.styleable.Excel_headerHeight, 80);
            itemHeight = attributes.getDimensionPixelSize(R.styleable.Excel_itemHeight, 80);
            screenWidth = attributes.getDimensionPixelSize(R.styleable.Excel_width, 2000);
            mFilterView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, filterHeight));
            mHeaderView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, headerHeight));
            mListView.setLayoutParams(new LinearLayout.LayoutParams(screenWidth, LinearLayout.LayoutParams.MATCH_PARENT));
            hasFilter = attributes.getBoolean(R.styleable.Excel_hasFilter, true);
            hasHeader = attributes.getBoolean(R.styleable.Excel_hasHeader, true);
            freezeHeader = attributes.getBoolean(R.styleable.Excel_freezeHeader, true);
            attributes.recycle();
        }
    }

    /**
     * 设置平均列宽
     *
     * @param averageColumnWidth 平均列宽
     */
    public void setAverageColumnWidth(int averageColumnWidth) {
        this.averageColumnWidth = averageColumnWidth;
    }

    /**
     * 必须要在{@link #show(List)}方法之前调用，否则会报错
     * 设计好excel要显示的列
     *
     * @param columns 列信息
     */
    public void initColumns(List<Column> columns) {
        this.mColumns = columns;
        setTableWidth(columns.size() * averageColumnWidth > screenWidth ? columns.size() * averageColumnWidth : screenWidth);
        initHeader();
    }

    /**
     * 获取好数据，在表格设计好后就可以显示了
     *
     * @param data 要显示的数据
     * @throws Exception 必先保证在{@link #initColumns(List)} 方法之后调用
     */
    public void show(List data) throws Exception {
        if (data == null) {
            return;
        }
        if (mColumns == null || mColumns.size() == 0) {
            throw new Exception("列还没设计好或没有列可显示,请先调用initColumns方法");
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
            mFilter.init(data);
        }
    }

    public int getCheckedItemCount() {
        return mListView.getCheckedItemCount();
    }

    /**
     * 参数检查，如果传入的参数中有空则返回true
     *
     * @param args 要检查的参数
     * @return 参数list中是否有空
     */
    private boolean isNUll(Object... args) {
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
                text.setCompoundDrawablesWithIntrinsicBounds(null, null, mActivity.getResources().getDrawable(R.drawable.arrow_drop_down), null);
            }
            text.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    showFilterDialog(column);
                }
            });
        }
    }

    /*private void showFilterDialog(final Column column) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        final List valueSet = mFilter.getValueSet(column.getField(), false);
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
        if (mConditions == null) {
            mConditions = new HashSet<>();
        }
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (int i = 0; i < check.length; i++) {
                    if (check[i]) {
                        Object o = valueSet.get(i);
                        mConditions.add(new Condition.Key(column.getField(), o));
                    }
                }
                mData = mFilter.filter(mConditions);
                mAdapter.notifyDataSetChanged();
            }
        });
        builder.show();
    }*/

    public void showFilterDialog(final Column column) {
        try {
            final List filters = mFilter.getValueSet(column.getField(), false);
            SearchableFragment dialog = new SearchableFragment(filters, column.getName());
            dialog.setListener(new SearchableFragment.OnPositiveListener() {
                @Override
                public void callback(List data) {
                    if (data.size() != 0) {
                        if (mConditions == null) {
                            mConditions = new HashSet<>();
                        }
                        for (Object o : data) {
                            mConditions.add(new Condition.Key(column.getField(), o));
                        }
                        mData = mFilter.filter(mConditions);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            });
            dialog.show(mActivity.getFragmentManager(), "filter");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private ItemGroup getItemView() {
        ItemGroup rslt = new ItemGroup(mActivity);
        rslt.setCheckedColor(Color.GRAY);
        rslt.setUncheckedColor(Color.WHITE);
        rslt.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, itemHeight));
        rslt.setOrientation(HORIZONTAL);
        rslt.setBackgroundColor(getResources().getColor(R.color.lineColor));
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
                    text.setTextColor(getResources().getColor(R.color.editTextColor));
                    text.setPadding(0, 0, 0, 0);
                    break;
                case CHECK:
                    text = new CheckBox(mActivity);
                    text.setTextColor(Color.BLACK);
                    break;
                default:
                    text = new TextView(mActivity);
                    text.setTextColor(Color.BLACK);

            }
        }
        LayoutParams lp = new LayoutParams(0, LayoutParams.MATCH_PARENT, column.getWidth());
        lp.setMargins(1, 1, 1, 1);
        lp.gravity = Gravity.CENTER;
        text.setGravity(Gravity.CENTER);
        text.setLayoutParams(lp);
        text.setBackgroundColor(Color.WHITE);
        text.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        //text.setTextColor(Color.BLACK);
        return text;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //得出屏幕宽度
        screenWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        this.setMeasuredDimension(screenWidth, parentHeight);
        if (mColumns != null) {
            //取屏幕宽度与计算宽度最大者作为表格的总宽度
            setTableWidth(screenWidth > mColumns.size() * averageColumnWidth ? screenWidth : mColumns.size() * averageColumnWidth);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void setTableWidth(int width) {
        ViewGroup.LayoutParams lp = mListView.getLayoutParams();
        lp.width = width;
        mListView.setLayoutParams(lp);
    }

    private class MyAdapter extends BaseAdapter {
        private int col;//列数,从0开始
        private int row;//行数，从0开始

        public MyAdapter() {
            col = mColumns.size();
            row = mData.size();
        }

        @Override
        public int getCount() {
            row = mData.size();
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

        int mCurrentTouchedIndex = -1;//记录editTetxt焦点所在的position，得到焦点的时候值为position ，失去焦点的时候置为-1

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                ItemGroup item = getItemView();
                for (Column column : mColumns) {
                    TextView text = getText(column, false);
                    item.addView(text);
                }
                convertView = item;
            }
            ((ItemGroup) convertView).setChecked(mListView.isItemChecked(position));
            //设置显示的内容
            final Object o = mData.get(position);//要操作的对象
            for (int i = 0; i < mColumns.size(); i++) {
                try {
                    final TextView text = (TextView) ((ViewGroup) convertView).getChildAt(i);
                    //设置内容
                    final Column column = mColumns.get(i);//column对象
                    switch (column.getFieldType()) {
                        case DOUBLE:
                            text.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                            break;
                        case FLOAT:
                            text.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                            break;
                        case INT:
                            text.setInputType(InputType.TYPE_CLASS_NUMBER);
                            break;
                    }
                    String content = column.getValue(o).toString();//该单元格的值
                    text.setText(content);
                    if (text instanceof EditText) {
                        text.setOnFocusChangeListener(new OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                if (hasFocus) {
                                    ((EditText) text).setSelection(0, text.getText().length());//获取焦点的时候默认选中所有的内容
                                    //text.setText("");
                                    mCurrentTouchedIndex = position;
                                    //mInputManager.showSoftInput(v, InputMethodManager.SHOW_FORCED);//获取焦点的时候强制显示软键盘
                                } else {
                                    column.setValue(o, text.getText().toString());
                                    mCurrentTouchedIndex = position;
                                    //mInputManager.hideSoftInputFromWindow(v.getWindowToken(), 0); //失去焦点的时候强制隐藏键盘
                                }
                            }
                        });
                        if (mCurrentTouchedIndex == position) {
                            text.requestFocus();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return convertView;
        }


    }

    /**
     * 滑动监听，{@link #mListView}在滑动的时候把焦点清楚，解决
     * parameter must be a descendant of this view
     */
    protected class MyScrollListener implements AbsListView.OnScrollListener {

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            // do nothing
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (SCROLL_STATE_TOUCH_SCROLL == scrollState) {
/*                View currentFocus = mActivity.getCurrentFocus();
                if (currentFocus != null) {
                    currentFocus.clearFocus();
                }*/
                view.clearFocus();
                mInputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);//滚动的时候隐藏软键盘
            }
        }

    }
}
