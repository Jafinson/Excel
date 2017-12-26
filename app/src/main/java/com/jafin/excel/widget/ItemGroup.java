package com.jafin.excel.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.LinearLayout;

/**
 * Created by 何锦发 on 2017/5/30.
 * 每行的item
 */
public class ItemGroup extends LinearLayout implements Checkable,View.OnClickListener {
    private boolean mChecked;
    private OnCheckedChangeListener onCheckedChangeListener;
    private int checkedColor;
    private int uncheckedColor;

    @Override
    public void onClick(View view) {
        toggle();
    }

    public interface OnCheckedChangeListener {
        void onCheckedChange(boolean isChecked);
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void toggle() {
        setChecked(!mChecked);
    }

    @Override
    public void setChecked(boolean checked) {
        if (mChecked != checked) {
            mChecked = checked;
            refreshDrawableState();
            if (onCheckedChangeListener != null) {
                onCheckedChangeListener.onCheckedChange(mChecked);
            }
        }
    }

    public ItemGroup(Context context) {
        this(context, null);
    }

    public ItemGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ItemGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setColor(int color) {
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setBackgroundColor(color);
        }
    }

    public void setCheckedColor(int checkedColor) {
        this.checkedColor = checkedColor;
    }

    public void setUncheckedColor(int unCheckedColor) {
        this.uncheckedColor = unCheckedColor;
    }

    @Override
    public void refreshDrawableState() {
        setColor(mChecked ? checkedColor : uncheckedColor);
        super.refreshDrawableState();
    }
}
