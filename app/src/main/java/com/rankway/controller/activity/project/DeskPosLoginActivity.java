package com.rankway.controller.activity.project;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.rankway.controller.R;
import com.rankway.controller.activity.BaseActivity;
import com.rankway.controller.activity.project.manager.SpManager;
import com.rankway.controller.common.AppIntentString;
import com.rankway.controller.dto.PosInfoBean;
import com.rankway.controller.hardware.util.DetLog;
import com.rankway.controller.persistence.DBManager;
import com.rankway.controller.persistence.entity.PaymentShiftEntity;
import com.rankway.controller.persistence.entity.PersonInfoEntity;
import com.rankway.controller.persistence.entity.QrBlackListEntity;
import com.rankway.controller.persistence.entity.UserInfoEntity;
import com.rankway.controller.persistence.gen.UserInfoEntityDao;
import com.rankway.controller.printer.PrinterBase;
import com.rankway.controller.printer.PrinterFactory;
import com.rankway.controller.printer.PrinterUtils;
import com.rankway.controller.utils.ClickUtil;
import com.rankway.controller.utils.DateStringUtils;
import com.rankway.controller.webapi.menu.Result;
import com.rankway.controller.webapi.payWebapi;
import com.rankway.controller.webapi.posAudit;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class DeskPosLoginActivity
        extends BaseActivity
        implements View.OnClickListener {

    final String TAG = "DeskPosLoginActivity";

    TextView tvTitle;
    EditText etUserCode;
    EditText etPassword;
    TextView tvAppVersion;
    TextView tvProcess;
    CheckBox checkBox;

    TextView tvShiftStatus;
    PosInfoBean posInfoBean = null;
    private static PaymentShiftEntity shiftEntity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desk_pos_login);

        initView();

        initData();
    }

    private void initView() {
        Log.d(TAG, "initView");

        tvTitle = findViewById(R.id.tvTitle);
        etUserCode = findViewById(R.id.etUserCode);
        etPassword = findViewById(R.id.etPassword);
        tvAppVersion = findViewById(R.id.tvAppVersion);
        tvProcess = findViewById(R.id.tvProcess);

        TextView textView = findViewById(R.id.tvLogin);
        textView.setOnClickListener(this);
        textView = findViewById(R.id.tvExit);
        textView.setOnClickListener(this);

        etUserCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                etPassword.setText("");
            }
        });

        textView = findViewById(R.id.tvShiftOn);
        textView.setOnClickListener(this);

        textView = findViewById(R.id.tvShiftOff);
        textView.setOnClickListener(this);

        checkBox = findViewById(R.id.chkboxRemeberPIN);

        tvShiftStatus = findViewById(R.id.tvShiftStatus);
    }

    private void initData() {
        Log.d(TAG, "initData");

        //  程序版本
        int versionCode = 0;
        try {
            versionCode = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        tvAppVersion.setText("程序版本：1.0." + versionCode);

        //  上次缓存的用户名
        String str = SpManager.getIntance().getSpString(AppIntentString.LAST_LOGIN_USER);
        if (StringUtils.isEmpty(str)) {
            etUserCode.setText("");
        } else {
            etUserCode.setText(str);
        }
        str = SpManager.getIntance().getSpString(AppIntentString.LAST_LOGIN_PASSWORD);
        if (StringUtils.isEmpty(str)) {
            etPassword.setText("");
        } else {
            etPassword.setText(str);
        }
        tvProcess.setText("");

        int nDBVer = DBManager.getInstance().getDatabaseVerion();
        String nAppVer = "";
        try {
            PackageManager manager = getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            nAppVer = info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        DetLog.writeLog(TAG, "启动程序，版本号：" + nAppVer + " 数据版本号：" + nDBVer);

        versionCode = getIntInfo(AppIntentString.REMEMBER_LOGIN_PIN);
        if(0==versionCode){
            checkBox.setChecked(false);
            etPassword.setText("");
        }else{
            checkBox.setChecked(true);
        }

        initPosConfig();
    }

    /***
     * 初始化POS配置信息
     * 必须要有PosNo和UserCode
     */
    private void initPosConfig(){
        //  查看配置信息
        posInfoBean = getPosInfoBean();

        if(null==posInfoBean){
            //  信息没有被配置，直接进入到设置界面
            Intent intent = new Intent(mContext, DeskPosSettingsActivity.class);
            startActivityForResult(intent, 210);
            return;
        }

        //  PosNo或UserCode不能是空
        if(StringUtils.isEmpty(posInfoBean.getCposno())
                ||StringUtils.isEmpty(posInfoBean.getUsercode())){
            //  信息没有被配置，直接进入到设置界面
            Intent intent = new Intent(mContext, DeskPosSettingsActivity.class);
            startActivityForResult(intent, 210);
        }

        //  信息如果被配置，进入到数据同步界面
        beginSyncDataTask();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, String.format("requestCode=%d,resultCode=%d", requestCode, resultCode));
        if (requestCode == 210) {
            //  信息同步
            initPosConfig();
        }
    }


    @Override
    protected void onResume(){
        super.onResume();
        tvTitle.requestFocus();
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


    @Override
    public void onClick(View v) {
        if (ClickUtil.isFastDoubleClick(v.getId())) {
            showToast("请勿连续点击!");
            return;
        }

        switch (v.getId()) {
            case R.id.tvLogin:
                if (!verifyUserCode()) {
                    playSound(false);
                    break;
                }

                //  判断是否已经结班
                if(shiftEntity==null) break;
                if(shiftEntity.getStatus()!=PaymentShiftEntity.SHIFT_STATUS_ON){
                    playSound(false);
                    showToast("请先开班");
                    break;
                }

                playSound(true);
                startActivity(DeskPosPayMainActivity.class);
                break;

            case R.id.tvExit:
                finishPrompt();
                break;

            case R.id.tvShiftOn:
                if (!verifyUserCode()) {
                    playSound(false);
                    break;
                }

                shiftOn();
                break;

            case R.id.tvShiftOff:
                if (!verifyUserCode()) {
                    playSound(false);
                    break;
                }

                shiftOff();
                break;

            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        DetLog.writeLog(TAG,"onDestory 退出程序");

        detSleep(100);

        System.exit(0);
    }

    /***
     * 验证操作员密码
     * @return  true        验证成功
     *          false       验证失败
     */
    private boolean verifyUserCode() {
        String struserid = etUserCode.getText().toString().trim();
        if (StringUtils.isEmpty(struserid)) {
            showToast("请输入操作员代码");
            return false;
        }
        String strpassword = etPassword.getText().toString();

        //  超级用户
        if ((struserid.equalsIgnoreCase("99521"))
                && (isAdvancedPasswordRight(strpassword))) {
            DetLog.writeLog(TAG, "Admin用户登录");
            return true;
        }

        //  普通用户
        UserInfoEntity user = DBManager.getInstance().getUserInfoEntityDao()
                .queryBuilder()
                .where(UserInfoEntityDao.Properties.UserCode.eq(struserid))
                .unique();
        if (null == user) {
            showToast("操作员代码或密码错误");
            return false;
        }

        if (user.getUserPassword().equalsIgnoreCase(strpassword)) {
            //  缓存登录用户名和密码
            SpManager.getIntance().saveSpString(AppIntentString.LAST_LOGIN_USER,
                    etUserCode.getText().toString().trim());
            SpManager.getIntance().saveSpString(AppIntentString.LAST_LOGIN_PASSWORD,
                    etPassword.getText().toString().trim());

            if(checkBox.isChecked()){
                setIntInfo(AppIntentString.REMEMBER_LOGIN_PIN,1);
            }else{
                setIntInfo(AppIntentString.REMEMBER_LOGIN_PIN,0);
            }

            return true;
        }
        showToast("操作员代码或密码错误");
        return false;
    }

    /***
     * 开启数据同步任务
     */
    private void beginSyncDataTask() {
        AsynSyncData task = new AsynSyncData();
        task.execute();
    }

    /***
     * 后台同步数据的异步任务
     * 		1.操作员信息
     * 		2.菜品种类和菜品明细（只下载上架的菜品）
     * 		3.IC卡黑名单
     * 		4.二维码黑名单
     * 	    5.上传离线交易
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
            sendProccessMessage("同步 POS流水信息，请稍等...");
            if (null != posInfoBean) {
                obj.setServerIP(posInfoBean.getServerIP());
                obj.setPortNo(posInfoBean.getPortNo());

                obj.setMenuServerIP(posInfoBean.getMenuServerIP());
                obj.setMenuPortNo(posInfoBean.getMenuPortNo());

                //  获取POS机流水号
                posAudit audit = obj.getPosAuditNo(posInfoBean.getCposno());
                if (null != audit) {
                    //  设置POS流水号
                    posInfoBean.setAuditNo(audit.getPosCno());
                    posInfoBean.setPosName(audit.getPosName());

                    sendProccessMessage("同步 POS流水信息 成功");

                    savePosInfoBean(posInfoBean);
                }else{
                    sendProccessMessage("同步 POS流水信息 失败");
                }
            }

            //  1. 操作员信息
            sendProccessMessage("同步 操作员信息，请稍等...");
            List<UserInfoEntity> listUserInfo = obj.getUserInfoList();
            if (listUserInfo == null) {
                sendProccessMessage("同步 操作员信息 失败");
            } else {
                sendProccessMessage("同步 操作员信息 成功");
                Log.d(TAG, "操作员个数：" + listUserInfo.size());
                if(listUserInfo.size()>0) {
                    DBManager.getInstance().getUserInfoEntityDao().deleteAll();
                    DBManager.getInstance().getUserInfoEntityDao().saveInTx(listUserInfo);
                }
                n++;
            }

            //  2. 菜品种类和菜品明细（只下载上架的菜品）
            String posno = "";
            if (null != posInfoBean) posno = posInfoBean.getCposno();
            sendProccessMessage("同步 菜品信息，请稍等...");
            Result result = obj.getDishType(posno);
            if (null == result) {
                sendProccessMessage("同步 菜品信息 失败");
            } else {
                sendProccessMessage("同步 菜品信息 成功");
                saveDishType(result);
                n++;
            }

            //  3. IC卡白名单
            sendProccessMessage("同步 IC卡白名单信息，请稍等...");
            List<PersonInfoEntity> listCardBlist = obj.getPersonInfoList();
            if (null == listCardBlist) {
                sendProccessMessage("同步 IC卡白名单信息 失败");
            } else {
                //  将卡序列号全部大写
                for(PersonInfoEntity pie : listCardBlist) pie.setGsno(pie.getGsno().toUpperCase());

                sendProccessMessage("同步 IC卡白名单信息 成功");
                Log.d(TAG, "IC卡白名单个数：" + listCardBlist.size());
                DBManager.getInstance().getPersonInfoEntityDao().deleteAll();
                DBManager.getInstance().getPersonInfoEntityDao().saveInTx(listCardBlist);
                n++;
            }

            //  4. 二维码黑名单
            sendProccessMessage("同步 二维码黑名单信息，请稍等...");
            List<QrBlackListEntity> listQrBlacklist = obj.getQrBlackList();
            if (null == listQrBlacklist) {
                sendProccessMessage("同步 二维码黑名单信息 失败");
            } else {
                sendProccessMessage("同步 二维码黑名单信息 成功");
                Log.d(TAG, "二维码黑名单个数：" + listQrBlacklist.size());
                DBManager.getInstance().getQrBlackListEntityDao().deleteAll();
                DBManager.getInstance().getQrBlackListEntityDao().saveInTx(listQrBlacklist);
                n++;
            }

            //  5. 上传离线交易
            sendProccessMessage("上传 离线交易，请稍等...");
            uploadPaymentRecords();
            sendProccessMessage("上传 离线交易 完成");

            //  6. 上传已经未上传的结班记录
            uploadShiftRecords();

            //  7. 上传支付明细
            uploadPaymentItems();

            //  上次同步时间
            if (4 == n) {
                sendProccessMessage("同步 完成");
                setLongInfo(AppIntentString.LAST_SYNC_TIME, System.currentTimeMillis());
            }

            //  清除日志文件
            zapLogFile();

            //  清除数据库
            zapDatabase();

            return 0;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            missProDialog();

            //  刷新班次新信息
            long shiftId = getLongInfo(AppIntentString.PAYMENT_SHIFT_ID);
            if(shiftId<=0){
                shiftEntity = new PaymentShiftEntity();
                savePaymentShiftEntity(shiftEntity);
                setLongInfo(AppIntentString.PAYMENT_SHIFT_ID,shiftEntity.getId());
            }else{
                shiftEntity = getPaymentShiftEntity(shiftId);
            }
            Log.d(TAG,"PaymentShiftEntity:"+shiftEntity.toString());

            refreshShiftStatus();
        }
    }

    private void sendProccessMessage(String msg) {
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
            switch (msg.what) {
                case 100:       //  显示对话框信息
                    String str = (String) msg.obj;
                    if (null != str) {
                        setProDialogText(str);
                        tvProcess.setText(str);
                    }
                    break;

                default:
                    break;
            }
        }
    };


    public static PaymentShiftEntity getShiftEntity(){
        return shiftEntity;
    }

    /***
     * 询问是否退出APP
     */
    private void finishPrompt() {
        showDialogMessage(null, "是否要退出APP？",
                "确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DetLog.writeLog(TAG,"退出程序");
                        finish();
                    }
                },
                "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.v(TAG, "取消退出！");
                        dialog.dismiss();
                    }
                });
        return;
    }

    /***
     * 开班
     */
    private void shiftOn(){
        //  判断是否已经开班
        if(shiftEntity==null) return;
        if(shiftEntity.getStatus()==PaymentShiftEntity.SHIFT_STATUS_ON){
            playSound(false);
            showToast("已经开班");
            return;
        }

        shiftEntity = new PaymentShiftEntity();

        shiftEntity.setSubCardCount(0);
        shiftEntity.setSubCardAmount(0);

        shiftEntity.setSubQrCount(0);
        shiftEntity.setSubQrAmount(0);

        if(null!=posInfoBean){
            shiftEntity.setShiftOnAuditNo(posInfoBean.getAuditNo());
        }
        shiftEntity.setShiftOnTime(System.currentTimeMillis());

        shiftEntity.setPosNo(posInfoBean.getCposno());
        shiftEntity.setOperatorNo(etUserCode.getText().toString());

        shiftEntity.setStatus(PaymentShiftEntity.SHIFT_STATUS_ON);

        //  班次号
        shiftEntity.setShiftNo(DateStringUtils.getYYMMDDHHMMss(shiftEntity.getShiftOnTime())+shiftEntity.getShiftOnAuditNo());

        savePaymentShiftEntity(shiftEntity);

        setLongInfo(AppIntentString.PAYMENT_SHIFT_ID,shiftEntity.getId());

        refreshShiftStatus();

        payWebapi obj = payWebapi.getInstance();
        obj.uploadShiftOn(shiftEntity);

        DetLog.writeLog(TAG,"开班："+shiftEntity.toString());
    }

    /***
     * 结班和打印
     */
    private void shiftOff(){
        //  判断是否已经结班
        if(shiftEntity==null) return;

        if(shiftEntity.getStatus()!=PaymentShiftEntity.SHIFT_STATUS_ON) {
            playSound(false);
            //  已经结班，是否要重新打印
            showDialogMessage("结班", "已经结班，是否要重新打印？",
                    "确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //  报表时间
                            shiftEntity.setReportTime(System.currentTimeMillis());
                            DetLog.writeLog(TAG,"已经结班，重新打印："+shiftEntity.toString());
                            printShiftOffEntity(shiftEntity);
                        }
                    },
                    "取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            return;
        }

        //  签退流水
        if (null != posInfoBean) {
            shiftEntity.setShiftOffAuditNo(posInfoBean.getAuditNo());
        }
        //  结班时间
        shiftEntity.setShiftOffTime(System.currentTimeMillis());

        //  结班状态
        shiftEntity.setStatus(PaymentShiftEntity.SHIFT_STATUS_OFF);

        savePaymentShiftEntity(shiftEntity);

        //  报表时间
        shiftEntity.setReportTime(System.currentTimeMillis());

        refreshShiftStatus();

        DetLog.writeLog(TAG,"结班："+shiftEntity.toString());

//        if(shiftEntity.getTotalCount()==0){
//            Log.d(TAG,"记录数位0，无需打印");
//            return;
//        }

        //  打印结班报表
        printShiftOffEntity(shiftEntity);

        //  上传结班记录，未上传陈宫纳入上传离线记录中
        payWebapi obj = payWebapi.getInstance();
        obj.uploadShiftOff(shiftEntity);

        playSound(true);

        //  上传离线数据
        AsynPushOfflineData task = new AsynPushOfflineData();
        task.execute();

        return;
    }

    /***
     * 打印结班记录
     * @param shiftEntity
     */
    private void printShiftOffEntity(PaymentShiftEntity shiftEntity){
        //  打印部分
        PrinterBase printer = PrinterFactory.getPrinter(mContext);
        int ret = printer.openPrinter();
        if (0 != ret) {
            playSound(false);
            showLongToast("打印机初始化失败，请检查连接");
        } else {
            PrinterUtils printerUtils = new PrinterUtils();
            printerUtils.printShiftSettle(printer, shiftEntity);
            printer.closePrinter();

            PrinterFactory.setPrinter(null);
        }
    }

    /***
     * 刷新班次状态
     */
    private void refreshShiftStatus(){
        Log.d(TAG,"refreshShiftStatus");
        if(shiftEntity.getStatus()==PaymentShiftEntity.SHIFT_STATUS_ON){
            tvShiftStatus.setText("当前状态：已开班");
        }else{
            tvShiftStatus.setText("当前状态：未开班");
        }
    }


    /***
     * 上传离线记录
     * 	    1.上传离线交易
     * 	    2.上传已结班未上传记录
     * 	    3.上传支付明细
     */
    private class AsynPushOfflineData extends AsyncTask<String, Integer, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProDialog("请稍等...");
        }

        @Override
        protected Integer doInBackground(String... strings) {

            //  1. 上传离线交易
            uploadPaymentRecords();

            //  2. 上传已经未上传的结班记录
            uploadShiftRecords();

            //  3. 上传支付明细
            uploadPaymentItems();

            return 0;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            missProDialog();
        }
    }
}