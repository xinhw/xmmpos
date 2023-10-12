package com.rankway.controller.activity.project;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.rankway.controller.R;
import com.rankway.controller.activity.BaseActivity;
import com.rankway.controller.common.AppIntentString;
import com.rankway.controller.common.BadgeView;
import com.rankway.controller.dto.PosInfoBean;
import com.rankway.controller.hardware.util.DetLog;
import com.rankway.controller.persistence.DBManager;
import com.rankway.controller.persistence.entity.PaymentRecordEntity;
import com.rankway.controller.persistence.entity.PaymentTotal;
import com.rankway.controller.persistence.entity.PersonInfoEntity;
import com.rankway.controller.persistence.entity.QrBlackListEntity;
import com.rankway.controller.persistence.entity.UserInfoEntity;
import com.rankway.controller.persistence.gen.PaymentRecordEntityDao;
import com.rankway.controller.utils.ClickUtil;
import com.rankway.controller.webapi.menu.Result;
import com.rankway.controller.webapi.payWebapi;

import java.util.Date;
import java.util.List;

public class DeskPosAuxillaryMenuActivity
        extends BaseActivity
        implements View.OnClickListener{
    final String TAG = "DeskPosAuxillaryMenuActivity";
    TextView tvProcess;

    private BadgeView bvMessage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desk_pos_auxillary_menu);

        initView();
    }

    private void initView() {
        View view = findViewById(R.id.back_img);
        view.setOnClickListener(this);

        view = findViewById(R.id.deskPosRefresh);
        view.setOnClickListener(this);

        view = findViewById(R.id.deskPosUpload);
        view.setOnClickListener(this);

        TextView tvUploadData = findViewById(R.id.tvUploadData);
        bvMessage = new BadgeView(this, tvUploadData);

        tvProcess = findViewById(R.id.tvProcess);
        tvProcess.setText("");
    }



    @Override
    protected void onResume() {
        super.onResume();
        setMessageText();
    }

    /***
     * 给消息图标上增加红数字
     */
    private void setMessageText() {
        if (null == bvMessage) return;

        List<PaymentRecordEntity> list = DBManager.getInstance().getPaymentRecordEntityDao()
                .queryBuilder()
                .where(PaymentRecordEntityDao.Properties.UploadFlag.eq(0))
                .list();
        int num = list.size();
        if (num > 0) {
            if(num>99) {
                bvMessage.setText("...");
            }else{
                bvMessage.setText(num + "");
            }
            bvMessage.setTextColor(Color.YELLOW);
            bvMessage.setTextSize(12);
            bvMessage.setBadgePosition(BadgeView.POSITION_TOP_RIGHT); //默认值
            bvMessage.setVisibility(View.VISIBLE);
            bvMessage.show();
        } else {
            bvMessage.setText("");
            bvMessage.hide();
        }
    }

    /***
     * 开启数据同步异步任务
     */
    private void startSyncDataTask(){
        AsynSyncData task = new AsynSyncData();
        task.execute();
    }

    /***
     * 开启数据上传异步任务
     */
    private void startUploadTask(){
        AsynUploadTask task = new AsynUploadTask();
        task.execute();
    }

    @Override
    public void onClick(View v) {
        if(ClickUtil.isFastDoubleClick(v.getId())){
            showToast("请勿连续点击!");
            return;
        }

        switch (v.getId()){
            case R.id.back_img:
                finish();
                break;

            case R.id.deskPosRefresh:
                startSyncDataTask();
                break;

            case R.id.deskPosUpload:
                startUploadTask();
                break;

            default:
                break;
        }
    }

    /***
     * 同步服务端信息
     */
    private class AsynSyncData extends AsyncTask<String, Integer, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProDialog("同步中,请稍等...");
        }

        @Override
        protected Integer doInBackground(String... strings) {
            int n = 0;

            payWebapi obj = payWebapi.getInstance();

            PosInfoBean posInfoBean = getPosInfoBean();
            if(null!=posInfoBean) {
                obj.setServerIP(posInfoBean.getServerIP());
                obj.setPortNo(posInfoBean.getPortNo());
            }

            //  1. 操作员信息
            sendProccessMessage("同步 操作员信息，请稍等...");
            List<UserInfoEntity> listUserInfo = obj.getUserInfoList();
            if(listUserInfo==null){
                sendProccessMessage("同步 操作员信息 失败");
            }else{
                sendProccessMessage("同步 操作员信息 成功");
                DBManager.getInstance().getUserInfoEntityDao().deleteAll();
                DBManager.getInstance().getUserInfoEntityDao().saveInTx(listUserInfo);
                n++;
            }

            //  2. 菜品种类和菜品明细（只下载上架的菜品）
            String posno = "";
            if(null!=posInfoBean) posno = posInfoBean.getCposno();
            sendProccessMessage("同步 菜品信息，请稍等...");
            Result result = obj.getDishType(posno);
            if(null==result){
                sendProccessMessage("同步 菜品信息 失败");
            }else{
                sendProccessMessage("同步 菜品信息 成功");
                saveDishType(result);
                n++;
            }

            //  3. IC卡白名单
            sendProccessMessage("同步 IC卡名单信息，请稍等...");
            List<PersonInfoEntity> listCardBlist = obj.getPersonInfoList();
            if(null==listCardBlist){
                sendProccessMessage("同步 IC卡名单信息 失败");
            }else{
                sendProccessMessage("同步 IC卡名单信息 成功");
                DBManager.getInstance().getPersonInfoEntityDao().deleteAll();
                DBManager.getInstance().getPersonInfoEntityDao().saveInTx(listCardBlist);
                n++;
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
                n++;
            }

            //  上次同步时间
            if(4==n) {
                setLongInfo(AppIntentString.LAST_SYNC_TIME, System.currentTimeMillis());
            }

            return 0;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            missProDialog();
        }
    }


    /***
     * 上传离线交易数据异步任务
     */
    private class AsynUploadTask extends AsyncTask<String, Integer, Integer> {
        int cardOfflineCount = 0,cardOfflineSuccess=0;
        int qrOfflineCount = 0,qrOfflineSuccess=0;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProDialog("上传中,请稍等...");
        }

        @Override
        protected Integer doInBackground(String... strings) {
            payWebapi obj = payWebapi.getInstance();

            PosInfoBean posInfoBean = getPosInfoBean();
            if(null!=posInfoBean) {
                obj.setServerIP(posInfoBean.getServerIP());
                obj.setPortNo(posInfoBean.getPortNo());
            }

            //  5. 未上传的记录上传
            //  5.1 未上传的IC记录
            List<PaymentRecordEntity> listCardRecord = DBManager.getInstance().getPaymentRecordEntityDao()
                    .queryBuilder()
                    .where(PaymentRecordEntityDao.Properties.UploadFlag.eq(0))
                    .where(PaymentRecordEntityDao.Properties.QrType.eq(0))
                    .list();
            if(listCardRecord.size()>0){
                cardOfflineCount = listCardRecord.size();
                Log.d(TAG,"未上传IC卡离线交易个数："+cardOfflineCount);
                cardOfflineSuccess = 0;

                int n = 0;
                sendProccessMessage("上传 IC卡离线交易，请稍等...");
                for(PaymentRecordEntity record:listCardRecord){
                    n++;
                    int ret = obj.pushOfflineCardPaymentRecords(record);
                    if(0!=ret){
                        sendProccessMessage(String.format("上传 IC卡离线交易%d/%d 失败",n,listCardRecord.size()));
                        DetLog.writeLog(TAG,"IC卡离线记录上送失败："+record.toString());
                    }else{
                        sendProccessMessage(String.format("上传 IC卡离线交易%d/%d 成功",n,listCardRecord.size()));
                        DetLog.writeLog(TAG,"IC卡离线记录上送成功："+record.toString());

                        record.setUploadFlag(PaymentTotal.UPLOADED);
                        record.setUploadTime(new Date());
                        DBManager.getInstance().getPaymentRecordEntityDao().save(record);

                        cardOfflineSuccess++;
                    }
                }
            }

            //  5.2 未上传的QR记录
            List<PaymentRecordEntity> listQrRecord = DBManager.getInstance().getPaymentRecordEntityDao()
                    .queryBuilder()
                    .where(PaymentRecordEntityDao.Properties.UploadFlag.eq(0))
                    .where(PaymentRecordEntityDao.Properties.QrType.notEq(0))
                    .list();
            if(listQrRecord.size()>0){
                qrOfflineCount = listQrRecord.size();
                Log.d(TAG,"未上传二维码离线交易个数："+qrOfflineCount);
                qrOfflineSuccess = 0;
                int n = 0;
                sendProccessMessage("上传 二维码离线交易，请稍等...");
                for(PaymentRecordEntity record:listQrRecord){
                    n++;
                    int ret = obj.pushOfflineQRPaymentRecords(record);
                    if(0!=ret){
                        sendProccessMessage(String.format("上传 二维码离线交易%d/%d 失败",n,listCardRecord.size()));
                        DetLog.writeLog(TAG,"二维码离线记录上送失败："+record.toString());
                    }else{
                        sendProccessMessage(String.format("上传 二维码离线交易%d/%d 成功",n,listCardRecord.size()));
                        DetLog.writeLog(TAG,"二维码离线记录上送成功："+record.toString());

                        record.setUploadFlag(PaymentTotal.UPLOADED);
                        record.setUploadTime(new Date());
                        DBManager.getInstance().getPaymentRecordEntityDao().save(record);

                        qrOfflineSuccess++;
                    }
                }
            }

            return 0;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            missProDialog();

            //  显示上送信息
            if((cardOfflineCount+qrOfflineCount)>0){
                int n = cardOfflineCount+qrOfflineCount;
                int m = cardOfflineSuccess+qrOfflineSuccess;

                if(m>0){
                    playSound(false);
                }else{
                    playSound(true);
                }

                String msg = String.format("共计 %d 离线交易，成功上传 %d",n,m);
                showDialogMessage("上送", msg,
                        "确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        },
                        "取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
            }

            setMessageText();
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG,"onKeyDown "+keyCode);

        //  右下角返回键
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            finish();
            return true;
        }
        if(KeyEvent.KEYCODE_HOME == keyCode){
            return true;
        }

        return super.onKeyUp(keyCode, event);
    }

}