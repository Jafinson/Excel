package com.longjoe.ui.grid.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.longjoe.ui.R;
import com.longjoe.ui.grid.bean.Column;
import com.longjoe.ui.grid.bean.LjBean;
import com.longjoe.ui.grid.fragment.SearchableFragment;
import com.longjoe.ui.grid.util.ListUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Excel extends LinearLayout {
    //region 自定义表格属性
    /**
     * 筛选器高度
     */
    private int filterHeight = 0;
    /**
     * 每一列的宽度
     */
    private int each = 200;
    /**
     * 是否支持筛选
     */
    private boolean hasFilter;
    /**
     * 线的颜色
     */
    private int lineColor;
    /**
     * 标题颜色
     */
    private int headerColor;
    /**
     * 奇数行颜色
     */
    private int oddRowColor;
    /**
     * 偶数行颜色
     */
    private int evenRowColor;
    /**
     * 行高度，默认50
     */
    private int itemHeight;
    /**
     * 表格字体大小
     */
    private int textSize = 30;
    /**
     * 选择模式
     */
    private int mChoiceMode;
    /**
     * Normal list that does not indicate choices
     */
    public static final int CHOICE_MODE_NONE = 0;

    /**
     * The list allows up to one choice
     */
    public static final int CHOICE_MODE_SINGLE = 1;

    /**
     * The list allows multiple choices
     */
    public static final int CHOICE_MODE_MULTIPLE = 2;
    //endregion

    private Context mCtx;
    private RecyclerView mTable;
    private HorizontalListView mFilterView;
    private LinearLayout mHeaderView;
    private MyAdapter mAdapter;
    private FilterAdapter mFilterAdapter;
    private List<Column> mColumns;
    private List mData;
    private Map<String, List> mConditions;
    private int width;//屏幕宽度
    private boolean[] checked;//多选的时候根据这个判断哪些被选
    private int checkPosition;//单选的时候根据这个判断哪些被选
    private Class clz;
    //region 长按监听
    private OnItemLongClickListener mLongClickListener;

    public void setmLongClickListener(OnItemLongClickListener mLongClickListener) {
        this.mLongClickListener = mLongClickListener;
    }

    interface OnItemLongClickListener {
        void onLongClick(LjBean ljBean);
    }
    //endregion

    public Excel(Context context) {
        this(context, null);
    }

    public Excel(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Excel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mCtx = context;
        View root = LayoutInflater.from(context).inflate(R.layout.excel, this, true);
        mTable = root.findViewById(R.id.body);
        mFilterView = root.findViewById(R.id.filter);
        mHeaderView = root.findViewById(R.id.header);

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.Excel);
        if (attributes != null) {
            filterHeight = attributes.getDimensionPixelSize(R.styleable.Excel_filterHeight, 80);
            mFilterView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, filterHeight));
            hasFilter = attributes.getBoolean(R.styleable.Excel_hasFilter, true);
            textSize = attributes.getDimensionPixelSize(R.styleable.Excel_android_textSize, 35);
            itemHeight = attributes.getDimensionPixelSize(R.styleable.Excel_itemHeight, 80);
            lineColor = attributes.getColor(R.styleable.Excel_lineColor, getResources().getColor(R.color.table_border));
            headerColor = attributes.getColor(R.styleable.Excel_headerColor, getResources().getColor(R.color.table_header_background));
            evenRowColor = attributes.getColor(R.styleable.Excel_evenRowColor, getResources().getColor(R.color.table_even_row_background));
            oddRowColor = attributes.getColor(R.styleable.Excel_oddRowColor, getResources().getColor(R.color.table_odd_row_background));
            headerColor = attributes.getColor(R.styleable.Excel_headerColor, getResources().getColor(R.color.table_header_background));
            mChoiceMode = attributes.getInt(R.styleable.Excel_choiceMode, 0);
            attributes.recycle();
        }
    }


    public void setEach(int each) {
        this.each = each;
        if (mColumns != null) {
            setTableWidth(mColumns.size() * each > width ? mColumns.size() * each : width);
        }
    }

    //解决context转换activity的问题
    private Activity scanForActivity(Context cont) {
        if (cont == null)
            return null;
        else if (cont instanceof Activity)
            return (Activity) cont;
        else if (cont instanceof ContextWrapper)
            return scanForActivity(((ContextWrapper) cont).getBaseContext());

        return null;
    }

    /**
     * 列改变后刷新界面
     *
     * @param columns
     */
    public void refreshColumns(List<Column> columns) {
        mColumns = Column.getShowColumns(columns);
        if (mData != null) {
            mTable.setAdapter(new MyAdapter());
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        this.setMeasuredDimension(width, parentHeight);
        setTableWidth(width > mColumns.size() * each ? width : mColumns.size() * each);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void show(List data) throws Exception {
        this.mData = data;
        if (mAdapter == null) {
            mAdapter = new MyAdapter();
            mTable.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    /***
     *必须放在调用show()之前设置表格显示的布局
     * @param columns 表格布局
     * @param clz 表格显示的javabean对象
     * @param choiceMode 多选/单选
     */
    public void initColumns(List<Column> columns, Class clz, int choiceMode) {
        this.mChoiceMode = choiceMode;
        this.clz=clz;
        mColumns = Column.getShowColumns(columns);
        try {
            Field field = Column.class.getDeclaredField("f");
            field.setAccessible(true);
            for (Column column : columns) {
                field.set(column, clz.getDeclaredField(column.getField()));
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        initHeader(mColumns);
    }

    public DisplayMetrics getWindowParameters() {
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager wm = (WindowManager) mCtx.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metric);
        return metric;
    }

    public void checkAll() {
        if (mData == null) {
            return;
        }
        for (int i = 0; i < checked.length; i++) {
            checked[i] = true;
        }
        mAdapter.notifyDataSetChanged();
    }

    public void uncheckAll() {
        if (mData == null) {
            return;
        }
        for (int i = 0; i < checked.length; i++) {
            checked[i] = false;
        }
        mAdapter.notifyDataSetChanged();
    }

    public void inverse() {
        if (mData == null) {
            return;
        }
        for (int i = 0; i < checked.length; i++) {
            checked[i] = !checked[i];
        }
        mAdapter.notifyDataSetChanged();
    }

    private void showFilterDialog(final Column column) {
        try {
            Field f = Column.class.getDeclaredField("f");
            Field field = (Field) f.get(column);
            final List filters = ListUtil.getNoRepeatValue(mData, field);
            dialog = getDialog(filters, column.getName());
            dialog.setListener(new SearchableFragment.OnPositiveListener() {
                @Override
                public void callback(List data) {
                    if (data.size() != 0) {
                        mConditions.put(column.getField(), data);
                        if (mFilterAdapter == null) {
                            mFilterAdapter = new FilterAdapter();
                            if (mConditions.size() > 0) {
                                mFilterView.setVisibility(View.VISIBLE);
                            }
                            mFilterView.setAdapter(mFilterAdapter);
                        } else {
                            mFilterAdapter.notifyDataSetChanged();
                        }
//                        mData = ListUtil.filter(mData, mReflector, mConditions);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            });
            dialog.show(scanForActivity(mCtx).getFragmentManager(), "filter");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private SearchableFragment dialog;

    public SearchableFragment getDialog(List filters, String name) {
        if (dialog != null) {
            dialog.dismiss();
        }
        dialog = new SearchableFragment(filters, name);
        return dialog;
    }

    private class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        int mCurrentTouchedIndex = -1;//记录editTetxt焦点所在的position，得到焦点的时候值为position ，失去焦点的时候置为-1

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LinearLayout root = getItemParent();
            switch (mChoiceMode) {
                case CHOICE_MODE_MULTIPLE:
                    root.addView(getCheck());
                case CHOICE_MODE_SINGLE:
                    root.addView(getRadio());
            }
            for (int i = 0; i < mColumns.size(); i++) {
                TextView text = getText(mColumns.get(i), false);
                root.addView(text);
            }
            return new RecyclerView.ViewHolder(root) {
            };
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            final int copyPosition = position;
            View vCheck = ((ViewGroup) holder.itemView).getChildAt(0);
            if (mChoiceMode == CHOICE_MODE_MULTIPLE) {//多选
                ((CheckBox) vCheck).setChecked(checked[copyPosition]);//根据值设置view
                //根据操作设置值
                ((CheckBox) vCheck).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        checked[copyPosition] = isChecked;
                    }
                });
            } else if (mChoiceMode == CHOICE_MODE_SINGLE) {//单选
                if (copyPosition == checkPosition) {
                    ((RadioButton) vCheck).setChecked(true);
                } else {
                    ((RadioButton) vCheck).setChecked(false);
                }
                ((RadioButton) vCheck).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked && checkPosition != copyPosition) {
                            ((RadioButton) ((ViewGroup) mTable.getChildAt(checkPosition)).getChildAt(0)).setChecked(false);
                        }
                    }
                });
            }
            for (int i = 0; i < mColumns.size(); i++) {
                final TextView text = (TextView) ((ViewGroup) holder.itemView).getChildAt(mChoiceMode == CHOICE_MODE_NONE ? i : i - 1);
                final Object o = mData.get(copyPosition);
                final Column column = mColumns.get(i);
                switch (getItemType(column)) {
                    case 1:
                        if (mCurrentTouchedIndex == copyPosition) {
                            text.requestFocus();
                        }
                        text.setOnTouchListener(new OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                    mCurrentTouchedIndex = copyPosition;
                                }
                                return false;
                            }
                        });
                        text.setOnFocusChangeListener(new OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                if (!hasFocus) {
                                    try {
                                        column.setValue(o, ((TextView) v).getText().toString());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                        try {
                            text.setText(column.getValue(o).toString());
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 2:
                        try {
                            ((CheckBox) text).setChecked((Boolean) column.getValue(o));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                        ((CheckBox) text).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                try {
                                    column.setValue(o, isChecked);
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        break;
                    default:
                        try {
                            text.setText(column.getValue(o).toString());
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                        break;
                }
                text.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, column.getWidth() == 0 ? 1.0f : column.getWidth()));
                setColor((ViewGroup) holder.itemView, copyPosition);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        private void setColor(ViewGroup convertView, int position) {
            for (int i = 0; i < convertView.getChildCount(); i++) {
                int color = position % 2 == 0 ? evenRowColor : oddRowColor;
                convertView.getChildAt(i).setBackgroundColor(color);
            }
        }
    }

    private class FilterAdapter extends BaseAdapter {
        private List<String> key;

        private FilterAdapter() {
            key = new ArrayList<>();
            key.addAll(mConditions.keySet());
        }

        @Override
        public int getCount() {
            return mConditions.size();
        }

        @Override
        public Object getItem(int position) {
            int j = 0;//筛选条件所在的标题位置
            for (int i = 0; i < mColumns.size(); i++) {
                if (mColumns.get(i).getField() == key.get(position)) {
                    j = i;
                    break;
                }
            }
            return mColumns.get(j).getName() + "=" + "\n" + mConditions.get(key.get(position).toString());
        }

        @Override
        public long getItemId(int position) {
            return position;
        }



        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            @SuppressLint("ViewHolder") final TextView text = (TextView) View.inflate(mCtx, R.layout.item_filter_text, null);
            text.setText((String) getItem(position));
           text.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        Drawable drawable = text.getCompoundDrawables()[2];
                        if (drawable == null) {
                            return false;
                        } else if (event.getX() > text.getWidth() - drawable.getBounds().width()) {
                            mConditions.remove(key.get(position));
                            FilterAdapter.this.notifyDataSetChanged();
                            mData = ListUtil.filter(mData, clz, mConditions);
                            mAdapter.notifyDataSetChanged();
                        }
                        return true;
                    }
                    return false;
                }
            });
            return text;
        }

        @Override
        public void notifyDataSetChanged() {
            key.clear();
            key.addAll(mConditions.keySet());
            if (mConditions.size() > 0) {
                mFilterView.setVisibility(View.VISIBLE);
            } else {
                mFilterView.setVisibility(View.GONE);
            }
            super.notifyDataSetChanged();
        }
    }

    /**
     * 根据column的信息判断应该用textview edittext checkbox
     *
     * @param column 信息
     * @return 0-textview 1-edittext 2-checkbox
     */
    private int getItemType(Column column) {
        if (column.editable) {
            return 1;
        }
        String name;
        try {
            name = Column.class.getDeclaredField("f").get(column).getClass().getName();
            if ("Boolean".equals(name)) {
                return 2;
            }
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return 0;
    }

    //region 样式设置
    private LinearLayout getItemParent() {
        LinearLayout rslt = new LinearLayout(mCtx);
        rslt.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, itemHeight));
        rslt.setOrientation(HORIZONTAL);
        rslt.setBackgroundColor(lineColor);
        return rslt;
    }

    private TextView getText(Column column, boolean isHeader) {
        TextView text;
        if (isHeader) {
            text = new TextView(mCtx);
        } else {
            switch (getItemType(column)) {
                case 1:
                    text = new EditText(mCtx);
                    text.setTextColor(Color.parseColor("#0000FF"));
                    text.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    text.setPadding(0, 0, 0, 0);
                    break;
                case 2:
                    text = new CheckBox(mCtx);
                    text.setTextColor(Color.BLACK);
                    break;
                default:
                    text = new TextView(mCtx);
                    text.setTextColor(Color.BLACK);
                    break;
            }
        }
        LayoutParams lp = new LayoutParams(0, LayoutParams.MATCH_PARENT, column.getWidth());
        lp.setMargins(1, 1, 1, 1);
        lp.gravity = Gravity.CENTER;
        text.setGravity(Gravity.CENTER);
        text.setLayoutParams(lp);

        text.setBackgroundColor(Color.WHITE);
        if (textSize != 0) {
            text.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        }
        return text;
    }

    private RadioButton getRadio() {
        RadioButton radio = new RadioButton(mCtx);
        LayoutParams lp = new LayoutParams(0, LayoutParams.MATCH_PARENT, 0.5f);
        lp.setMargins(1, 1, 1, 1);
        lp.gravity = Gravity.CENTER;
        radio.setBackgroundColor(Color.WHITE);
        return radio;
    }

    private CheckBox getCheck() {
        CheckBox check = new CheckBox(mCtx);
        LayoutParams lp = new LayoutParams(0, LayoutParams.MATCH_PARENT, 0.5f);
        lp.setMargins(1, 1, 1, 1);
        lp.gravity = Gravity.CENTER;
        check.setBackgroundColor(Color.WHITE);
        return check;
    }

    private void initHeader(List<Column> columns) {
        setTableWidth(columns.size() * each > width ? columns.size() * each : width);
        if (mHeaderView.getChildCount() > 0) {
            mHeaderView.removeAllViews();
        }
        for (final Column column : columns) {
            final TextView text = getText(column, true);
            text.setText(column.getName());
            text.setBackgroundColor(headerColor);
            text.setCompoundDrawablesWithIntrinsicBounds(null, null, mCtx.getResources().getDrawable(R.drawable.arrow_drop_down), null);
            text.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mAdapter == null) {
                        return;
                    }
                    // mAdapter.showFilterPopup(text, column);
                    showFilterDialog(column);
                }
            });
            mHeaderView.addView(text);
        }
    }

    private void setTableWidth(int width) {
        ViewGroup.LayoutParams lp = mTable.getLayoutParams();
        lp.width = width;
        mTable.setLayoutParams(lp);
    }
    //endregion

    private void clearFocus(ViewGroup view) {
        for (int i = 0; i < view.getChildCount(); i++) {
            View child = view.getChildAt(i);
            if (child instanceof ViewGroup) {
                clearFocus(((ViewGroup) child));
            } else {
                child.clearFocus();
            }
        }
    }
}
