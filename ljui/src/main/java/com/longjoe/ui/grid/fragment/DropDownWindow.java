package com.longjoe.ui.grid.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.longjoe.ui.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 何锦发 on 2017/4/7.
 */
public class DropDownWindow extends PopupWindow {

    public interface OnMultiChoiceListener {
        void onPositiveClick(PopupWindow popupWindow, boolean[] checked);
    }

    public interface OnSingleChoiceListener {
        void onSingleChoice(PopupWindow popupWindow, int position);
    }

    private Context mCtx;
    private List mData;
    private boolean isMulti;
    private Adapter mAdapter;
    private OnMultiChoiceListener multiChoiceListener;
    private OnSingleChoiceListener singleChoiceListener;
    private boolean[] checked;
    private String mTitle;
    private int itemPosition = -1;

    public int getItemPosition() {
        return itemPosition;
    }

    public DropDownWindow(Context ctx, List data) {
        this.mCtx = ctx;
        this.mData = data;
        this.checked = new boolean[data.size()];
    }

    public DropDownWindow(Context ctx, String[] data) {
        this.mCtx = ctx;
        this.mData = new ArrayList();
        for (String s : data) {
            mData.add(s);
        }
        this.checked = new boolean[data.length];
    }

    public DropDownWindow setMultiChoice(OnMultiChoiceListener multiChoiceListener) {
        isMulti = true;
        this.multiChoiceListener = multiChoiceListener;
        return this;
    }

    public DropDownWindow setTitle(String title) {
        this.mTitle = title;
        return this;
    }

    public DropDownWindow setSingleChoice(OnSingleChoiceListener singleChoiceListener) {
        this.isMulti = false;
        this.singleChoiceListener = singleChoiceListener;
        return this;
    }

    public void show(View view) {
        show(view, false);
    }

    public void show(View view, boolean isMatched) {
        if (multiChoiceListener == null && singleChoiceListener == null) {
            return;
        }
        if (mData.size() == 0) {
            return;
        }
        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(mCtx).inflate(
                R.layout.drop_down_window, null);
        final PopupWindow popupWindow = new PopupWindow(contentView,
                isMatched ? view.getWidth() : LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        ListView lv_group = (ListView) contentView.findViewById(R.id.lv_group);
        LinearLayout ll_group = (LinearLayout) contentView.findViewById(R.id.ll_group);
        if (!isMulti) {
            ll_group.setVisibility(View.GONE);
        } else {
            Button bt_confirm = (Button) contentView.findViewById(R.id.bt_confirm);
            bt_confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    multiChoiceListener.onPositiveClick(popupWindow, checked);
                }
            });
        }
        TextView tv_title = (TextView) contentView.findViewById(R.id.tv_title);
        if (mTitle == null) {
            tv_title.setVisibility(View.GONE);
        } else {
            tv_title.setText(mTitle);
        }
        mAdapter = new Adapter();
        lv_group.setAdapter(mAdapter);

        if (!isMulti) {
            lv_group.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    itemPosition=position;
                    singleChoiceListener.onSingleChoice(popupWindow, position);
                }
            });
        }
        popupWindow.setTouchable(true);
        popupWindow.setBackgroundDrawable(mCtx.getResources().getDrawable(R.color.cadetblue));
        popupWindow.showAsDropDown(view);
    }

    private class Adapter extends BaseAdapter {

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
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (!isMulti) {
                if (convertView == null) {
                    convertView = View.inflate(mCtx, R.layout.item_drop_down_text, null);
                    TextView text = (TextView) convertView.findViewById(R.id.tv_group);
                    convertView.setTag(R.id.text, text);
                }
                TextView item = (TextView) convertView.getTag(R.id.text);
                item.setText(mData.get(position).toString());
            } else {
                if (convertView == null) {
                    convertView = View.inflate(mCtx, R.layout.item_drop_down_check, null);
                    CheckBox text = (CheckBox) convertView.findViewById(R.id.cb_group);
                    convertView.setTag(R.id.check, text);
                }
                CheckBox tag = (CheckBox) convertView.getTag(R.id.check);
                tag.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        checked[position] = isChecked;
                    }
                });
                tag.setText(mData.get(position).toString());
            }
            return convertView;
        }
    }
}
