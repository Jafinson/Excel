package com.jafin.excel.fragment;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.jafin.excel.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 筛选窗口
 */

@SuppressLint({"NewApi", "ValidFragment"})
@SuppressWarnings("unchecked")
public class SearchableFragment extends DialogFragment implements View.OnClickListener {
    private List mData;//要显示的列表
    private List mSource;//数据源；
    private ListView lv_list;
    private OnPositiveListener mListener;//确认按钮的监听；
    private ArrayAdapter mAdapter;
    private String mTitle;//标题

    public void setListener(OnPositiveListener listener) {
        this.mListener = listener;
    }

    public interface OnPositiveListener {
        void callback(List data);
    }

    @SuppressLint("ValidFragment")
    public SearchableFragment(List data, String title) {
        this.mData = new ArrayList<>();
        this.mSource = new ArrayList<>();
        this.mData.addAll(data);
        this.mSource.addAll(data);
        this.mTitle = title;
    }

    public SearchableFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        View root = inflater.inflate(R.layout.fragment_searchable, container, false);
        lv_list = (ListView) root.findViewById(R.id.lv_list);
        lv_list.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        Button bt_confirm = (Button) root.findViewById(R.id.bt_confirm);
        bt_confirm.setOnClickListener(this);
        mAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_multiple_choice, mData);
        lv_list.setAdapter(mAdapter);
        EditText et_keyword = (EditText) root.findViewById(R.id.et_keyword);
        et_keyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
                mAdapter.notifyDataSetChanged();
            }
        });
        TextView tv_title = (TextView) root.findViewById(R.id.tv_title);
        tv_title.setText(mTitle + ":");
        return root;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_confirm && mListener != null) {
            SparseBooleanArray itemPositions = lv_list.getCheckedItemPositions();
            if (itemPositions.size() != 0) {
                List check = new ArrayList<>();
                for (int i = 0; i < mData.size(); i++) {
                    if (itemPositions.get(i)) {
                        check.add(mData.get(i));
                    }
                }
                mListener.callback(check);
                dismiss();
            }
        }
    }

    /**
     * 筛选关键字
     *
     * @param keyword 要删选的内容
     */
    private void filter(String keyword) {
        mData.clear();
        if (keyword.isEmpty()) {
            mData.addAll(mSource);
        } else {
            for (Object o : mSource) {
                if (o.toString().contains(keyword)) {
                    mData.add(o);
                }
            }
        }
    }


}
