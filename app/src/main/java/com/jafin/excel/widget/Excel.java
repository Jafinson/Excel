package com.jafin.excel.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Vibrator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.jafin.excel.R;
import com.jafin.excel.bean.Column;
import com.jafin.excel.fragment.SearchableDialog;
import com.jafin.excel.util.ListUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Excel<T> extends LinearLayout {
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
     * 是否支持拖拽
     */
    private boolean draggable;
    private LinearLayoutManager mLinearLayoutManager;

    public void setDraggable(boolean draggable) {
        this.draggable = draggable;
        if (draggable) {
            helper = new ItemTouchHelper(new MyCallBack());
            helper.attachToRecyclerView(mTable);
        }
    }

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

    private Activity mCtx;
    private RecyclerView mTable;
    private HorizontalListView mFilterView;
    private LinearLayout mHeaderView;
    private MyAdapter mAdapter;
    private FilterAdapter mFilterAdapter;
    private List<Column> mColumns;
    private List<T> mData;
    private Map<String, Set> mConditions;
    private int width;//屏幕宽度
    private boolean[] checked;//多选的时候根据这个判断哪些被选
    private int checkPosition;//单选的时候根据这个判断哪些被选
    private Class<T> mClz;
    private ItemTouchHelper helper;
    private Map<String, Integer> fieldCheck;
    private boolean move;

    public Excel(Context context) {
        this(context, null);
    }

    public Excel(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Excel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mCtx = scanForActivity(context);
        View root = LayoutInflater.from(context).inflate(R.layout.excel, this, true);
        mTable = (RecyclerView) root.findViewById(R.id.body);
        mLinearLayoutManager = new LinearLayoutManager(context);
        mTable.setLayoutManager(mLinearLayoutManager);

        mFilterView = (HorizontalListView) root.findViewById(R.id.filter);
        mHeaderView = (LinearLayout) root.findViewById(R.id.header);
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.Excel);
        if (attributes != null) {
            filterHeight = attributes.getDimensionPixelSize(R.styleable.Excel_filterHeight, 80);
            mFilterView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, filterHeight));
            hasFilter = attributes.getBoolean(R.styleable.Excel_hasFilter, true);
            draggable = attributes.getBoolean(R.styleable.Excel_draggable, false);
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
        if (draggable) {
            helper = new ItemTouchHelper(new MyCallBack());
            helper.attachToRecyclerView(mTable);
        }

    }


    public void setEach(int each) {
        this.each = each;
        if (mColumns != null) {
            setTableWidth(mColumns.size() * each > width ? mColumns.size() * each : width);
        }
    }

    /**
     * 列改变后刷新界面
     *
     * @param columns 刷新后的列布局
     */
    public void refreshColumns(List<Column> columns) {
        mColumns = Column.getShowColumns(columns);
        initHeader(mColumns);
        if (mData != null) {
            mAdapter = new MyAdapter(mAdapter.data);
            mTable.setAdapter(mAdapter);
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

    public void show(List<T> data) throws Exception {
        this.mData = data;
        this.checked = new boolean[data.size()];
        if (mAdapter == null) {
            mAdapter = new MyAdapter(mData);
            mTable.setAdapter(mAdapter);
        } else {
            mAdapter.show(mData);
        }
    }

    /***
     *必须放在调用show()之前设置表格显示的布局
     * @param columns 表格布局
     * @param clz 表格显示的javabean对象
     * @param choiceMode 多选/单选
     */
    public void initColumns(List<Column> columns, Class<T> clz, int choiceMode) {
        this.mChoiceMode = choiceMode;
        this.mClz = clz;
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
        if (mChoiceMode != CHOICE_MODE_MULTIPLE) {
            Log.w("Excel", "非多选模式选择无效");
            return;
        }
        if (mData == null) {
            return;
        }
        for (int i = 0; i < checked.length; i++) {
            checked[i] = true;
        }
        mAdapter.show(mData);
    }

    public void clear() {
        if (mChoiceMode != CHOICE_MODE_MULTIPLE) {
            Log.w("Excel", "非多选模式选择无效");
            return;
        }
        if (mData == null) {
            return;
        }
        for (int i = 0; i < checked.length; i++) {
            checked[i] = false;
        }
        mAdapter.show(mData);
    }

    public void inverse() {
        if (mChoiceMode != CHOICE_MODE_MULTIPLE) {
            Log.w("Excel", "非多选模式选择无效");
            return;
        }
        if (mData == null) {
            return;
        }
        for (int i = 0; i < checked.length; i++) {
            checked[i] = !checked[i];
        }
        mAdapter.show(mData);
    }

    public List getCheckedData() {
        List<T> result = new ArrayList<>();
        for (int i = 0; i < checked.length; i++) {
            if (checked[i]) {
                result.add(mData.get(i));
            }
        }
        return result;
    }

    public int getCheckedPosition() {return checkPosition;}

    private void showFilterDialog(final Column column) {
        try {
            Field f = Column.class.getDeclaredField("f");
            f.setAccessible(true);
            Field field = (Field) f.get(column);
            final List filters = ListUtil.getNoRepeatValue(mAdapter.data, field);
            dialog = getDialog(filters, column.getName());
            dialog.setListener(data -> {
                if (data.size() != 0) {
                    if (mConditions == null) {
                        mConditions = new HashMap<>();
                    }
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
                    mAdapter.show(ListUtil.filter(mAdapter.data, mClz, mConditions));
                }
            });
            dialog.show(scanForActivity(mCtx).getFragmentManager(), "filter");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void moveToPosition(int n) {
        //先从RecyclerView的LayoutManager中获取第一项和最后一项的Position
        int firstItem = mLinearLayoutManager.findFirstVisibleItemPosition();
        //当要置顶的项已经在屏幕上显示时
        int top = mTable.getChildAt(n - firstItem).getTop();
        mTable.scrollBy(0, top);
    }

    private SearchableDialog dialog;

    private SearchableDialog getDialog(List filters, String name) {
        if (dialog != null) {
            dialog.dismiss();
        }
        dialog = new SearchableDialog(filters, name);
        return dialog;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        View currentFocus = mCtx.getCurrentFocus();
        if (currentFocus != null) {
            currentFocus.clearFocus();
            // mTable.scrollToPosition(mAdapter.mCurrentTouchedIndex);
            mTable.smoothScrollBy(0, mAdapter.scrollY);
            //moveToPosition(mAdapter.mCurrentTouchedIndex);
        }
    }

    /**
     * 根据column的信息判断应该用textview edittext checkbox
     *
     * @param column 信息
     * @return 0-textview 1-edittext 2-checkbox 3-ratio
     */
    private int getItemType(Column column) {
        /*if (column.editable) {
            return 1;
        }
        if (column.choiceMode == CHOICE_MODE_SINGLE) {
            //return 3;
        }
        String name;
        try {
            Field f = Column.class.getDeclaredField("f");
            f.setAccessible(true);
            name = ((Field) f.get(column)).getType().getName();
            if ("boolean".equals(name)) {
                return 2;
            }
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return 0;*/
        return column.getItemType();
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
                    break;
                case 2:
                    text = new CheckBox(mCtx);
                    text.setTextColor(Color.BLACK);
                    break;
                case 3:
                    text = new RadioButton(mCtx);
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
        //lp.setMargins(1, 1, 1, 1);
        lp.gravity = Gravity.CENTER;
        radio.setBackgroundColor(Color.WHITE);
        radio.setLayoutParams(lp);
        return radio;
    }

    private CheckBox getCheck() {
        CheckBox check = new CheckBox(mCtx);
        LayoutParams lp = new LayoutParams(0, LayoutParams.MATCH_PARENT, 0.5f);
        // lp.setMargins(1, 1, 1, 1);
        lp.gravity = Gravity.CENTER;
        check.setBackgroundColor(Color.WHITE);
        check.setLayoutParams(lp);
        return check;
    }

    private void initHeader(List<Column> columns) {
        setTableWidth(columns.size() * each > width ? columns.size() * each : width);
        if (mHeaderView.getChildCount() > 0) {
            mHeaderView.removeAllViews();
        }
        if (mChoiceMode != CHOICE_MODE_NONE) {
            TextView text = new TextView(getContext());
            text.setTextColor(Color.BLACK);
            LayoutParams lp = new LayoutParams(0, LayoutParams.MATCH_PARENT, 0.5f);
            // lp.setMargins(1, 1, 1, 1);
            lp.gravity = Gravity.CENTER;
            text.setGravity(Gravity.CENTER);
            text.setLayoutParams(lp);
            text.setText("选");
            text.setBackgroundColor(headerColor);
            if (textSize != 0) {
                text.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            }
            mHeaderView.addView(text);
        }
        for (final Column column : columns) {
            final TextView text = getText(column, true);
            text.setText(column.getName());
            text.setBackgroundColor(headerColor);
            text.setCompoundDrawablesWithIntrinsicBounds(null, null, mCtx.getResources().getDrawable(R.drawable.arrow_drop_down), null);
            text.setOnClickListener(v -> {
                if (mData == null) {
                    return;
                }
                showFilterDialog(column);
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

    private class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private int mCurrentTouchedIndex = -1;//记录editTetxt焦点所在的position，得到焦点的时候值为position ，失去焦点的时候置为-1
        private RadioButton rb_checked;
        private List<T> data;
        private int scrollY;
        //private EditText focus;

        private MyAdapter(List<T> data) {
            this.data = new ArrayList<>();
            this.data.addAll(data);
        }

        private void show(List<T> data) {
            if (this.data == null) {
                this.data = new ArrayList<>();
                this.data.addAll(data);
            } else {
                this.data.clear();
                this.data.addAll(data);
            }
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LinearLayout root = getItemParent();
            switch (mChoiceMode) {
                case CHOICE_MODE_MULTIPLE:
                    root.addView(getCheck());
                    break;
                case CHOICE_MODE_SINGLE:
                    root.addView(getRadio());
                    break;
            }
            for (int i = 0; i < mColumns.size(); i++) {
                TextView text = getText(mColumns.get(i), false);
                root.addView(text);
            }
            RecyclerView.ViewHolder holder = new RecyclerView.ViewHolder(root) {
            };
            if (draggable) {
                holder.itemView.setOnLongClickListener(v -> {
                    helper.startDrag(holder);
                    //获取系统震动服务
                    Vibrator vib = (Vibrator) mCtx.getSystemService(Service.VIBRATOR_SERVICE);
                    //震动70毫秒
                    vib.vibrate(70);
                    return true;
                });
            }
            return holder;
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            final int copyPosition = position;
            final View vCheck = ((ViewGroup) holder.itemView).getChildAt(0);
            if (mChoiceMode == CHOICE_MODE_MULTIPLE && vCheck instanceof CheckBox) {//多选
                //根据操作设置值
                ((CheckBox) vCheck).setOnCheckedChangeListener((buttonView, isChecked) -> checked[copyPosition] = isChecked);
                ((CheckBox) vCheck).setChecked(checked[copyPosition]);//根据值设置view
            } else if (mChoiceMode == CHOICE_MODE_SINGLE && vCheck instanceof RadioButton) {//单选
                ((RadioButton) vCheck).setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked && checkPosition != copyPosition) {
                        if (rb_checked == null) {
                            ((RadioButton) ((ViewGroup) mTable.getChildAt(0)).getChildAt(0)).setChecked(false);
                        } else {
                            rb_checked.setChecked(false);
                        }
                        checkPosition = copyPosition;
                        rb_checked = ((RadioButton) vCheck);
                    }
                });
                if (copyPosition == checkPosition) {
                    ((RadioButton) vCheck).setChecked(true);
                } else {
                    ((RadioButton) vCheck).setChecked(false);
                }
            }
            for (int i = 0; i < mColumns.size(); i++) {
                final TextView text = (TextView) ((ViewGroup) holder.itemView).getChildAt(mChoiceMode == CHOICE_MODE_NONE ? i : i + 1);
                final Object o = data.get(copyPosition);
                final Column column = mColumns.get(i);
                switch (getItemType(column)) {
                    case 1:
                        if (mCurrentTouchedIndex == copyPosition) {
                            text.requestFocus();
                        }
                        try {
                            text.setText(column.getValue(o).toString());
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                        text.setOnFocusChangeListener((v, hasFocus) -> {
                            if (!hasFocus) {
                                try {
                                    column.setValue(o, text.getText().toString());
                                } catch (Exception e) {
                                    try {
                                        text.setText(column.getValue(o).toString());
                                    } catch (Exception e1) {
                                        e1.printStackTrace();
                                    }
                                }
                            }
                        });
                        text.setOnTouchListener((v, event) -> {
                            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                mCurrentTouchedIndex = copyPosition;
                                scrollY = holder.itemView.getTop();
                            }
                            return false;
                        });
                        break;
                    case 2:
                        ((CheckBox) text).setOnCheckedChangeListener((buttonView, isChecked) -> {
                            try {
                                column.setValue(o, isChecked);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        });
                        try {
                            ((CheckBox) text).setChecked((boolean) column.getValue(o));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 3:
                        //TODO 属性单选逻辑
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
            return data.size();
        }

        private void setColor(ViewGroup convertView, int position) {
            int color = position % 2 == 0 ? evenRowColor : oddRowColor;
            convertView.setTag(color);
            for (int i = 0; i < convertView.getChildCount(); i++) {
                convertView.getChildAt(i).setBackgroundColor(color);
            }
        }

        private void swipColor(ViewGroup v1, ViewGroup v2) {
            int color2 = (int) v2.getTag();
            int color1 = (int) v1.getTag();
            for (int i = 0; i < v1.getChildCount(); i++) {
                v1.getChildAt(i).setBackgroundColor(color2);
            }
            for (int i = 0; i < v2.getChildCount(); i++) {
                v2.getChildAt(i).setBackgroundColor(color1);
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
            text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mConditions.remove(key.get(position));
                    FilterAdapter.this.notifyDataSetChanged();
                    mAdapter.show(ListUtil.filter(mData, mClz, mConditions));
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

    class MyCallBack extends ItemTouchHelper.Callback {

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                final int swipeFlags = 0;
                return makeMovementFlags(dragFlags, swipeFlags);
            } else {
                final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                final int swipeFlags = 0;
                return makeMovementFlags(dragFlags, swipeFlags);
            }
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            //得到当拖拽的viewHolder的Position
            int fromPosition = viewHolder.getAdapterPosition();
            //拿到当前拖拽到的item的viewHolder
            int toPosition = target.getAdapterPosition();
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(mAdapter.data, i, i + 1);
                    int i1 = mData.indexOf(mAdapter.data.get(i));
                    int i2 = mData.indexOf(mAdapter.data.get(i + 1));
                    Collections.swap(mData, i1, i2);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(mAdapter.data, i, i - 1);
                    int i1 = mData.indexOf(mAdapter.data.get(i));
                    int i2 = mData.indexOf(mAdapter.data.get(i - 1));
                    Collections.swap(mData, i1, i2);
                }
            }
            //mAdapter.swipColor((ViewGroup) mTable.getChildAt(fromPosition), (ViewGroup) mTable.getChildAt(toPosition));
            mAdapter.notifyItemMoved(fromPosition, toPosition);
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

        }

        /**
         * 长按item的时候高亮
         *
         * @param viewHolder
         * @param actionState
         */
        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                viewHolder.itemView.setBackgroundColor(Color.LTGRAY);
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        /**
         * 手指松开的时候还原
         *
         * @param recyclerView
         * @param viewHolder
         */
        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            viewHolder.itemView.setBackgroundColor(0);
        }
    }
}
