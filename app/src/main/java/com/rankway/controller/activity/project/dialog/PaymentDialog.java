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

import com.alibaba.fastjson.JSON;
import com.rankway.controller.R;
import com.rankway.controller.activity.BaseActivity;
import com.rankway.controller.activity.project.manager.SpManager;
import com.rankway.controller.common.AppIntentString;
import com.rankway.controller.dto.PosInfoBean;
import com.rankway.controller.hardware.util.DetLog;
import com.rankway.controller.persistence.DBManager;
import com.rankway.controller.persistence.entity.DishEntity;
import com.rankway.controller.persistence.entity.PaymentItemEntity;
import com.rankway.controller.persistence.entity.PaymentRecordEntity;
import com.rankway.controller.persistence.entity.PaymentTotal;
import com.rankway.controller.persistence.entity.PersonInfoEntity;
import com.rankway.controller.persistence.entity.QrBlackListEntity;
import com.rankway.controller.persistence.gen.PersonInfoEntityDao;
import com.rankway.controller.persistence.gen.QrBlackListEntityDao;
import com.rankway.controller.printer.PrinterBase;
import com.rankway.controller.printer.PrinterFactory;
import com.rankway.controller.printer.PrinterUtils;
import com.rankway.controller.reader.ReaderFactory;
import com.rankway.controller.utils.HttpUtil;
import com.rankway.controller.webapi.cardInfo;
import com.rankway.controller.webapi.decodeQRCode;
import com.rankway.controller.webapi.payWebapi;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private TextView tvPayAmount;

    private Handler mHandler = null;
    private Context mContext;
    private BaseActivity baseActivity;

    private boolean isPaying = false;

    private PosInfoBean posInfoBean = null;

    private List<DishEntity> listDishes = new ArrayList<>();

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
        params.width = (int) (dm.widthPixels * 0.6);                //  设置对话框宽度
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;        //  设置对话框高度
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

            int ret = ReaderFactory.getReader(mContext).openReader();
            if(0!=ret){
                baseActivity.showLongToast("读卡器打开失败，请检查连接!");
                baseActivity.playSound(false);
            }else {
                readCardThread = new ReadCardThread();
                readCardThread.start();
            }
        }else{
            tvPayMode.setText("支付方式：二维码");
        }

        tvPayAmount = rootView.findViewById(R.id.tvPayAmount);
        tvPayAmount.setText(String.format("支付金额：%.2f",nAmount*0.01));

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvCancel:
                if(null!=readCardThread){
                    Log.d(TAG,"终止线程");
                    try {
                        readCardThread.interrupt();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                Log.d(TAG,"终止线程退出");

                baseActivity.detSleep(200);

                if(null!=onPaymentResultListner) onPaymentResultListner.onPaymentCancel();
                dismiss();
                break;
        }
    }


    public interface OnPaymentResult{
        void onPaymentSuccess(int type,int flag,int amount,List<DishEntity> dishes,PaymentRecordEntity record);
        void onPaymentCancel();
    }

    private OnPaymentResult onPaymentResultListner = null;

    public void setOnPaymentResultListner(OnPaymentResult listner){
        this.onPaymentResultListner = listner;
    }

    /***
     * 处理扫描头输入
     * @param keyCode
     * @param event
     * @return
     */
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

            try {
                cardInfo cardPaymentObj = decodeQRCode.decode(qrcode);
                if (null == cardPaymentObj) {
                    baseActivity.playSound(false);
                    baseActivity.showLongToast("无效的二维码");
                    return;
                }
                Log.d(TAG, "二维码：" + qrcode);

                PutMessage(PAY_MODE_QRCODE, cardPaymentObj);
            }catch (Exception e){
                DetLog.writeLog(TAG,"无法解析的二维码："+qrcode);
                baseActivity.playSound(false);
                baseActivity.showLongToast("无效的二维码");
                return;
            }

        }
    };

    private  ReadCardThread readCardThread = null;
    class ReadCardThread extends Thread{
        @Override
        public void run(){
            cardInfo cardPaymentObj = null;
            while (!isInterrupted()){
                try {
                    Thread.sleep(100);
                }catch (Exception e){
                    e.printStackTrace();
                    break;
                }
                String sn = ReaderFactory.getReader(mContext).getCardNo();

                if(sn==null) continue;

                cardPaymentObj = new cardInfo();
                cardPaymentObj.setGsno(sn);
                break;
            }

            ReaderFactory.getReader(mContext).closeReader();

            if(null!=cardPaymentObj) PutMessage(PAY_MODE_CARD,cardPaymentObj);
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
        int payMode;
        boolean isOnlineFailure = false;
        boolean isOnlinePay = true;

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
            if(HttpUtil.isOnline){
                isOnlinePay = true;
                ret = onlinePayment();
            }else{
                isOnlinePay = false;
                ret = offlinePayment();
            }
            return ret;
        }


        /***
         * 在线支付
         * @return
         */
        private int onlinePayment(){
            int ret = -1;
            payWebapi obj = payWebapi.getInstance();

            obj.setServerIP(posInfoBean.getServerIP());
            obj.setPortNo(posInfoBean.getPortNo());

            // 1. 获取POS流水
            int auditNo = posInfoBean.getAuditNo();
            auditNo++;

            //  设置POS流水号
            posInfoBean.setAuditNo(auditNo);
            baseActivity.savePosInfoBean(posInfoBean);

            obj.setCposno(posInfoBean.getCposno());
            obj.setCusercode(posInfoBean.getUsercode());

            //  2. 查询凭证信息
            cardInfo cardInfoObj = null;
            if (payMode == PAY_MODE_CARD) {
                DetLog.writeLog(TAG, "IC卡支付查询：");
                cardInfoObj = obj.getPersonInfoBySNO(cardPaymentObj.getGsno());
                if(null!=cardInfoObj) cardInfoObj.setGsno(cardPaymentObj.getGsno());
                DetLog.writeLog(TAG, "getPersonBySNO:" + cardInfoObj);
            } else {
                DetLog.writeLog(TAG, "二维码支付查询：");
                cardInfoObj = obj.getPersonInfoByQrCode(cardPaymentObj.getSystemId(), cardPaymentObj.getQrType(), cardPaymentObj.getUserId());
                DetLog.writeLog(TAG, "getQrPersonInfo:" + cardInfoObj);
            }
            if (null == cardInfoObj){
                errString = obj.getErrMsg();
                DetLog.writeLog(TAG,"查询失败："+errString);
                return -1;
            }

            //  3. 比较余额
            if(cardInfoObj.getGremain()<famount){
                errString = "余额不足，无法支付";
                DetLog.writeLog(TAG,String.format("余额不足，无法支付：%.2f,%.2f",cardInfoObj.getGremain(),famount));
                return -1;
            }

            //  4. 支付流程
            cardPaymentObj = new cardInfo(cardInfoObj);
            if (payMode==PAY_MODE_CARD) {
                // public int cardPayment(int auditNo,int cardno,Date cdate,int cmoney){
                ret = obj.cardPayment(posInfoBean.getAuditNo(), cardPaymentObj.getCardno(), new Date(), (int) (famount * 100));
                DetLog.writeLog(TAG, "cardPayment:" + ret);
            } else {
                // public int qrPayment(int auditNo,int systemId,int qrType,String userId,Date cdate,int cmoney){
                ret = obj.qrPayment(posInfoBean.getAuditNo(), cardPaymentObj.getSystemId(), cardPaymentObj.getQrType(), cardPaymentObj.getUserId(), new Date(), (int) (famount * 100));
                DetLog.writeLog(TAG, "qrPayment:" + ret);
            }

            //  如果支付环节出现超时等情况
            //  先保存记录，状态改为离线，报成功
            isOnlineFailure = false;
            if(ret!=0){
                isOnlineFailure = true;
                DetLog.writeLog(TAG,"支付出现失败，保存离线记录："+ JSON.toJSONString(cardPaymentObj));
                errString = obj.getErrMsg();
            }
            return 0;
        }

        /***
         * 离线支付
         * @return
         */
        private int offlinePayment(){
            int ret = -1;
            ret = SpManager.getIntance().getSpInt(AppIntentString.OFFLINE_MAX_AMOUNT);
            if(ret<=0) ret = 30;
            int MAX_OFFLINE_AMOUNT = ret;

            // 1. 获取POS流水
            int auditNo = posInfoBean.getAuditNo();
            auditNo++;

            //  设置POS流水号
            posInfoBean.setAuditNo(auditNo);
            baseActivity.savePosInfoBean(posInfoBean);

            //  2. 查询凭证信息
            cardInfo cardInfoObj = null;
            if (payMode == PAY_MODE_CARD) {
                //  在不在白名单内
                DetLog.writeLog(TAG, "IC卡支付查询(离线)：");
                cardInfoObj = isCardInWhiteList(cardPaymentObj.getGsno());
                if(null==cardInfoObj){
                    errString = "无效卡片，不能支付！";
                    DetLog.writeLog(TAG,"失败："+errString);
                    return -1;
                }
            } else {
                //  在不在黑名单内
                DetLog.writeLog(TAG, "二维码支付查询(离线)：");
                cardInfoObj = isQrCodeInBlackList(cardPaymentObj.getSystemId(),cardPaymentObj.getQrType(),cardPaymentObj.getUserId());
                if(null==cardInfoObj){
                    errString = "二维码在黑名单内，不能支付！";
                    DetLog.writeLog(TAG,"失败："+errString);
                    return -1;
                }
            }

            //  3. 比较余额
            if(MAX_OFFLINE_AMOUNT<famount){
                errString = "超过离线支付限制，无法支付";
                DetLog.writeLog(TAG,String.format("余额不足，无法支付：%.2f,%.2f",cardInfoObj.getGremain(),famount));
                return -1;
            }

            //  4. 支付
            cardPaymentObj = new cardInfo(cardInfoObj);

            return 0;
        }

        /***
         * 离线交易，判断卡唯一号是否在白名单内
         * @param gsno
         * @return
         */
        private cardInfo isCardInWhiteList(String gsno){
            Log.d(TAG,"isCardInWhiteList "+gsno);

            PersonInfoEntity person = DBManager.getInstance().getPersonInfoEntityDao()
                    .queryBuilder()
                    .where(PersonInfoEntityDao.Properties.Gsno.eq(gsno))
                    .unique();
            if(person==null){
                DetLog.writeLog(TAG,String.format("未找到卡[%s]对应的人员信息",gsno));
                return null;
            }
            Log.d(TAG,"person "+person.toString());

            cardInfo obj = new cardInfo();
            //  gremain
            obj.setGremain(0.0f);
            //  gno
            obj.setGno(person.getGno());
            //  gname
            obj.setName(person.getGname());
            //  StatusId
            obj.setStatusid(person.getStatusId());
            //  cardno
            obj.setCardno(person.getCardno());

            obj.setSystemId(0);
            obj.setQrType(0);
            obj.setUserId("");
            return obj;
        }

        /***
         * 离线二维码判断是否在黑名单内
         * @param systemid
         * @param qrtype
         * @param userId
         * @return
         */
        private cardInfo isQrCodeInBlackList(int systemid,int qrtype,String userId){
            Log.d(TAG,"isQrCodeInBlackList "+String.format("(%d,%d,%s)", systemid,qrtype,userId));
            QrBlackListEntity black = DBManager.getInstance().getQrBlackListEntityDao()
                    .queryBuilder()
                    .where(QrBlackListEntityDao.Properties.StatusId.eq(systemid))
                    .where(QrBlackListEntityDao.Properties.QrType.eq(qrtype))
                    .where(QrBlackListEntityDao.Properties.UserId.eq(userId))
                    .unique();

            if(black!=null){
                DetLog.writeLog(TAG,String.format("(%d,%d,%s)二维码在黑名单内:%s",
                        systemid,qrtype,userId,
                        black.toString()));
                return null;
            }

            //  {"gremain":567.39,"WorkNo":"00002203","Status":2,"Name":"杨欢","Cellphone":null,"CardNo":30943}
            cardInfo obj = new cardInfo();

            //  gremain
            obj.setGremain(0.0f);
            //  gno
            obj.setGno("");
            //  gname
            obj.setName("");
            //  StatusId
            obj.setStatusid(2);
            //  cardno
            obj.setCardno(0);

            obj.setSystemId(systemid);
            obj.setQrType(qrtype);
            obj.setUserId(userId);

            return obj;
        }

        /***
         * 上传支付明细
         * @param isOnline
         * @param posInfoBean
         * @param record
         */
        private void saveAndUploadPaymentItems(boolean isOnline,PosInfoBean posInfoBean,PaymentRecordEntity record) {
            Log.d(TAG,"uploadPaymentItems");

            String s1 = SpManager.getIntance().getSpString(AppIntentString.DISH_TYPE_VER);
            PaymentTotal paymentTotal = new PaymentTotal(record,s1);
            paymentTotal.setUploadFlag(PaymentTotal.UNUPLOAD);
            DBManager.getInstance().getPaymentTotalDao().save(paymentTotal);

            int n = 1;
            List<PaymentItemEntity> items = new ArrayList<>();
            for(DishEntity dishEntity : listDishes){
                PaymentItemEntity item = new PaymentItemEntity(n,paymentTotal.getId(), dishEntity);
                items.add(item);
                n++;
            }
            DBManager.getInstance().getPaymentItemEntityDao().saveInTx(items);
            Log.d(TAG,"items "+items.size());
            if(!isOnline) return;

            payWebapi obj = payWebapi.getInstance();
            if(null!=posInfoBean){
                obj.setServerIP(posInfoBean.getServerIP());
                obj.setPortNo(posInfoBean.getPortNo());

                obj.setMenuServerIP(posInfoBean.getMenuServerIP());
                obj.setMenuPortNo(posInfoBean.getMenuPortNo());
            }

            obj.uploadPaymentItems(paymentTotal);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

            missProDialog();

            //  支付成功
            if (0 == integer) {
                baseActivity.playSound(true);

                PaymentRecordEntity record = new PaymentRecordEntity(cardPaymentObj, famount, posInfoBean);

                int flag = 0x00;
                if(isOnlinePay) {
                    flag = 0x01;
                    if(isOnlineFailure) flag = 0x00;        //  在线，但失败了
                }

                saveAndUploadPaymentItems(isOnlinePay,posInfoBean,record);

                record.setUploadFlag(flag);
                DBManager.getInstance().getPaymentRecordEntityDao().save(record);

                //  打印部分
                printDishes(record);

                DetLog.writeLog(TAG, "支付成功：" + record.toString());
                if(null!=onPaymentResultListner) onPaymentResultListner.onPaymentSuccess(payMode,flag,nAmount,listDishes,record);

                //  支付成功，关闭对话框
                dismiss();

                isPaying = false;
                return;
            }

            //  支付失败，显示失败信息
            baseActivity.showToast(errString);
            if(null!=onPaymentResultListner) onPaymentResultListner.onPaymentCancel();

            dismiss();

            isPaying = false;
            return;
        }
    }

    private void printDishes(PaymentRecordEntity record){
        //  打印
        PrinterBase printer = PrinterFactory.getPrinter(mContext);
        int ret = printer.openPrinter();
        if (0 != ret) {
            baseActivity.playSound(false);
            baseActivity.showLongToast("打印机初始化失败，请检查连接");
        } else {
            //  打印
            PrinterUtils printerUtils = new PrinterUtils();
            printerUtils.printPayItem(printer, posInfoBean, record, listDishes);
            baseActivity.detSleep(100);
            printer.closePrinter();
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


    public List<DishEntity> getListDishes() {
        return listDishes;
    }

    public void setListDishes(List<DishEntity> dishes) {
        //  复制一份，避免主界面其他修改
        this.listDishes.clear();
        for(DishEntity dish:dishes){
            DishEntity entity = new DishEntity(dish);
            this.listDishes.add(entity);
        }
    }
}
