package com.jafin.excel.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.layout.GridLayoutHelper;
import com.jafin.excel.R;
import com.jafin.excel.bean.Column;

import java.util.LinkedList;
import java.util.List;

public class TestActivity extends AppCompatActivity {

    private List<Column> mColumns;
    private List<Student> mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        RecyclerView excel = (RecyclerView) findViewById(R.id.excel);
        mColumns = Column.createByClz(Student.class);
        //columns.get(1).type= ViewTypeEnum.EDIT;
      //  excel.setLayout(mColumns);
        try {
            mData = Util.getList();
            BodyAdapter  mBodyAdapter = new BodyAdapter();
            final VirtualLayoutManager layoutManager = new VirtualLayoutManager(this);
            excel.setLayoutManager(layoutManager);
            DelegateAdapter da = new DelegateAdapter(layoutManager, false);
            List<DelegateAdapter.Adapter> adapters = new LinkedList<>();
            adapters.add(mBodyAdapter);
            da.setAdapters(adapters);
            //excel.show(mData);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            TextView item = new TextView(TestActivity.this);
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
