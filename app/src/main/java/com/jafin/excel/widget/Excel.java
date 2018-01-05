package com.jafin.excel.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jafin.excel.R;
import com.jafin.excel.bean.Column;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by 何锦发 on 2017/5/10.
 * 表格显示控件
 */
@SuppressWarnings("unchecked")
public class Excel extends LinearLayout {
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
    /**
     * 字体大小
     */
    private int mTextSize;
    /**
     * 选择模式
     */
    private int mChoiceMode;
    /**
     * 标题高度
     */
    private int mHeaderHeight;
    /**
     * item高度
     */
    private int mItemHeight;

    private Context mCtx;

    public Excel(Context context) {
        this(context, null);
    }

    public Excel(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Excel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mCtx = context;
        View root = LayoutInflater.from(context).inflate(R.layout.excel, this, true);
        //region 从xml获取属性
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.Excel);
        if (attributes != null) {
            mTextSize = attributes.getDimensionPixelSize(R.styleable.Excel_android_textSize, 10);
            mChoiceMode = attributes.getInt(R.styleable.Excel_choiceMode, 0);
            mHeaderHeight = attributes.getDimensionPixelSize(R.styleable.Excel_headerHeight, 80);
            mItemHeight = attributes.getDimensionPixelSize(R.styleable.Excel_itemHeight, 80);
            attributes.recycle();
        }
        // endregion
        mBody = root.findViewById(R.id.body);
        mHeader = root.findViewById(R.id.header);
    }

    private RecyclerView mBody;
    private LinearLayout mHeader;
    private List<Column> mColumns;
    private List mData;
    private boolean mCheck[];

    public void setColumns(List<Column> columns) {
        this.mColumns = columns;
        initHeader(columns.stream().filter(Column::getIsShow).sorted().collect(Collectors.toList()));
    }

    public void show(List data){
        this.mData=data;
        if (mChoiceMode==CHOICE_MODE_MULTIPLE) {
            mCheck=new boolean[data.size()];
        }
        mBody.setAdapter(new MyAdapter(mColumns.stream().filter(Column::getIsShow).collect(Collectors.toList())));
    }

    private void initHeader(List<Column> columns) {
        Object[] width = getWidthArray(columns);
        mHeader.removeAllViews();
        mHeader.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mHeaderHeight));
        if (mChoiceMode == CHOICE_MODE_MULTIPLE) {
            TextView tv_header = getTextView(0.5f, false);
            tv_header.setText("选");
            mHeader.addView(tv_header);
        }
        for (int i = 0; i < columns.size(); i++) {
            TextView tv_header = getTextView((Float) width[i], false);
            tv_header.setText(columns.get(i).getName());
            mHeader.addView(tv_header);
        }
    }

    private CheckBox getCheckBox() {
        CheckBox checkBox = new CheckBox(mCtx);
        checkBox.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 0.5f));
        return checkBox;
    }

    private TextView getTextView(float width, boolean enable) {
        TextView tv;
        if (enable) {
            tv = new EditText(mCtx);
        } else {
            tv = new TextView(mCtx);
        }
        tv.setTextSize(mTextSize);
        tv.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, width));
        return tv;
    }

    private LinearLayout getItemView(List<Column> columns) {
        LinearLayout item = new LinearLayout(mCtx);
        item.setOrientation(HORIZONTAL);
        Object[] width = getWidthArray(columns);
        mHeader.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mItemHeight));
        if (mChoiceMode == CHOICE_MODE_MULTIPLE) {
            mHeader.addView(getCheckBox());
        }
        for (int i = 0; i < columns.size(); i++) {
            TextView tv_item = getTextView((Float) width[i], columns.get(i).enable);
            mHeader.addView(tv_item);
        }
        return item;
    }

    private Object[] getWidthArray(List<Column> columns) {
        int sum = columns.parallelStream().mapToInt(Column::getWidth).sum();
        return columns.stream().map(column -> column.getWidth() / sum).toArray();
    }

    private class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<Column> columns;

        private MyAdapter(List<Column> columns) {
            this.columns = columns;
        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerView.ViewHolder(getItemView(columns)) {
            };
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ViewGroup root = (ViewGroup) holder.itemView;
            for (int i = 0; i < root.getChildCount(); i++) {
                TextView tv_item = (TextView) root.getChildAt(i);
                if (i == 0 && tv_item.getClass() == CheckBox.class && mChoiceMode == CHOICE_MODE_MULTIPLE) {
                    CheckBox checkBox = (CheckBox) tv_item;
                    checkBox.setChecked(mCheck[position]);
                    checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> mCheck[position]=isChecked);
                    return;
                }
                int p = mChoiceMode == CHOICE_MODE_MULTIPLE ? i - 1 : i;
                Column column = columns.get(p);
                Object data = mData.get(position);
                try {
                    tv_item.setText(column.getValue(data).toString());
                    tv_item.setTag(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(tv_item.getClass() == EditText.class){
                    tv_item.setOnFocusChangeListener((v, hasFocus) -> {
                        if (!hasFocus) {
                            try {
                                column.setValue(data, ((TextView) v).getText().toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

            }
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }
    }
}
