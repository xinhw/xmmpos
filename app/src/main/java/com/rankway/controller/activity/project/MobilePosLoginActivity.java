package com.rankway.controller.activity.project;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.rankway.controller.entity.PosInfoBean;
import com.rankway.controller.hardware.util.DetLog;
import com.rankway.controller.persistence.entity.PaymentShiftEntity;
import com.rankway.controller.printer.PrinterBase;
import com.rankway.controller.printer.PrinterFactory;
import com.rankway.controller.printer.PrinterUtils;
import com.rankway.controller.utils.DateStringUtils;
import com.rankway.controller.webapi.payWebapi;

import org.apache.commons.lang3.StringUtils;

public class MobilePosLoginActivity
        extends BaseActivity
        implements View.OnClickListener {
    private final String TAG = "MobilePosLoginActivity";

    private TextView tvShiftStatus;
    private TextView tvPosNo;
    private EditText etUserCode;
    private EditText etPassword;

    private CheckBox checkBox;

    PosInfoBean posInfoBean = null;
    private static PaymentShiftEntity shiftEntity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_pos_login);

        initView();

        initData();
    }

    private void initData() {

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

        int versionCode = getIntInfo(AppIntentString.REMEMBER_LOGIN_PIN);
        if(0==versionCode){
            checkBox.setChecked(false);
            etPassword.setText("");
        }else{
            checkBox.setChecked(true);
        }

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

        initPosConfig();

    }

    private void initView() {
        Log.d(TAG,"initView");

        int[] ids = {R.id.tvShiftOn, R.id.tvShiftOff, R.id.tvLogin};
        setOnClickListener(ids);

        tvShiftStatus = findViewById(R.id.tvShiftStatus);
        tvPosNo = findViewById(R.id.tvPosNo);
        etUserCode = findViewById(R.id.etUserCode);
        etPassword = findViewById(R.id.etPassword);

        checkBox = findViewById(R.id.chkboxRemeberPIN);

    }

    private void setOnClickListener(int[] ids) {
        Log.d(TAG, "setOnClickListener");
        for (int id : ids) {
            View view = findViewById(id);
            if (null != view) view.setOnClickListener(this);
        }
        return;
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(null!=posInfoBean){
            tvPosNo.setText(String.format("设备号：%s",posInfoBean.getCposno()));
        }
        refreshShiftStatus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        DetLog.writeLog(TAG,"onDestory 退出程序");

        detSleep(100);

        System.exit(0);
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
            Intent intent = new Intent(mContext, MobilePosSettingsActivity.class);
            startActivityForResult(intent, 210);
            return;
        }

        //  PosNo或UserCode不能是空
        if(StringUtils.isEmpty(posInfoBean.getCposno())
                ||StringUtils.isEmpty(posInfoBean.getUsercode())){
            //  信息没有被配置，直接进入到设置界面
            Intent intent = new Intent(mContext, MobilePosSettingsActivity.class);
            startActivityForResult(intent, 210);
        }

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
    public void onClick(View view) {
        switch (view.getId()) {
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
                startActivity(MobilePosPayMainActivity.class);
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "keyCode:" + keyCode);

        //  右下角返回键
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            finishPrompt();
            return true;
        }
        if (KeyEvent.KEYCODE_HOME == keyCode) {
            return true;
        }

        return super.onKeyDown(keyCode, event);
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

//        //  普通用户
//        UserInfoEntity user = DBManager.getInstance().getUserInfoEntityDao()
//                .queryBuilder()
//                .where(UserInfoEntityDao.Properties.UserCode.eq(struserid))
//                .unique();
//        if (null == user) {
//            showToast("操作员代码或密码错误");
//            return false;
//        }
//
//        if (user.getUserPassword().equalsIgnoreCase(strpassword)) {
//            //  缓存登录用户名和密码
//            SpManager.getIntance().saveSpString(AppIntentString.LAST_LOGIN_USER,
//                    etUserCode.getText().toString().trim());
//            SpManager.getIntance().saveSpString(AppIntentString.LAST_LOGIN_PASSWORD,
//                    etPassword.getText().toString().trim());
//
//            if(checkBox.isChecked()){
//                setIntInfo(AppIntentString.REMEMBER_LOGIN_PIN,1);
//            }else{
//                setIntInfo(AppIntentString.REMEMBER_LOGIN_PIN,0);
//            }
//
//            return true;
//        }
//        showToast("操作员代码或密码错误");
        return true;
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
            printerUtils.printShiftSettle(printer,shiftEntity);

            printer.closePrinter();
        }
    }

    public static PaymentShiftEntity getShiftEntry(){
        return shiftEntity;
    }
}