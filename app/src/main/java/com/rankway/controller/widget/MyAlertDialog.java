package com.rankway.controller.widget;

/**
 * <pre>
 *   author : Xin Hongwei
 *   e-mail : xinhw@wxsemicon.com
 *   time  : 2023/09/17
 *   desc  :
 *   version: 1.0
 * </pre>
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rankway.controller.R;

import org.greenrobot.greendao.annotation.NotNull;

/**
 * @author： Felix
 * @time： 2023/3/13 14:39
 * @description：
 * @version： 1.0
 */
public class MyAlertDialog extends AlertDialog implements View.OnClickListener{

    private TextView btnCancel;
    private TextView btnConfirm;
    private TextView tvTitle;
    private TextView tvMsg;
    private LinearLayout llContent;

    private ConfirmListener confirmListener;
    private CancelListener cancelListener;

    private OnClickListener okListener;
    private OnClickListener noListener;

    public MyAlertDialog(@NotNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_custom_common);
        btnCancel = findViewById(R.id.btn_cancel);
        btnConfirm = findViewById(R.id.btn_confirm);
        btnCancel.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
        llContent = findViewById(R.id.ll_content);

        tvTitle = findViewById(R.id.diag_title);
        tvMsg = findViewById(R.id.diag_msg);
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        tvTitle.setText(title);
        tvTitle.setVisibility(View.VISIBLE);
    }

    @Override
    public void setMessage(CharSequence message) {
        super.setMessage(message);
        tvMsg.setText(message);
        tvMsg.setVisibility(View.VISIBLE);
    }

    public void resetContent(View v) {
        llContent.removeAllViews();
        llContent.addView(v);
        llContent.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_cancel:
                if(noListener != null) noListener.onClick(MyAlertDialog.this, DialogInterface.BUTTON_NEGATIVE);
                break;
            case R.id.btn_confirm:
                if(okListener != null) okListener.onClick(MyAlertDialog.this, DialogInterface.BUTTON_POSITIVE);
                break;
        }
    }

    public void setPositive(String confirmText,OnClickListener confirmListener){
        if(confirmListener!=null) {
            this.okListener = confirmListener;
            btnConfirm.setText(confirmText);
            btnConfirm.setVisibility(View.VISIBLE);
        }else{
            btnConfirm.setVisibility(View.GONE);
        }
//        setButton(btnConfirm.getId(), "", confirmListener);
    }

    public void setNegative(String cancelText,OnClickListener cancelListener){
        if(cancelListener!=null) {
            this.noListener = cancelListener;
            btnCancel.setText(cancelText);
            btnCancel.setVisibility(View.VISIBLE);
        }else {
            btnCancel.setVisibility(View.GONE);
        }
//        setButton(btnCancel.getId(), "", cancelListener);
    }

    public interface ConfirmListener{
        void onMyConfirm(MyAlertDialog dialog, View view);
    }

    public interface CancelListener{
        void onMyCancel(MyAlertDialog dialog, View view);
    }

    public View getCancelButton() {
        return this.btnCancel;
    }

    public View getConfirButton() {
        return this.btnConfirm;
    }
}

