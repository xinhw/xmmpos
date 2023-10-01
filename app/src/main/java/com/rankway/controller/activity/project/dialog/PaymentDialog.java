package com.rankway.controller.activity.project.dialog;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.rankway.controller.R;
import com.rankway.controller.activity.BaseActivity;
import com.rankway.controller.dto.PosInfoBean;
import com.rankway.controller.hardware.util.DetLog;
import com.rankway.controller.persistence.DBManager;
import com.rankway.controller.persistence.entity.PaymentRecord;
import com.rankway.controller.reader.ReaderFactory;
import com.rankway.controller.webapi.cardInfo;
import com.rankway.controller.webapi.decodeQRCode;
import com.rankway.controller.webapi.payWebapi;
import com.rankway.controller.webapi.posAudit;
import com.rankway.sommerlibrary.utils.ToastUtils;

import java.util.Date;

/**
 * <pre>
 *   author : Xin Hongwei
 *   e-mail : xinhw@wxsemicon.com
 *   time  : 2023/09/30
 *   desc  :
 *   version: 1.0
 * </pre>
 */
@SuppressLint("ValidFragment")
public class PaymentDialog
        extends DialogFragment
        implements View.OnClickListener{

    private final String TAG = "PaymentDialog";

    public static final int PAY_MODE_CARD = 0;
    public static final int PAY_MODE_QRCODE = 1;

    private int payMode = PAY_MODE_CARD;
    private int nAmount = 0;

    private TextView tvPayMode;

    private Handler mHandler = null;
    private Context mContext;
    private BaseActivity baseActivity;

    private boolean isPaying = false;

    private PosInfoBean posInfoBean = null;

    @SuppressLint("ValidFragment")
    public PaymentDialog(Context context,
                         BaseActivity baseActivity,
                         PosInfoBean posInfoBean,
                         int mode,
                         int amount){
        this.mContext = context;
        this.baseActivity = baseActivity;
        this.posInfoBean = posInfoBean;
        this.payMode = mode;
        this.nAmount = amount;
    }


    @Override
    public void onStart() {
        super.onStart();
        Window win = getDialog().getWindow();
        // 一定要设置Background，如果不设置，window属性设置无效
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().setCancelable(false);
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return processKeyEvent(keyCode,event);
            }
        });

        // 一定要设置Background，如果不设置，window属性设置无效
        win.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().setCanceledOnTouchOutside(false);
        setCancelable(false);
        WindowManager.LayoutParams params = win.getAttributes();
        params.gravity = Gravity.CENTER;
        // 使用ViewGroup.LayoutParams，以便Dialog 宽度充满整个屏幕
        params.width = (int) (dm.widthPixels * 0.9);
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        win.setAttributes(params);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_payment,null);
        TextView textView = rootView.findViewById(R.id.tvCancel);
        textView.setOnClickListener(this);

        mHandler = new Handler();

        tvPayMode = rootView.findViewById(R.id.tvPayMode);
        if(payMode==PAY_MODE_CARD){
            tvPayMode.setText("支付方式：IC卡");
            readCardThread = new ReadCardThread();
            readCardThread.start();
        }else{
            tvPayMode.setText("支付方式：二维码");
        }

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvCancel:
                if(null!=onPaymentResultListner) {
                    if(null!=readCardThread){
                        try {
                            readCardThread.join();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    
                    onPaymentResultListner.onPaymentCancel();
                }
                break;
        }
    }


    public interface OnPaymentResult{
        void onPaymentSuccess(PaymentRecord record);
        void onPaymentCancel();
    }

    private OnPaymentResult onPaymentResultListner = null;

    public void setOnPaymentResultListner(OnPaymentResult listner){
        this.onPaymentResultListner = listner;
    }


    private boolean processKeyEvent(int keyCode,KeyEvent event){
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            char aChar = (char) event.getUnicodeChar();
            if (aChar != 0) {
                mStringBufferResult.append(aChar);
            }

            mHandler.removeCallbacks(mScanningFishedRunnable);

            //若为回车键，直接返回
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                mHandler.post(mScanningFishedRunnable);
            } else {
                //延迟post，若500ms内，有其他事件
                mHandler.postDelayed(mScanningFishedRunnable, 500L);
            }
            return true;
        }
        return false;
    }
    /**
     * 二维码信息原始数据容器
     * 参考：https://www.jb51.net/article/224306.htm
     */
    private StringBuilder mStringBufferResult = new StringBuilder();
    private Runnable mScanningFishedRunnable = new Runnable() {
        @Override
        public void run() {
            String qrcode = mStringBufferResult.toString();
            mStringBufferResult.setLength(0);
            if (TextUtils.isEmpty(qrcode)){
                Log.d(TAG,"qrcode is empty");
                return;
            }

            cardInfo cardPaymentObj = decodeQRCode.decode(qrcode);
            if (null == cardPaymentObj) {
                baseActivity.playSound(false);
                ToastUtils.showLong(mContext, "无效的二维码");
                return;
            }

            PutMessage(PAY_MODE_QRCODE,cardPaymentObj);
        }
    };

    private  ReadCardThread readCardThread = null;
    class ReadCardThread extends Thread{
        @Override
        public void run(){
            while (true){
                sleep(100);

                String sn = ReaderFactory.getReader(mContext).getCardNo();
                if(sn==null) continue;

                cardInfo cardPaymentObj = new cardInfo();
                cardPaymentObj.setGsno(sn);

                PutMessage(PAY_MODE_CARD,cardPaymentObj);
                break;
            }
        }

        private void sleep(int ms){
            try{
                Thread.sleep(ms);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void PutMessage(int type,cardInfo cardPaymentObj) {
        mHandler.post(() -> {
            if(isPaying) return;

            AsynTaskPayment task = new AsynTaskPayment(type,cardPaymentObj,(float)(nAmount*0.01));
            task.execute();
        });
    }

    /****
     * 异步支付任务
     */
    private class AsynTaskPayment extends AsyncTask<String, Integer, Integer> {
        float famount = 0;
        String errString = "";
        cardInfo cardPaymentObj;
        int payMode = PAY_MODE_CARD;

        public AsynTaskPayment(int payMode,cardInfo obj,float amount) {
            this.payMode = payMode;
            this.cardPaymentObj = obj;
            this.famount = amount;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            isPaying = true;

            showProDialog("请稍等...");
        }

        @Override
        protected Integer doInBackground(String... strings) {
            int ret = -1;
            payWebapi obj = payWebapi.getInstance();

            obj.setServerIP(posInfoBean.getServerIP());
            obj.setPortNo(posInfoBean.getPortNo());

            // 1. 获取POS流水
            posAudit audit = obj.getPosAuditNo(posInfoBean.getCposno());
            if (null == audit) {
                errString = "获取POS流水号失败！";
                return -1;
            }

            //  设置POS流水号
            posInfoBean.setAuditNo(audit.getPosCno());
            baseActivity.savePosInfoBean(posInfoBean);

            obj.setCposno(posInfoBean.getCposno());
            obj.setCusercode(posInfoBean.getUsercode());

            //  2. 查询凭证信息
            cardInfo cardInfoObj = null;
            if (payMode == PAY_MODE_CARD) {
                DetLog.writeLog(TAG, "IC卡支付查询：");
                cardInfoObj = obj.getPersonInfoBySNO(cardPaymentObj.getGsno());
                cardInfoObj.setGsno(cardPaymentObj.getGsno());
                DetLog.writeLog(TAG, "getPersonBySNO:" + cardInfoObj);
            } else {
                DetLog.writeLog(TAG, "二维码支付查询：");
                cardInfoObj = obj.getPersonInfoByQrCode(cardPaymentObj.getSystemId(), cardPaymentObj.getQrType(), cardPaymentObj.getUserId());
                DetLog.writeLog(TAG, "getQrPersonInfo:" + cardInfoObj);
            }
            if (null == cardInfoObj){
                errString = obj.getErrMsg();
                return -1;
            }

            //  3. 比较余额
            if(cardInfoObj.getGremain()<famount){
                errString = "余额不足，无法支付";
                return -1;
            }

            //  4. 支付
            cardPaymentObj = new cardInfo(cardInfoObj);
            if (payMode==PAY_MODE_CARD) {
                // public int qrPayment(int auditNo,int systemId,int qrType,String userId,Date cdate,int cmoney){
                ret = obj.qrPayment(posInfoBean.getAuditNo(), cardPaymentObj.getSystemId(), cardPaymentObj.getQrType(), cardPaymentObj.getUserId(), new Date(), (int) (famount * 100));
                DetLog.writeLog(TAG, "qrPayment:" + ret);
            } else {
                // public int cardPayment(int auditNo,int cardno,Date cdate,int cmoney){
                ret = obj.cardPayment(posInfoBean.getAuditNo(), cardPaymentObj.getCardno(), new Date(), (int) (famount * 100));
                DetLog.writeLog(TAG, "cardPayment:" + ret);
            }
            if(ret!=0){
                errString = obj.getErrMsg();
                return ret;
            }

            return 0;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

            missProDialog();

            isPaying = false;

            if (0 == integer) {
                baseActivity.playSound(true);
                DetLog.writeLog(TAG, "支付成功：" + cardPaymentObj.toString());

                PaymentRecord record = new PaymentRecord(cardPaymentObj, (int)(famount*100), posInfoBean);

                record.setUploadFlag(0x01);
                DBManager.getInstance().getPaymentRecordDao().save(record);

                if(null!=onPaymentResultListner) onPaymentResultListner.onPaymentSuccess(record);

            } else {
                baseActivity.playSound(false);

                ToastUtils.showLong(mContext,errString);

                DetLog.writeLog(TAG, "支付失败：" + errString);
            }
            return;
        }
    }


    private ProgressDialog progressDialog;
    protected void showProDialog(String msg) {
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(msg);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    protected void missProDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        progressDialog = null;
    }
}
