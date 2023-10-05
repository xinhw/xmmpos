package com.rankway.controller.activity.project;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.rankway.controller.R;
import com.rankway.controller.activity.BaseActivity;
import com.rankway.controller.utils.ClickUtil;

public class DeskPosSettingMenuActivity
        extends BaseActivity
        implements View.OnClickListener{

    final String TAG = "DeskPosSettingMenuActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desk_pos_setting_menu);
        
        initView();
    }

    private void initView() {
        View view = findViewById(R.id.back_img);
        view.setOnClickListener(this);

        view = findViewById(R.id.deskPosRecord);
        view.setOnClickListener(this);

        view = findViewById(R.id.deskPosModuleTest);
        view.setOnClickListener(this);

        view = findViewById(R.id.deskPosSetting);
        view.setOnClickListener(this);

        view = findViewById(R.id.deskPosAuxillary);
        view.setOnClickListener(this);

        view = findViewById(R.id.deskPosCleanData);
        view.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(ClickUtil.isFastDoubleClick(v.getId())){
            showToast("请勿连续点击!");
            return;
        }

        switch (v.getId()){
            case R.id.deskPosRecord:
                startActivity(DeskPosPayRecordActivity.class);
                break;

            case R.id.deskPosModuleTest:
                startActivity(DeskPosModulesTestActivity.class);
                break;

            case R.id.deskPosSetting:
                startActivity(MobilePosSettingsActivity.class);
                break;

            case R.id.deskPosAuxillary:
                startActivity(DeskPosAuxillaryMenuActivity.class);
                break;

            case R.id.deskPosCleanData:
                selectCleanDate();
                break;

            case R.id.back_img:
                finish();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG,"onKeyDown "+keyCode);

        //  右下角返回键
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            return true;
        }
        if(KeyEvent.KEYCODE_HOME == keyCode){
            return true;
        }

        return super.onKeyUp(keyCode, event);
    }


}