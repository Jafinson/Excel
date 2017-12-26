package com.jafin.excel.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Service;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jafin.excel.bean.Column;

import java.util.Collections;
import java.util.List;

/**
 * Created by 何锦发 on 2017/2/28.
 * 列设置对话框
 */
public class ColumnDialog extends DialogFragment implements View.OnClickListener {
    private Dialog mDialog;
    private OnPositiveClickListener positiveClickListener;
    private RecyclerView mTable;//表格显示
    private List<Column> mColumns;
    private float dialogWidth = 0.85f;
    private MyAdapter myAdapter;
    private ItemTouchHelper helper;
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
    private LinearLayout mHeader;

    public interface OnPositiveClickListener {
        void onDialogPositiveClick(DialogFragment dialog, List data);
    }

    @SuppressLint("ValidFragment")
    public ColumnDialog(List<Column> columns) {
        this.mColumns = columns;
    }

    private void initHeader() {
        for (Column column : mColumns) {
            TextView text = getText(column, true);
            text.setBackgroundColor(headerColor);
            text.setText(column.getName());
            mHeader.addView(text);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        textSize = 18;
        headerColor = getResources().getColor(R.color.table_header_background);
        evenRowColor = getResources().getColor(R.color.table_even_row_background);
        oddRowColor = getResources().getColor(R.color.table_odd_row_background);
    }

    @Override
    public void onStart() {
        super.onStart();
        // dialogWidth = ((BaseApplication) getActivity().getApplication()).getFingerWidth();
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().getWindow().setLayout((int) (dm.widthPixels * dialogWidth), LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    public ColumnDialog setDialogWidth(float dialogWidth) {
        if (dialogWidth <= 1.0f) {
            this.dialogWidth = dialogWidth;
        }
        return this;
    }

    @SuppressLint("ValidFragment")
    public ColumnDialog() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_column, null);
        return view;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mDialog = super.onCreateDialog(savedInstanceState);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return mDialog;
    }

    private LinearLayout getItemParent() {
        LinearLayout rslt = new LinearLayout(getActivity());
        rslt.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, 60));
        for (Column column : mColumns) {
            rslt.addView(getText(column, false));
        }
        return rslt;
    }

    private void setColor(ViewGroup convertView, int position) {
        for (int i = 0; i < convertView.getChildCount(); i++) {
            int color = position % 2 == 0 ? evenRowColor : oddRowColor;
            convertView.getChildAt(i).setBackgroundColor(color);
        }
    }

    private TextView getText(Column column, boolean isHeader) {
        TextView text;
        if (isHeader) {
            text = new TextView(getActivity());
        } else {
            switch (column.type) {
                case EDIT:
                    text = new EditText(getActivity());
                    text.setTextColor(Color.parseColor("#0000FF"));
                    text.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    text.setPadding(0, 0, 0, 0);
                    break;
                case CHECK:
                    text = new CheckBox(getActivity());
                    text.setTextColor(Color.BLACK);
                    break;
                default:
                    text = new TextView(getActivity());
                    text.setTextColor(Color.BLACK);

            }
        }
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT,
                column.getWidth());
        lp.setMargins(1, 1, 1, 1);
        lp.gravity = Gravity.CENTER;
        text.setGravity(Gravity.CENTER);
        text.setLayoutParams(lp);
        text.setTextSize(textSize);
        text.setBackgroundColor(Color.WHITE);
        return text;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_all) {
            //mTable.checkAll();
        } else if (v.getId() == R.id.bt_inverse) {
            //mTable.inverse();
        } else if (v.getId() == R.id.bt_clear) {
            //mTable.uncheckAll();
        } else if (v.getId() == R.id.bt_three) {
            if (positiveClickListener != null) {
                positiveClickListener.onDialogPositiveClick(this, Column.getChecked(mColumns));
            }
            mDialog.dismiss();
        }
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
                setColor(holder.lv,position);
                holder.lv.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        helper.startDrag(holder);
                        //获取系统震动服务
                        Vibrator vib = (Vibrator) getActivity().getSystemService(Service.VIBRATOR_SERVICE);
                        //震动70毫秒
                        vib.vibrate(70);
                        return true;
                    }
                });
                Column column = mColumns.get(position);
                holder.tv_field.setText(column.getField().getName());
                holder.tv_name.setText(column.getName());
                holder.tv_width.setText(column.getWidth()+"");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
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
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder
                target) {
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
