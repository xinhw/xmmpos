package com.rankway.controller.activity.project;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.rankway.controller.R;
import com.rankway.controller.activity.BaseActivity;
import com.rankway.controller.hardware.util.DetLog;
import com.rankway.controller.utils.ClickUtil;

public class DeskPosSettingMenuActivity
        extends BaseActivity
        implements View.OnClickListener {

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
        if (ClickUtil.isFastDoubleClick(v.getId())) {
            showToast("请勿连续点击!");
            return;
        }

        switch (v.getId()) {
            case R.id.deskPosRecord:
                startActivity(DeskPosPayRecordActivity.class);
                break;

            case R.id.deskPosModuleTest:
                startActivity(DeskPosModulesTestActivity.class);
                break;

            case R.id.deskPosSetting:
                startActivity(DeskPosSettingsActivity.class);
                break;

            case R.id.deskPosAuxillary:
                startActivity(DeskPosAuxillaryMenuActivity.class);
                break;

            case R.id.deskPosCleanData:
                inputPassword2CleanData();
                break;

            case R.id.back_img:
                finish();
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
        if(keyCode==KeyEvent.KEYCODE_BACK) return true;
        if(keyCode==KeyEvent.KEYCODE_HOME) return true;

        return super.dispatchKeyEvent(event);
    }

    /***
     * 输入密码区清理
     */
    private void inputPassword2CleanData() {
        // 展示提示框，进行数据清除
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(com.rankway.controller.R.layout.dialog_intput, null);
        EditText editPossword = view.findViewById(com.rankway.controller.R.id.edit_msg);
        editPossword.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(view);
        builder.setTitle("请输入密码清除:");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String possword = editPossword.getText().toString().trim();
                if (TextUtils.isEmpty(possword)) {
                    showToast("请输入密码后清理！");
                    playSound(false);
                    return;
                }

                if (!isAdvancedPasswordRight(possword)) {
                    showToast("密码错误！");
                    playSound(false);
                    return;
                }

                dialog.dismiss();

                selectCleanDate();
            }
        });
        builder.setNegativeButton(getString(com.rankway.controller.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(TAG, "onConfigurationChanged");

        DetLog.writeLog(TAG,"onConfigurationChanged "+newConfig.toString());
        //USB 拔插动作, 这个方法都会被调用.
        super.onConfigurationChanged(newConfig);
    }
}