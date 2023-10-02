package com.rankway.controller.activity.project;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.rankway.controller.R;
import com.rankway.controller.activity.BaseActivity;
import com.rankway.controller.activity.project.manager.SpManager;
import com.rankway.controller.common.AppIntentString;
import com.rankway.controller.hardware.util.DetLog;
import com.rankway.controller.persistence.DBManager;
import com.rankway.controller.persistence.entity.CardBlackListEntity;
import com.rankway.controller.persistence.entity.PaymentRecordEntity;
import com.rankway.controller.persistence.entity.QrBlackListEntity;
import com.rankway.controller.persistence.entity.UserInfoEntity;
import com.rankway.controller.persistence.gen.PaymentRecordEntityDao;
import com.rankway.controller.persistence.gen.UserInfoEntityDao;
import com.rankway.controller.utils.ClickUtil;
import com.rankway.controller.webapi.payWebapi;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;

public class DeskPosLoginActivity
        extends BaseActivity
        implements View.OnClickListener{

    final String TAG = "DeskPosLoginActivity";

    TextView tvTitle;
    EditText etUserCode;
    EditText etPassword;
    TextView tvAppVersion;
    TextView tvProcess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desk_pos_login);

        initView();

        initData();

        DetLog.writeLog(TAG,"程序启动");
    }

    private void initView() {
        Log.d(TAG,"initView");

        tvTitle = findViewById(R.id.tvTitle);
        etUserCode = findViewById(R.id.etUserCode);
        etPassword = findViewById(R.id.etPassword);
        tvAppVersion = findViewById(R.id.tvAppVersion);
        tvProcess = findViewById(R.id.tvProcess);

        TextView textView = findViewById(R.id.tvLogin);
        textView.setOnClickListener(this);
        textView = findViewById(R.id.tvExit);
        textView.setOnClickListener(this);
    }

    private void initData() {
        Log.d(TAG,"initData");

        //  程序版本
        int versionCode = 0;
        try{
            versionCode = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(),0).versionCode;
        }catch (Exception e){
            e.printStackTrace();
        }
        tvAppVersion.setText(String.format("程序版本：1.0."+versionCode));

        //  上次缓存的用户名
        String str = SpManager.getIntance().getSpString(AppIntentString.LAST_LOGIN_USER);
        if(StringUtils.isEmpty(str)){
            etUserCode.setText("");
        }else{
            etUserCode.setText(str);
        }

        tvProcess.setText("");

        beginSyncDataTask();
    }

    @Override
    public void onClick(View v) {
        if(ClickUtil.isFastDoubleClick(v.getId())){
            showToast("请勿连续点击!");
            return;
        }

        switch (v.getId()){
            case R.id.tvLogin:
                if(!verifyUserCode()) {
                    playSound(false);
                    break;
                }

                playSound(true);
                startActivity(DeskPosPayMainActivity.class);
                finish();

                break;

            case R.id.tvExit:
                finish();
                break;

            default:
                break;
        }
    }

    /***
     * 验证操作员密码
     * @return
     */
    private boolean verifyUserCode(){
        String struserid = etUserCode.getText().toString().trim();
        if(StringUtils.isEmpty(struserid)){
            showToast("请输入操作员代码");
            return false;
        }
        String strpassword = etPassword.getText().toString();

        UserInfoEntity user = DBManager.getInstance().getUserInfoEntityDao()
                .queryBuilder()
                .where(UserInfoEntityDao.Properties.UserCode.eq(struserid))
                .unique();
        if(null==user){
            showToast("操作员代码或密码错误");
            return false;
        }

        if(user.getUserPassword().equalsIgnoreCase(strpassword)){
            return true;
        }
        showToast("操作员代码或密码错误");
        return false;
    }

    /***
     * 开启数据同步任务
     */
    private void beginSyncDataTask(){
        AsynSyncData task = new AsynSyncData();
        task.execute();
    }


    /***
     * 后台同步数据的异步任务
     * 		操作员信息
     * 		菜品种类和菜品明细（只下载上架的菜品）
     * 		IC卡黑名单
     * 		二维码黑名单
     * 		未上传的记录上传
     */
    private class AsynSyncData extends AsyncTask<String, Integer, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProDialog("同步中,请稍等...");
        }

        @Override
        protected Integer doInBackground(String... strings) {
            payWebapi obj = payWebapi.getInstance();
            //  1. 操作员信息
            sendProccessMessage("同步 操作员信息，请稍等...");
            List<UserInfoEntity> listUserInfo = obj.getUserInfoList();
            if(listUserInfo==null){
                sendProccessMessage("同步 操作员信息 失败");
            }else{
                sendProccessMessage("同步 操作员信息 成功");
                DBManager.getInstance().getUserInfoEntityDao().deleteAll();
                DBManager.getInstance().getUserInfoEntityDao().saveInTx(listUserInfo);
            }

            //  2. 菜品种类和菜品明细（只下载上架的菜品）

            //  3. IC卡黑名单
            sendProccessMessage("同步 IC卡黑名单信息，请稍等...");
            List<CardBlackListEntity> listCardBlacklist = obj.getCardBlackList();
            if(null==listCardBlacklist){
                sendProccessMessage("同步 IC卡黑名单信息 失败");
            }else{
                sendProccessMessage("同步 IC卡黑名单信息 成功");
                DBManager.getInstance().getCardBlackListEntityDao().deleteAll();
                DBManager.getInstance().getCardBlackListEntityDao().saveInTx(listCardBlacklist);
            }

            //  4. 二维码黑名单
            sendProccessMessage("同步 二维码黑名单信息，请稍等...");
            List<QrBlackListEntity> listQrBlacklist = obj.getQrBlackList();
            if(null==listQrBlacklist){
                sendProccessMessage("同步 二维码黑名单信息 失败");
            }else{
                sendProccessMessage("同步 二维码黑名单信息 成功");
                DBManager.getInstance().getQrBlackListEntityDao().deleteAll();
                DBManager.getInstance().getQrBlackListEntityDao().saveInTx(listQrBlacklist);
            }

            //  5. 未上传的记录上传
            //  5.1 未上传的IC记录
            List<PaymentRecordEntity> listCardRecord = DBManager.getInstance().getPaymentRecordEntityDao()
                    .queryBuilder()
                    .where(PaymentRecordEntityDao.Properties.UploadFlag.eq(0))
                    .where(PaymentRecordEntityDao.Properties.QrType.eq(0))
                    .list();
            if(listCardRecord.size()>0){
                sendProccessMessage("上传 IC卡离线交易，请稍等...");
                int ret = obj.pushOfflineCardPaymentRecords(listCardRecord);
                if(0!=ret){
                    sendProccessMessage("上传 IC卡离线交易 失败");
                }else{
                    sendProccessMessage("上传 IC卡离线交易 成功");
                    for(PaymentRecordEntity record:listCardRecord){
                        record.setUploadFlag(0);
                        record.setUploadTime(new Date());
                    }
                    DBManager.getInstance().getPaymentRecordEntityDao().saveInTx(listCardRecord);
                }
            }


            //  5.2 未上传的QR记录
            List<PaymentRecordEntity> listQrRecord = DBManager.getInstance().getPaymentRecordEntityDao()
                    .queryBuilder()
                    .where(PaymentRecordEntityDao.Properties.UploadFlag.eq(0))
                    .where(PaymentRecordEntityDao.Properties.QrType.notEq(0))
                    .list();
            if(listQrRecord.size()>0){
                sendProccessMessage("上传 二维码离线交易，请稍等...");
                int ret = obj.pushOfflineQRPaymentRecords(listQrRecord);
                if(0!=ret){
                    sendProccessMessage("上传 二维码离线交易 失败");
                }else{
                    sendProccessMessage("上传 二维码离线交易 成功");
                    for(PaymentRecordEntity record:listQrRecord){
                        record.setUploadFlag(0);
                        record.setUploadTime(new Date());
                    }
                    DBManager.getInstance().getPaymentRecordEntityDao().saveInTx(listQrRecord);
                }
            }

            return 0;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            missProDialog();
        }
    }


    private void sendProccessMessage(String msg){
        Message message = new Message();
        message.what = 100;
        message.obj = msg;
        mHandler.sendMessage(message);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (null == msg) return;
            switch (msg.what){
                case 100:       //  显示对话框信息
                    String str = (String)msg.obj;
                    if(null!=str){
                        setProDialogText(str);
                        tvProcess.setText(str);
                    }
                    break;

                default:
                    break;
            }
        }
    };

}