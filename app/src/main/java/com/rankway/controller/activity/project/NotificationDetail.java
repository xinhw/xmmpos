package com.rankway.controller.activity.project;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.rankway.controller.R;
import com.rankway.controller.activity.BaseActivity;
import com.rankway.sommerlibrary.utils.DateUtil;

public class NotificationDetail extends BaseActivity
        implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_detail);

        initView();
    }

    private void initView() {
        View backImag = findViewById(R.id.back_img);
        backImag.setOnClickListener(this);

        Intent intent = getIntent();
        String str = intent.getStringExtra("TITLE");

        TextView tv = findViewById(R.id.text_title);
        tv.setText(str);

        str = intent.getStringExtra("CONTENT");
        tv = findViewById(R.id.tv_content);
        tv.setText(str);

        long lt = intent.getLongExtra("TIME", System.currentTimeMillis());
        str = "时间：" + DateUtil.getDateDStr(lt);
        tv = findViewById(R.id.tv_time);
        tv.setText(str);

        str = intent.getStringExtra("FROM");
        tv = findViewById(R.id.tv_from);
        tv.setText("来自：" + str);

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (null != nm) nm.cancel(getIntent().getExtras().getInt("ID"));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_img:
                finish();
                break;
        }

    }
}