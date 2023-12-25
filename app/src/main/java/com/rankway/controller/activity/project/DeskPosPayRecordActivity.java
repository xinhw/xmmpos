package com.rankway.controller.activity.project;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.TextView;

import com.rankway.controller.R;
import com.rankway.controller.activity.BaseActivity;
import com.rankway.controller.activity.project.view.RankwayExpandableListView;
import com.rankway.controller.adapter.DeskPosPayRecordAdapter;
import com.rankway.controller.entity.PaymentStatisticsRecordEntity;
import com.rankway.controller.hardware.util.DetLog;
import com.rankway.controller.persistence.entity.PaymentRecordEntity;

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

        initAdapter();

        initData();
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

    private void initAdapter() {
        Log.d(TAG, "initAdapter");

        adapter = new DeskPosPayRecordAdapter(mContext, listRecords);
        recyclerView.setAdapter(adapter);
        recyclerView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        adapter.setOnItemClickListener(new DeskPosPayRecordAdapter.OnItemClickListener() {
            @Override
            public void childOnClickListener(int groupPosition, int childPosition, PaymentRecordEntity record) {
                adapter.setSelectedChildItem(groupPosition, childPosition);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void parentOnClickListener(View view, int groupPosition) {
                //  收缩其他已经扩展的排
                for (int i = 0; i < listRecords.size(); i++) {
                    if (i != groupPosition) {
                        if (recyclerView.isGroupExpanded(i)) recyclerView.collapseGroup(i);
                    }
                }

                //  如果原来是收缩就扩展；如果是扩展就收缩
                boolean b = recyclerView.isGroupExpanded(groupPosition);
                if (b) {
                    recyclerView.collapseGroup(groupPosition);
                } else {
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

        Log.d(TAG, "记录数：" + listRecords.size());

        if (listRecords.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            onDataView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            onDataView.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
    }

    //  防止这个界面出现扫二维码的情况
    private StringBuilder mStringBufferResult = new StringBuilder();
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        final int MAX_BUFFER_LEN = 256;
        int keyCode = event.getKeyCode();
        Log.d(TAG, "dispatchKeyEvent " + keyCode);

        char aChar = (char) event.getUnicodeChar();
        if (aChar != 0) {
            mStringBufferResult.append(aChar);
        }

        //  若为回车键，直接返回
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            DetLog.writeLog(TAG,"扫描输入："+mStringBufferResult.toString());
            mStringBufferResult.setLength(0);
        }

        if(mStringBufferResult.length()>MAX_BUFFER_LEN){
            DetLog.writeLog(TAG,"键盘输入："+mStringBufferResult.toString());
            mStringBufferResult.setLength(0);
        }
        if(keyCode==KeyEvent.KEYCODE_ENTER) return true;
        if(keyCode==KeyEvent.KEYCODE_BACK) return true;
        if(keyCode==KeyEvent.KEYCODE_HOME) return true;

        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(TAG, "onConfigurationChanged");

        DetLog.writeLog(TAG,"onConfigurationChanged "+newConfig.toString());
        //USB 拔插动作, 这个方法都会被调用.
        super.onConfigurationChanged(newConfig);
    }
}