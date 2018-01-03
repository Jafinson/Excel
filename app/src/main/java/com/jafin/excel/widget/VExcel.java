package com.jafin.excel.widget;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.layout.GridLayoutHelper;
import com.jafin.excel.R;
import com.jafin.excel.bean.Column;
import com.jafin.excel.util.Utils;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by 何锦发 on 2017/12/26.
 */
public class VExcel extends LinearLayout {

    /**
     * 总宽度，默认屏幕宽度
     */
    private int screenWidth;
    /**
     * 平均每列宽度
     */
    private int averageColumnWidth = 200;


    private RecyclerView mView;
    private List<Column> mColumns;
    private Context mCtx;
    private List mData;
    private BodyAdapter mBodyAdapter;

    public VExcel(Context context) {
        this(context, null);
    }

    public VExcel(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VExcel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mCtx = Utils.scanForActivity(context);
        View root = LayoutInflater.from(context).inflate(R.layout.excel_vlayout, this, true);
        mView = (RecyclerView) root.findViewById(R.id.body);

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

    /**
     * 设计好excel要显示的列
     *
     * @param columns 列信息
     */
    public void setLayout(List<Column> columns) {
        this.mColumns = columns;
        setTableWidth(columns.size() * averageColumnWidth > screenWidth ? columns.size() * averageColumnWidth : screenWidth);

    }

    /**
     * 获取好数据，在表格设计好后就可以显示了
     *
     * @param data 要显示的数据
     * @throws Exception 必先保证在{@link #setLayout(List)} 方法之后调用
     */
    public void show(List data) throws Exception {
        if (data == null) {
            return;
        }
        if (mColumns == null || mColumns.size() == 0) {
            throw new Exception("列还没设计好或没有列可显示,请先调用initColumns方法");
        }
        if (mData == null) {
            mData = data;
        } else {
            mData.clear();
            mData.addAll(data);
        }
        if (mBodyAdapter == null) {
            mBodyAdapter = new BodyAdapter();
            final VirtualLayoutManager layoutManager = new VirtualLayoutManager(mCtx);
            mView.setLayoutManager(layoutManager);
            DelegateAdapter da = new DelegateAdapter(layoutManager, false);
            List<DelegateAdapter.Adapter> adapters = new LinkedList<>();
            adapters.add(mBodyAdapter);
            da.setAdapters(adapters);
        } else {
            mBodyAdapter.notifyDataSetChanged();
        }
    }

    private void setTableWidth(int width) {
        ViewGroup.LayoutParams lp = mView.getLayoutParams();
        lp.width = width;
        mView.setLayoutParams(lp);
    }

    @Override
    public boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate) {
        return true;//不自动滑动
    }

    private class BodyAdapter extends DelegateAdapter.Adapter<RecyclerView.ViewHolder> {
        private float[] weights;

        public BodyAdapter() {
            weights = new float[mColumns.size()];
            for (int i = 0; i < mColumns.size(); i++) {
                weights[i] = mColumns.get(i).getWidth();
            }
        }

        @Override
        public LayoutHelper onCreateLayoutHelper() {
            //设置Grid布局
            GridLayoutHelper glh = new GridLayoutHelper(mColumns.size());
            //是否自动扩展
            glh.setAutoExpand(false);
            //自定义设置某些位置的Item的占格数
            glh.setSpanSizeLookup(new GridLayoutHelper.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return 1;
                }
            });
            glh.setWeights(weights);
            return glh;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView item = new TextView(mCtx);
            return new RecyclerView.ViewHolder(item) {
            };
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Column column = mColumns.get(getColumn(position));
            ((TextView) holder.itemView).setText(column.getValue(mData.get(getRow(position))).toString());
        }


        @Override
        public int getItemCount() {
            return mData == null || mColumns == null ? 0 : mData.size() * mColumns.size();
        }

        private int getRow(int position) {
            return position / mColumns.size();
        }

        private int getColumn(int position) {
            return position % mColumns.size();
        }
    }
}
