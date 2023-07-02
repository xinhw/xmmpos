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
import com.rankway.controller.adapter.PaymentRecordAdapter;
import com.rankway.controller.dto.PosInfoBean;
import com.rankway.controller.persistence.DBManager;
import com.rankway.controller.persistence.entity.PaymentRecord;
import com.rankway.controller.persistence.gen.PaymentRecordDao;
import com.rankway.controller.webapi.cardInfo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class PaymentRecordDetailActivity
        extends BaseActivity
        implements View.OnClickListener{

    final String TAG = "PaymentRecordDetailActivity";


    View onDataView;
    RecyclerView recyclerView;
    List<PaymentRecord> allRecords = new ArrayList<>();
    List<PaymentRecord> showRecords = new ArrayList<>();

    private PaymentRecordAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_record_detail);

        initView();

        initData();
    }

    private void initView() {
        TextView textView = findViewById(R.id.text_title);
        textView.setText("交易明细");

        onDataView = findViewById(R.id.noDataView);

        recyclerView = findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        int[] ids = {R.id.back_img,R.id.imgSetting};
        setOnClickListener(ids);
    }

    private void setOnClickListener(int[] ids){
        Log.d(TAG,"setOnClickListener");
        for(int id:ids){
            View view = findViewById(id);
            if(null!=view) view.setOnClickListener(this);
        }
        return;
    }

    private void initData() {
        adapter = new PaymentRecordAdapter(R.layout.item_payment_record,showRecords);
        recyclerView.setAdapter(adapter);

        getTodayRecords();
        Log.d(TAG,"记录数："+allRecords.size());

        showRecords.clear();
        if(allRecords.size()==0){
            recyclerView.setVisibility(View.GONE);
            onDataView.setVisibility(View.VISIBLE);
        }else{
            recyclerView.setVisibility(View.VISIBLE);
            onDataView.setVisibility(View.GONE);
            showRecords.addAll(allRecords);
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.back_img:
                finish();
                break;
            case R.id.imgSetting:
                genRecords();
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


    private void genRecords() {
        Log.d(TAG,"genRecords 生成测试记录");
        float amount = 1.00f;
        float remain = 1232.30f;
        int i;

        List<PaymentRecord> list = new ArrayList<>();
        list.clear();

        PosInfoBean posInfoBean = getPosInfoBean();

        cardInfo card = new cardInfo();
        card.setCardno(5049);
        card.setGno("00401061");
        card.setGsno("1E6776E6");
        card.setName("童惠涌");
        card.setQrType(0);
        card.setSystemId(0);
        card.setUserId("");
        card.setGremain(remain);

        for (i = 1; i < 21; i++) {
            posInfoBean.setAuditNo(posInfoBean.getAuditNo() + 1);
            amount = (float) (i * 0.11);

            PaymentRecord record = new PaymentRecord(card, amount, posInfoBean);
            list.add(record);

            card.setGremain(card.getGremain() - amount);
        }

        remain = 897.40f;

        card = new cardInfo();
        card.setCardno(383);
        card.setGno("00220013");
        card.setGsno("9E967AE6");
        card.setName("秦秦");
        card.setQrType(1);
        card.setSystemId(1);
        card.setUserId("736497");
        card.setGremain(remain);

        for (i = 1; i < 21; i++) {
            posInfoBean.setAuditNo(posInfoBean.getAuditNo() + 1);
            amount = (float) (i * 0.22);

            PaymentRecord record = new PaymentRecord(card, amount, posInfoBean);
            list.add(record);

            card.setGremain(card.getGremain() - amount);
        }
        DBManager.getInstance().getPaymentRecordDao().deleteAll();
        DBManager.getInstance().getPaymentRecordDao().saveInTx(list);
        Log.d(TAG,"DONE");
    }

    /***
     * 获取当日的交易记录
     */
    private void getTodayRecords(){
        Log.d(TAG,"getTodayRecords");

        Calendar calnow = Calendar.getInstance();
        calnow.set(Calendar.HOUR_OF_DAY,0);
        calnow.set(Calendar.MINUTE,0);
        calnow.set(Calendar.SECOND,0);
        calnow.set(Calendar.MILLISECOND,0);

        Date today = calnow.getTime();

        calnow.add(Calendar.DAY_OF_YEAR,1);
        Date tommorw = calnow.getTime();

        allRecords.clear();
        List<PaymentRecord> records = DBManager.getInstance().getPaymentRecordDao().queryBuilder()
                .where(PaymentRecordDao.Properties.TransTime.ge(today))
                .where(PaymentRecordDao.Properties.TransTime.lt(tommorw))
                .list();
        if(records.size()>0){
            Collections.sort(records);
            allRecords.addAll(records);
        }

        return;
    }
}