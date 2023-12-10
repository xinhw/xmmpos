package com.rankway.controller.activity.project;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.rankway.controller.R;
import com.rankway.controller.activity.BaseActivity;
import com.rankway.controller.adapter.MobliePosPayRecordAdapter;
import com.rankway.controller.persistence.DBManager;
import com.rankway.controller.persistence.entity.PaymentRecordEntity;
import com.rankway.controller.persistence.gen.PaymentRecordEntityDao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MobilePayRecordListActivity
        extends BaseActivity
        implements View.OnClickListener, MobliePosPayRecordAdapter.OnItemClickListener {

    final String TAG = "MobilePayRecordListActivity";

    View onDataView;
    RecyclerView recyclerView;
    List<PaymentRecordEntity> showRecords = new ArrayList<>();
    List<PaymentRecordEntity> todayRecords = new ArrayList<>();
    List<PaymentRecordEntity> allRecords = new ArrayList<>();

    private MobliePosPayRecordAdapter adapter;

    TextView tvTodayRecord;
    TextView tvAllRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_record_list);

        initView();

        initData();
    }

    private void initView() {
        TextView textView = findViewById(R.id.text_title);
        textView.setText("交易明细");

        onDataView = findViewById(R.id.noDataView);

        recyclerView = findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        int[] ids = {R.id.back_img, R.id.imgSetting, R.id.tvTodayRecord, R.id.tvAllRecord};
        setOnClickListener(ids);

        tvTodayRecord = findViewById(R.id.tvTodayRecord);
        tvAllRecord = findViewById(R.id.tvAllRecord);

        tvTodayRecord.setBackground(tvTodayRecord.getContext().getDrawable(R.color.btn_blue_pressed));
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
        adapter = new MobliePosPayRecordAdapter(mContext, showRecords);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);

        getAllRecords();

        Log.d(TAG, "记录数：" + allRecords.size());
        showRecords.clear();
        showRecords.addAll(todayRecords);

        if (allRecords.size() == 0) {
            recyclerView.setVisibility(View.GONE);
            onDataView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            onDataView.setVisibility(View.GONE);
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_img:
                finish();
                break;
            case R.id.imgSetting:
                popupBtnFunction(v);
                break;

            case R.id.tvTodayRecord:
                showRecords.clear();
                showRecords.addAll(todayRecords);
                adapter.notifyDataSetChanged();

                tvTodayRecord.setBackground(tvTodayRecord.getContext().getDrawable(R.color.btn_blue_pressed));
                tvAllRecord.setBackground(tvAllRecord.getContext().getDrawable(R.drawable.textview_border));
                break;

            case R.id.tvAllRecord:
                showRecords.clear();
                showRecords.addAll(allRecords);
                adapter.notifyDataSetChanged();

                tvTodayRecord.setBackground(tvTodayRecord.getContext().getDrawable(R.drawable.textview_border));
                tvAllRecord.setBackground(tvAllRecord.getContext().getDrawable(R.color.btn_blue_pressed));
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

    /***
     * 获取当日的交易记录
     */
    private void getAllRecords() {
        Log.d(TAG, "getTodayRecords");

        //  所有记录
        allRecords.clear();
        List<PaymentRecordEntity> records = DBManager.getInstance().getPaymentRecordEntityDao().queryBuilder().list();
        if (records.size() > 0) {
            Collections.reverse(records);
            allRecords.addAll(records);
        }

        //  今日记录
        Calendar calnow = Calendar.getInstance();
        calnow.set(Calendar.HOUR_OF_DAY, 0);
        calnow.set(Calendar.MINUTE, 0);
        calnow.set(Calendar.SECOND, 0);
        calnow.set(Calendar.MILLISECOND, 0);

        Date today = calnow.getTime();

        calnow.add(Calendar.DAY_OF_YEAR, 1);
        Date tommorw = calnow.getTime();

        todayRecords.clear();
        records = DBManager.getInstance().getPaymentRecordEntityDao().queryBuilder()
                .where(PaymentRecordEntityDao.Properties.TransTime.ge(today))
                .where(PaymentRecordEntityDao.Properties.TransTime.lt(tommorw))
                .list();
        if (records.size() > 0) {
            Collections.reverse(records);
            todayRecords.addAll(records);
        }

        return;
    }

    @Override
    public void onItemClick(View view, int position) {
        Log.d(TAG, "onItemClick " + position);
        adapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(position);
    }

    @Override
    public void onItemLongClick(View view, int position) {
        Log.d(TAG, "onItemLongClick " + position);
        adapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(position);
        showPaymentRecordDialog(1, showRecords.get(position));
    }

    @Override
    public void onItemDoubleClick(View view, int position) {
        Log.d(TAG, "onItemDoubleClick " + position);
        showPaymentRecordDialog(1, showRecords.get(position));
    }


    /***
     * 右上角弹出对话框
     * @param view
     */
    private void popupBtnFunction(View view) {
        int[] location = new int[2];
        view.getLocationInWindow(location);
        View popuView = getLayoutInflater().inflate(R.layout.popuwindow_view, null, false);
        PopupWindow popupWindow = new PopupWindow(popuView, 300, 200);
        popupWindow.setFocusable(true);

        TextView vw = popuView.findViewById(R.id.delete_item);
        vw.setText("清理");
        popuView.findViewById(R.id.delete_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 删除条目
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
                selectCleanDate();
            }
        });

        vw = popuView.findViewById(R.id.insert_item);
        vw.setText("生成");
        vw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 删除条目
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        });

        popupWindow.setOutsideTouchable(true);
        popupWindow.showAtLocation(view, Gravity.RIGHT | Gravity.TOP, 0, location[1] + 25);
    }


}