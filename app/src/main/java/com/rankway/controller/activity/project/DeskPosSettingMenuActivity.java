package com.rankway.controller.activity.project;

import android.os.Bundle;
import android.view.View;

import com.rankway.controller.R;
import com.rankway.controller.activity.BaseActivity;

public class DeskPosSettingMenuActivity
        extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desk_pos_setting_menu);
        
        initView();
    }

    private void initView() {
        View view = findViewById(R.id.back_img);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        view = findViewById(R.id.deskPosRecord);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(DeskPosPayRecordActivity.class);
            }
        });

        view = findViewById(R.id.deskPosModuleTest);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(DeskPosModulesTestActivity.class);
            }
        });

        view = findViewById(R.id.deskPosSetting);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(MobilePosSettingsActivity.class);
            }
        });
    }
}