package com.rankway.controller.activity.project;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.rankway.controller.R;
import com.rankway.controller.activity.BaseActivity;
import com.rankway.controller.adapter.MobliePosPayStatisticsAdapter;
import com.rankway.controller.entity.PaymentStatisticsRecordEntity;
import com.rankway.controller.hardware.util.DetLog;
import com.rankway.controller.persistence.DBManager;
import com.rankway.controller.persistence.entity.PaymentRecord;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MobilePosPayStatisticsActivity
        extends BaseActivity
        implements View.OnClickListener,
        MobliePosPayStatisticsAdapter.OnItemClickListener {
    final String TAG = "MobilePosPayStatisticsActivity";

    View onDataView;
    RecyclerView recyclerView;
    MobliePosPayStatisticsAdapter adapter;
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
        adapter = new MobliePosPayStatisticsAdapter(mContext, listStatistics);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);

        getStatisticsRecord();
        adapter.notifyDataSetChanged();
    }

    private void getStatisticsRecord() {
        Log.d(TAG, "getStatisticsRecord");

        listStatistics.clear();
        List<PaymentRecord> records = DBManager.getInstance().getPaymentRecordDao()
                .queryBuilder()
                .list();
        if (records.size() == 0) return;
        Log.d(TAG, "记录总数：" + records.size());

        records.sort(new Comparator<PaymentRecord>() {
            @Override
            public int compare(PaymentRecord o1, PaymentRecord o2) {
                if (o1.getTransTime().before(o2.getTransTime())) return -1;
                return 1;
            }
        });

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        int seqNo = 1;
        for (int i = 0; i < records.size(); i++) {
            PaymentRecord record = records.get(i);
            String s = format.format(record.getTransTime());

            boolean bexist = false;
            for (PaymentStatisticsRecordEntity entity : listStatistics) {
                if (s.equals(entity.getCdate())) {
                    bexist = true;
                    entity.setSubCount(entity.getSubCount() + 1);
                    entity.setSubAmount(entity.getSubAmount() + record.getAmount());
                    entity.getRecordList().add(record);
                    break;
                }
            }
            if (bexist) continue;

            PaymentStatisticsRecordEntity entity = new PaymentStatisticsRecordEntity();
            entity.setSeqNo(seqNo);
            entity.setSubCount(1);
            entity.setCdate(s);
            entity.setSubAmount(record.getAmount());
            entity.getRecordList().add(record);
            listStatistics.add(entity);
            seqNo++;
        }
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(TAG, "onConfigurationChanged");

        DetLog.writeLog(TAG,"onConfigurationChanged "+newConfig.toString());
        //USB 拔插动作, 这个方法都会被调用.
        super.onConfigurationChanged(newConfig);
    }
}