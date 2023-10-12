package com.rankway.controller.activity.project;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.rankway.controller.R;
import com.rankway.controller.activity.BaseActivity;
import com.rankway.controller.adapter.MobilePosPayStatisticsAdapter;
import com.rankway.controller.entity.PaymentStatisticsRecordEntity;

import java.util.ArrayList;
import java.util.List;

public class MobilePosStatisticsActivity
        extends BaseActivity
        implements View.OnClickListener,
        MobilePosPayStatisticsAdapter.OnItemClickListener {
    final String TAG = "MobilePosStatisticsActivity";

    View onDataView;
    RecyclerView recyclerView;
    MobilePosPayStatisticsAdapter adapter;
    List<PaymentStatisticsRecordEntity> listStatistics = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_statistics);

        initView();

        initData();
    }


    private void initView() {
        TextView textView = findViewById(R.id.text_title);
        textView.setText("交易统计");

        onDataView = findViewById(R.id.noDataView);

        recyclerView = findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        int[] ids = {R.id.back_img, R.id.imgSetting};
        setOnClickListener(ids);
    }

    private void setOnClickListener(int[] ids) {
        Log.d(TAG, "setOnClickListener");
        for (int id : ids) {
            View view = findViewById(id);
            if (null != view) view.setOnClickListener(this);
        }
        return;
    }

    private void initData() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MobilePosPayStatisticsAdapter(mContext, listStatistics);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);

        List<PaymentStatisticsRecordEntity> list0 = getStatisticsRecord();
        listStatistics.clear();
        listStatistics.addAll(list0);

        adapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_img:
                finish();
                break;
            case R.id.imgSetting:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //  右下角返回键
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            finish();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    public void onItemLongClick(View view, int position) {

    }

    @Override
    public void onItemDoubleClick(View view, int position) {

    }
}