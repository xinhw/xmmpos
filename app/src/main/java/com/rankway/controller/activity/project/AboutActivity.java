package com.rankway.controller.activity.project;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rankway.controller.R;
import com.rankway.controller.activity.BaseActivity;
import com.rankway.controller.hardware.util.DetLog;
import com.rankway.controller.utils.SommerUtils;
import com.rankway.sommerlibrary.utils.NetUtil;

public class AboutActivity extends BaseActivity implements View.OnClickListener {

    private final static String TAG = "AboutActivity";

    private boolean m_bUpgrading = false;
    private TextView m_btnUpdate;

    final static int COUNTS = 5;// 点击次数
    final static long DURATION = 2000;// 规定有效时间
    long[] mHits = new long[COUNTS];

    private RelativeLayout rlWebsite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initView();
        initData();
    }


    private void initData() {

    }

    private void initView() {
        RelativeLayout update = findViewById(R.id.rl_update);
        TextView speed = findViewById(R.id.speed);
        ProgressBar updateProgress = findViewById(R.id.update_progress);
        TextView appVersion = findViewById(R.id.set_app_version);
        TextView checkUpdate = findViewById(R.id.check_update);
        checkUpdate.setOnClickListener(this);


        String s = "";
        appVersion.setText(SommerUtils.getVersionName(this) + s);

        m_btnUpdate = findViewById(R.id.check_update);

        rlWebsite = findViewById(R.id.rl_site);
        rlWebsite.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.check_update:
                if (NetUtil.getNetType(mContext) < 0) {
                    showStatusDialog("请去设置网络！");
                    return;
                }

                m_bUpgrading = true;
                m_btnUpdate.setVisibility(View.GONE);

                checkAppUpdate();

                break;
            case R.id.rl_site:
                break;
        }
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
        if(keyCode==KeyEvent.KEYCODE_BACK){
            finish();
            return true;
        }
        if(keyCode==KeyEvent.KEYCODE_HOME) return true;

        return super.dispatchKeyEvent(event);
    }


    @Override
    protected void showUpdateMessage(String msg) {
        Message message = new Message();
        message.what = 100;
        message.obj = msg;
        mHandler.sendMessage(message);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 100) {
                m_bUpgrading = false;
                Log.d(TAG, "showUpdateMessage:" + msg);
                showToast(msg.obj.toString());
                m_btnUpdate.setVisibility(View.VISIBLE);
            }
        }
    };

}