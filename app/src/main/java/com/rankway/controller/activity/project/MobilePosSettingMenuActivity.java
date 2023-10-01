package com.rankway.controller.activity.project;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.rankway.controller.R;
import com.rankway.controller.activity.BaseActivity;

public class MobilePosSettingMenuActivity
        extends BaseActivity
        implements View.OnClickListener {
    final String TAG = "MobilePosSettingMenuActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_setting);

        initView();
    }

    private void initView() {
        int[] ids = {R.id.back_img, R.id.paymentDetail, R.id.paymentStatistics, R.id.paymentSetting};
        setOnClickListener(ids);

        TextView textView = findViewById(R.id.text_title);
        textView.setText("参数设置");
    }


    private void setOnClickListener(int[] ids) {
        Log.d(TAG, "setOnClickListener");
        for (int id : ids) {
            View view = findViewById(id);
            if (null != view) view.setOnClickListener(this);
        }
        return;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_img:
                finish();
                break;

            case R.id.paymentDetail:
                startActivity(MobilePayRecordListActivity.class);
                break;

            case R.id.paymentStatistics:
                startActivity(MobilePosStatisticsActivity.class);
                break;

            case R.id.paymentSetting:
                startActivity(MobilePosSettingsActivity.class);
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
}