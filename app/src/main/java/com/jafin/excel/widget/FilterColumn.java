package com.jafin.excel.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jafin.excel.R;
import com.jafin.excel.bean.FilterCondition;

import java.util.List;

/**
 * Created by 何锦发 on 2017/6/30.
 * 筛选条件的控件，两层结构，单行多列，列为列表，列为筛选列，行为筛选值
 */
public class FilterColumn extends RecyclerView {
    private List<FilterCondition> conditions;
    private List<String> mData;
    private MyAdapter mAdapter;
    private Context mCtx;

    public FilterColumn(Context context) {
        this(context, null);
    }

    public FilterColumn(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FilterColumn(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mCtx=context;
    }


    public void show(List<String> data) {
        this.mData = data;
        if (mAdapter == null) {
            mAdapter = new MyAdapter();
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(mCtx, LinearLayoutManager.HORIZONTAL, false);
            setLayoutManager(mLayoutManager);
            setAdapter(mAdapter);
            ItemTouchHelper helper = new ItemTouchHelper(new MyCallBack());
            helper.attachToRecyclerView(this);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    private class MyCallBack extends ItemTouchHelper.Callback {
        /**
         * 设置滑动类型标记
         *
         * @param recyclerView
         * @param viewHolder
         * @return
         *          返回一个整数类型的标识，用于判断Item那种移动行为是允许的
         */
        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            return makeMovementFlags(0,ItemTouchHelper.UP | ItemTouchHelper.DOWN);
        }

        /**
         * Item是否支持长按拖动
         *
         * @return
         *          true  支持长按操作
         *          false 不支持长按操作
         */
        @Override
        public boolean isLongPressDragEnabled() {
            return false;
        }

        /**
         * Item是否支持滑动
         *
         * @return
         *          true  支持滑动操作
         *          false 不支持滑动操作
         */
        @Override
        public boolean isItemViewSwipeEnabled() {
            return true;
        }

        /**
         * 拖拽切换Item的回调
         *
         * @param recyclerView
         * @param viewHolder
         * @param target
         * @return
         *          如果Item切换了位置，返回true；反之，返回false
         */
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return true;
        }

        /**
         * 滑动删除Item
         *
         * @param viewHolder
         * @param direction
         *           Item滑动的方向
         */
        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            mAdapter.delete(viewHolder.getAdapterPosition());
        }

        /**
         * Item被选中时候回调
         *
         * @param viewHolder
         * @param actionState
         *          当前Item的状态
         *          ItemTouchHelper.ACTION_STATE_IDLE   闲置状态
         *          ItemTouchHelper.ACTION_STATE_SWIPE  滑动中状态
         *          ItemTouchHelper#ACTION_STATE_DRAG   拖拽中状态
         */
        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            //  item被选中的操作
            if(actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                viewHolder.itemView.setBackgroundResource(R.color.colorPrimary);
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        /**
         * 移动过程中绘制Item
         *
         * @param c
         * @param recyclerView
         * @param viewHolder
         * @param dX
         *          X轴移动的距离
         * @param dY
         *          Y轴移动的距离
         * @param actionState
         *          当前Item的状态
         * @param isCurrentlyActive
         *          如果当前被用户操作为true，反之为false
         */
        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView,
                                RecyclerView.ViewHolder viewHolder,
                                float dX, float dY, int actionState, boolean isCurrentlyActive) {
            float x = Math.abs(dX) + 0.5f;
            float width = viewHolder.itemView.getWidth();
            float alpha = 1f - x / width;
            viewHolder.itemView.setAlpha(alpha);
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState,
                    isCurrentlyActive);
        }

        /**
         * 移动过程中绘制Item
         *
         * @param c
         * @param recyclerView
         * @param viewHolder
         * @param dX
         *          X轴移动的距离
         * @param dY
         *          Y轴移动的距离
         * @param actionState
         *          当前Item的状态
         * @param isCurrentlyActive
         *          如果当前被用户操作为true，反之为false
         */
        @Override
        public void onChildDrawOver(Canvas c, RecyclerView recyclerView,
                                    RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState,
                                    boolean isCurrentlyActive) {
            super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState,
                    isCurrentlyActive);
        }

        /**
         * 用户操作完毕或者动画完毕后会被调用
         *
         * @param recyclerView
         * @param viewHolder
         */
        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            // 操作完毕后恢复颜色
            viewHolder.itemView.setBackgroundResource(R.color.colorAccent);
            viewHolder.itemView.setAlpha(1.0f);
            super.clearView(recyclerView, viewHolder);
        }


    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView tv;

            public MyViewHolder(View itemView) {
                super(itemView);
                this.tv = (TextView) itemView;
            }
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView item = new TextView(mCtx);
            item.setLayoutParams(new LinearLayout.LayoutParams(100,100));
            return new MyViewHolder(item);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.tv.setText(mData.get(position));
        }

        public void delete(int position) {
            if (position < 0 || position > getItemCount()) {
                return;
            }
            mData.remove(position);
            notifyItemRemoved(position);
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }
    }
}
