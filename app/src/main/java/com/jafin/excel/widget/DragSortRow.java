package com.jafin.excel.widget;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.graphics.Color;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jafin.excel.bean.Column;
import com.jafin.excel.util.Utils;

import java.util.Collections;
import java.util.List;

/**
 * Created by 何锦发 on 2017/7/19.
 * 行可以拖拽排列的表格
 */
public class DragSortRow extends RecyclerView {
    private List mData;
    private MyAdapter myAdapter;
    private Activity mActivity;
    private ItemTouchHelper mHelper;
    private List<Column> mColumns;
    /**
     * 奇数行颜色
     */
    private int oddRowColor;
    /**
     * 偶数行颜色
     */
    private int evenRowColor;
    /**
     * 表格字体大小
     */
    private int textSize;
    /**
     * 标题颜色
     */
    private int headerColor;

    public DragSortRow(Context context) {
        this(context, null);
    }

    public DragSortRow(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragSortRow(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mActivity = Utils.scanForActivity(context);
    }


    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = new MyViewHolder(getItemParent());
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            try {
                setColor(holder.lv, position);
                holder.lv.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        mHelper.startDrag(holder);
                        //获取系统震动服务
                        Vibrator vib = (Vibrator) mActivity.getSystemService(Service.VIBRATOR_SERVICE);
                        //震动70毫秒
                        vib.vibrate(70);
                        return true;
                    }
                });
                holder.tv_field.setText(mColumns.get(1).getValue(mData.get(position)).toString());
                holder.tv_name.setText(mColumns.get(0).getValue(mData.get(position)).toString());
                holder.tv_width.setText(mColumns.get(2).getValue(mData.get(position)).toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private LinearLayout getItemParent() {
            LinearLayout rslt = new LinearLayout(mActivity);
            rslt.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, 60));
            for (Column column : mColumns) {
                rslt.addView(getText(column, false));
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
                        text.setTextColor(Color.parseColor("#0000FF"));
                        text.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
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
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, column.getWidth());
            lp.setMargins(1, 1, 1, 1);
            lp.gravity = Gravity.CENTER;
            text.setGravity(Gravity.CENTER);
            text.setLayoutParams(lp);
            text.setTextSize(18);
            text.setBackgroundColor(Color.WHITE);
            return text;
        }

        private void setColor(ViewGroup convertView, int position) {
            for (int i = 0; i < convertView.getChildCount(); i++) {
                int color = position % 2 == 0 ? evenRowColor : oddRowColor;
                convertView.getChildAt(i).setBackgroundColor(color);
            }
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView tv_name;
            TextView tv_field;
            TextView tv_width;
            LinearLayout lv;

            public MyViewHolder(LinearLayout view) {
                super(view);
                this.lv = view;
                tv_name = (TextView) view.getChildAt(0);
                tv_field = (TextView) view.getChildAt(1);
                tv_width = (TextView) view.getChildAt(2);
            }
        }
    }

    class MyCallBack extends ItemTouchHelper.Callback {

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN |
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
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
                    Collections.swap(mData, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(mData, i, i - 1);
                }
            }
            myAdapter.notifyItemMoved(fromPosition, toPosition);
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
