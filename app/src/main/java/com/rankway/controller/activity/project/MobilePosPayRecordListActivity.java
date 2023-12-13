package com.rankway.controller.activity.project;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.rankway.controller.R;
import com.rankway.controller.activity.BaseActivity;
import com.rankway.controller.adapter.MobilePosPayRecordAdapter;
import com.rankway.controller.hardware.util.DetLog;
import com.rankway.controller.persistence.DBManager;
import com.rankway.controller.persistence.entity.PaymentRecord;
import com.rankway.controller.persistence.gen.PaymentRecordDao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MobilePosPayRecordListActivity
        extends BaseActivity
        implements View.OnClickListener, MobilePosPayRecordAdapter.OnItemClickListener {

    final String TAG = "MobilePosPayRecordListActivity";

    View onDataView;
    RecyclerView recyclerView;
    List<PaymentRecord> showRecords = new ArrayList<>();
    List<PaymentRecord> todayRecords = new ArrayList<>();
    List<PaymentRecord> allRecords = new ArrayList<>();

    private MobilePosPayRecordAdapter adapter;

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
        adapter = new MobilePosPayRecordAdapter(mContext, showRecords);
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
        List<PaymentRecord> records = DBManager.getInstance().getPaymentRecordDao().queryBuilder().list();
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
        records = DBManager.getInstance().getPaymentRecordDao().queryBuilder()
                .where(PaymentRecordDao.Properties.TransTime.ge(today))
                .where(PaymentRecordDao.Properties.TransTime.lt(tommorw))
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
        PopupWindow popupWindow = new PopupWindow(popuView, 300, 100);
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
        vw.setVisibility(View.GONE);
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

    /***
     * 设置清理日期
     */
    private void selectCleanDate() {
        Calendar c = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, AlertDialog.THEME_HOLO_LIGHT,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Date date = new Date(year, month, dayOfMonth, 0, 0, 0);
                        String s = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                        DetLog.writeLog(TAG, "清理日期之前记录：" + s);

                        cleanDataPromptDialog(date, s);
                    }

                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                Log.d(TAG, "onKey " + keyCode);
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    dialog.dismiss();

                    DatePicker datePicker = datePickerDialog.getDatePicker();
                    Date date = new Date(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), 0, 0, 0);
                    String s = String.format("%04d-%02d-%02d", datePicker.getYear(), datePicker.getMonth() + 1, datePicker.getDayOfMonth());
                    DetLog.writeLog(TAG, "清理日期之前记录：" + s);

                    cleanDataPromptDialog(date, s);

                    return true;
                }

                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                    return true;
                }
                return false;
            }
        });
        datePickerDialog.show();
    }

    /***
     * 清理确认对话框
     * @param date
     * @param strdate
     */
    private void cleanDataPromptDialog(Date date, String strdate) {
        showDialogMessage("删除", String.format("是否清除%s之前的交易明细？", strdate),
                "确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        DBManager.getInstance().getPaymentRecordDao()
                                .queryBuilder()
                                .where(PaymentRecordDao.Properties.TransTime.lt(date))
                                .where(PaymentRecordDao.Properties.UploadFlag.eq(1))
                                .buildDelete()
                                .executeDeleteWithoutDetachingEntities();
                        playSound(true);
                    }
                },
                "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }, null);
    }

}