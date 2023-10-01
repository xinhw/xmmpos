package com.rankway.controller.activity.project;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.TextView;

import com.rankway.controller.R;
import com.rankway.controller.activity.BaseActivity;
import com.rankway.controller.activity.project.view.RankwayExpandableListView;
import com.rankway.controller.adapter.DeskPosPayRecordAdapter;
import com.rankway.controller.entity.PaymentStatisticsRecordEntity;
import com.rankway.controller.persistence.entity.PaymentRecord;

import java.util.ArrayList;
import java.util.List;

public class DeskPosPayRecordActivity extends BaseActivity {
    final String TAG = "DeskPosPayRecordActivity";

    View onDataView;
    RankwayExpandableListView recyclerView;
    DeskPosPayRecordAdapter adapter;
    List<PaymentStatisticsRecordEntity> listRecords = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desk_pos_pay_record);

        initView();

        initData();

        initAdapter();
    }

    private void initView() {
        TextView textView = findViewById(R.id.text_title);
        textView.setText("交易明细");

        onDataView = findViewById(R.id.noDataView);

        recyclerView = findViewById(R.id.recycleView);

        View view = findViewById(R.id.back_img);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initAdapter(){
        adapter = new DeskPosPayRecordAdapter(mContext,listRecords);
        recyclerView.setAdapter(adapter);
        recyclerView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        adapter.setOnItemClickListener(new DeskPosPayRecordAdapter.OnItemClickListener() {
            @Override
            public void childOnClickListener(int groupPosition, int childPosition, PaymentRecord record) {
                adapter.setSelectedChildItem(groupPosition, childPosition);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void parentOnClickListener(View view, int groupPosition) {
                //  收缩其他已经扩展的排
                for(int i=0;i<listRecords.size();i++){
                    if(i!=groupPosition) {
                        if(recyclerView.isGroupExpanded(i)) recyclerView.collapseGroup(i);
                    }
                }

                //  如果原来是收缩就扩展；如果是扩展就收缩
                boolean b = recyclerView.isGroupExpanded(groupPosition);
                if(b){
                    recyclerView.collapseGroup(groupPosition);
                }else {
                    recyclerView.expandGroup(groupPosition);
                }
                adapter.setSelectedGroupItem(groupPosition);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void initData() {
        List<PaymentStatisticsRecordEntity> list = getStatisticsRecord();

        listRecords.clear();
        listRecords.addAll(list);

        Log.d(TAG,"记录数："+listRecords.size());

        if(listRecords.size()>0){
            recyclerView.setVisibility(View.VISIBLE);
            onDataView.setVisibility(View.GONE);
        }else{
            recyclerView.setVisibility(View.GONE);
            onDataView.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
    }
}