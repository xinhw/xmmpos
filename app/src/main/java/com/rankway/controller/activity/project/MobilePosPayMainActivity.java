package com.rankway.controller.activity.project;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.rankway.controller.R;
import com.rankway.controller.activity.BaseActivity;
import com.rankway.controller.adapter.MobilePosPayRecordDetailAdapter;
import com.rankway.controller.dto.PosInfoBean;
import com.rankway.controller.hardware.util.DataConverter;
import com.rankway.controller.hardware.util.DetLog;
import com.rankway.controller.persistence.DBManager;
import com.rankway.controller.persistence.entity.PaymentRecord;
import com.rankway.controller.persistence.gen.PaymentRecordDao;
import com.rankway.controller.scan.ScannerBase;
import com.rankway.controller.scan.ScannerFactory;
import com.rankway.controller.utils.DateStringUtils;
import com.rankway.controller.webapi.cardInfo;
import com.rankway.controller.webapi.decodeQRCode;
import com.rankway.controller.webapi.payWebapi;
import com.rankway.controller.webapi.posAudit;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MobilePosPayMainActivity
        extends BaseActivity
        implements View.OnClickListener, MobilePosPayRecordDetailAdapter.OnItemClickListener {
    private final String TAG = "MobilePosPayMainActivity";

    NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;

    private ScannerBase scanner;
    private static final String RES_ACTION = "android.intent.action.SCANRESULT";
    private ScannerResultReceiver scanReceiver;

    TextView tvLocalTime;
    TextView tvTotalCount;
    TextView tvTotalAmount;

    TextView tvName;
    TextView tvWorkNo;
    TextView tvRemain;
    EditText etAmount;

    boolean isPaying = false;           //  是否在查询or支付中
    TextView tvPayment;

    final int MAX_IDLE_TIME_MS = 60 * 1000;       //  最大未支付时间（毫秒）

    long lastQueryTime = 0;
    private cardInfo cardPaymentObj = null;

    private final int PAYMENT_TYPE_CARD = 0;
    private final int PAYMENT_TYPE_QRCODE = 1;

    private PosInfoBean posInfoBean = null;

    List<PaymentRecord> listRecords = new ArrayList<>();    //  今日所有消费记录
    MobilePosPayRecordDetailAdapter adapter;
    RecyclerView recyclerView;

    private int totalCount = 0;
    private double totalAmount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_payment);

        initView();

        initData();

        initNFCAdapter();

        initScanner();

        mHandler.sendEmptyMessageDelayed(100, 1000);

        checkAppUpdate();

        DetLog.writeLog(TAG, "启动程序，版本号："
                + getVersionCode()
                + " 数据版本号："
                + DBManager.getInstance().getDatabaseVerion());
    }

    private void initView() {
        int[] ids = {R.id.back_img, R.id.imgSetting, R.id.btnPay};
        setOnClickListener(ids);

        tvLocalTime = findViewById(R.id.handsetime);
        tvTotalCount = findViewById(R.id.totalCount);
        tvTotalCount.setText("0");
        tvTotalAmount = findViewById(R.id.totalAmount);
        tvTotalAmount.setText("0.00");

        tvName = findViewById(R.id.tvName);
        tvName.setText("");
        tvWorkNo = findViewById(R.id.tvWorkNo);
        tvWorkNo.setText("");
        tvRemain = findViewById(R.id.tvRemain);
        tvRemain.setText("");

        etAmount = findViewById(R.id.etAmount);
        etAmount.setText("");

        tvPayment = findViewById(R.id.btnPay);
        tvPayment.setVisibility(View.GONE);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setOnClickListener(int[] ids) {
        Log.d(TAG, "setOnClickListener");
        for (int id : ids) {
            View view = findViewById(id);
            if (null != view) view.setOnClickListener(this);
        }
        return;
    }


    private void initData() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MobilePosPayRecordDetailAdapter(mContext, listRecords);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // NFC处理部分
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void initNFCAdapter() {
        // 获取默认的NFC控制器
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (nfcAdapter == null) {
            Toast.makeText(this, "对不起,您的设备不支持nfc功能！", Toast.LENGTH_SHORT).show();
            //promt.setText("设备不支持NFC！");
            finish();
            return;
        }
        if (!nfcAdapter.isEnabled()) {
            Toast.makeText(this, "请在系统设置中开启NFC功能！", Toast.LENGTH_SHORT).show();
            //promt.setText("请在系统设置中先启用NFC功能！");
            finish();
            return;
        }

        pendingIntent = PendingIntent.getActivity(this,
                0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "onNewIntent");

        super.onNewIntent(intent);
        // 当前app正在前端界面运行，这个时候有intent发送过来，那么系统就会调用onNewIntent回调方法，将intent传送过来
        // 我们只需要在这里检验这个intent是否是NFC相关的intent，如果是，就调用处理方法
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            processIntent(intent);
        }
    }

    /**
     * Parses the NDEF Message from the intent and prints to the TextView
     */
    private void processIntent(Intent intent) {
        Log.d(TAG, "processIntent");

        //取出封装在intent中的TAG
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        String CardId = DataConverter.bytes2HexString(tagFromIntent.getId());
        String metaInfo = "";
        metaInfo += "卡片ID:" + CardId;
        for (String tech : tagFromIntent.getTechList()) {
            Log.d(TAG, tech);
        }
        boolean auth = false;

        //读取TAG
        MifareClassic mfc = MifareClassic.get(tagFromIntent);

        try {
            //Enable I/O operations to the tag from this TagTechnology object.
            mfc.connect();
            int type = mfc.getType();//获取TAG的类型
            int sectorCount = mfc.getSectorCount();//获取TAG中包含的扇区数
            String typeS = "";
            switch (type) {
                case MifareClassic.TYPE_CLASSIC:
                    typeS = "TYPE_CLASSIC";
                    break;
                case MifareClassic.TYPE_PLUS:
                    typeS = "TYPE_PLUS";
                    break;
                case MifareClassic.TYPE_PRO:
                    typeS = "TYPE_PRO";
                    break;
                case MifareClassic.TYPE_UNKNOWN:
                    typeS = "TYPE_UNKNOWN";
                    break;
            }
            metaInfo += "\n卡片类型：" + typeS + "\n共" + sectorCount + "个扇区\n共"
                    + mfc.getBlockCount() + "个块\n存储空间: " + mfc.getSize() + "B\n";
            Log.d(TAG, metaInfo);

//            for (int j = 0; j < sectorCount; j++) {
//                //Authenticate a sector with key A.
//                auth = mfc.authenticateSectorWithKeyA(j,
//                        MifareClassic.KEY_DEFAULT);
//                int bCount;
//                int bIndex;
//                if (auth) {
//                    metaInfo += "Sector " + j + ":验证成功\n";
//                    // 读取扇区中的块
//                    bCount = mfc.getBlockCountInSector(j);
//                    bIndex = mfc.sectorToBlock(j);
//                    for (int i = 0; i < bCount; i++) {
//                        byte[] data = mfc.readBlock(bIndex);
//                        metaInfo += "Block " + bIndex + " : "
//                                + DataConverter.bytes2HexString(data) + "\n";
//                        bIndex++;
//                    }
//                } else {
//                    metaInfo += "Sector " + j + ":验证失败\n";
//                }
//            }

            cardPaymentObj = new cardInfo();
            cardPaymentObj.setGsno(CardId);

            startQuery(PAYMENT_TYPE_CARD);
//            promt.setText(metaInfo);
            //Toast.makeText(this, metaInfo, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //  扫描仪部分
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void initScanner() {
        Log.d(TAG, "initScanner");
        enableScanner();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RES_ACTION);

        //注册广播接受者
        scanReceiver = new ScannerResultReceiver();
        registerReceiver(scanReceiver, intentFilter);

        LocalBroadcastManager.getInstance(this).registerReceiver(scanReceiver, intentFilter);
    }

    //  启用扫描
    private void enableScanner() {
        Log.d(TAG,"enableScanner");

        scanner = ScannerFactory.getScannerObject(this);
        if (null == scanner) return;

        scanner.open();
        scanner.lockScanKey();
        scanner.setOutputMode(1);

        //  扫描失败是否发送广播
        scanner.SetErrorBroadCast(false);
    }

    //  禁止扫描
    private void disableScanner() {
        Log.d(TAG, "disableScanner");

        if (null == scanner) return;

        scanner.unlockScanKey();
        scanner.setOutputMode(0);
        scanner.close();
    }

    @Override
    public void onItemClick(View view, int position) {
        Log.d(TAG, "onItemClick " + position);
        adapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(position);
    }

    @Override
    public void onItemLongClick(View view, int position) {
        Log.d(TAG, "onItemLongClick " + position);
        adapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(position);
    }

    @Override
    public void onItemDoubleClick(View view, int position) {
        Log.d(TAG, "onItemDoubleClick " + position);
        showPaymentRecordDialog(0, listRecords.get(position));
    }

    /**
     * 扫描结果广播接收
     */
    //*********重要
    private class ScannerResultReceiver extends BroadcastReceiver {
        public synchronized void onReceive(Context context, Intent intent) {
            Log.d(TAG, "intent.getAction()-->" + intent.getAction());//

            //*******重要，注意Extral为"value"
            final String scanResult = intent.getStringExtra("value");
            Log.d(TAG, "onReceive: scanResult = " + scanResult);

            if (null == scanResult) return;
            if (scanResult.length() == 0) return;

            cardPaymentObj = decodeQRCode.decode(scanResult);
            if (null == cardPaymentObj) {
                playSound(false);
                showLongToast("无效的二维码");
                return;
            }

            startQuery(PAYMENT_TYPE_QRCODE);

            return;
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, String.format("requestCode=%d,resultCode=%d", requestCode, resultCode));
        if (requestCode == 210) {
        }
    }

    //页面获取焦点
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");

        posInfoBean = getPosInfoBean();
        if (null == posInfoBean) {
            Log.d(TAG, "第一次使用，需要配置参数");
            Intent intent = new Intent(mContext, MobilePosSettingsActivity.class);
            startActivityForResult(intent, 210);
            return;
        }
        if(StringUtils.isEmpty(posInfoBean.getCposno())||StringUtils.isEmpty(posInfoBean.getUsercode())){
            Log.d(TAG, "第一次使用，需要配置参数");
            Intent intent = new Intent(mContext, MobilePosSettingsActivity.class);
            startActivityForResult(intent, 210);
            return;
        }

        enableScanner();
        if (null != nfcAdapter) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
        }

        getTodayRecords();

        refreshStatistics(totalCount, totalAmount);

        adapter.notifyDataSetChanged();
    }

    //页面失去焦点
    @Override
    protected void onPause() {
        super.onPause();

        disableScanner();

        if (nfcAdapter != null) {
            Log.d(TAG, "disableForegroundDispatch");
            nfcAdapter.disableForegroundDispatch(this);//关闭前台发布系统
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        disableScanner();

        System.exit(0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_img:
                finishPrompt();
                break;

            case R.id.imgSetting:
                if (!isPaying) {
                    startActivity(MobilePosPaySettingMenuActivity.class);
                }
                break;

            case R.id.btnPay:
                if (!isPaying) {
                    startPayment();
                }
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
     * 刷新统计信息
     * @param nCount
     * @param famount
     */
    private void refreshStatistics(int nCount, double famount) {
        String format = getString(R.string.formatTotalCount);
        String str = String.format(format, nCount);
        tvTotalCount.setText(str);

        format = getString(R.string.formatTotalAmount);
        str = String.format(format, famount);
        tvTotalAmount.setText(str);

        refreshLocalTime();
    }

    /***
     * 刷新当亲日期时间
     */
    private void refreshLocalTime() {
        String strtime = DateStringUtils.getCurrentTime();

        String format = getString(R.string.formatCurrentTime);
        String str = String.format(format, strtime);
        tvLocalTime.setText(str);
    }

    private void clearLastPayment() {
        tvName.setText("");
        tvWorkNo.setText("");
        tvRemain.setText("");
        etAmount.setText("");
        tvPayment.setVisibility(View.GONE);
    }


    private void startQuery(int payType) {
        clearLastPayment();
        if(isPaying) return;

        AsynTaskQuery task = new AsynTaskQuery(payType);
        task.execute();
    }


    /***
     * 查询任务
     */
    private class AsynTaskQuery extends AsyncTask<String, Integer, Integer> {
        private int payType = 0;
        private cardInfo cardInfoObj = null;
        private String errString = "";

        public AsynTaskQuery(int type) {
            this.payType = type;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProDialog("查询中,请稍等...");

            isPaying = true;
        }

        @Override
        protected Integer doInBackground(String... strings) {
            payWebapi obj = payWebapi.getInstance();

            obj.setServerIP(posInfoBean.getServerIP());
            obj.setPortNo(posInfoBean.getPortNo());

            // 1. 获取POS流水
            posAudit audit = obj.getPosAuditNo(posInfoBean.getCposno());
            if (null == audit) {
                errString = "网络不通，请检查网络连接！";
                return -1;
            }

            //  设置POS流水号
            posInfoBean.setAuditNo(audit.getPosCno());
            savePosInfoBean(posInfoBean);

            obj.setCposno(posInfoBean.getCposno());
            obj.setCusercode(posInfoBean.getUsercode());

            if (payType == PAYMENT_TYPE_CARD) {
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
            return 0;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

            isPaying = false;

            missProDialog();

            if (null == cardInfoObj) {
                tvPayment.setVisibility(View.GONE);

                DetLog.writeLog(TAG, "查询失败：" + errString);

                showLongToast(errString);
                playSound(false);

                return;
            }

            lastQueryTime = SystemClock.elapsedRealtime();
            tvPayment.setVisibility(View.VISIBLE);

            cardPaymentObj = new cardInfo(cardInfoObj);
            tvName.setText(cardPaymentObj.getName());
            tvWorkNo.setText(cardPaymentObj.getGno());
            tvRemain.setText(String.format("%.02f", cardPaymentObj.getGremain()));

            //  消费金额获取焦点
            etAmount.setText("");

            etAmount.setFocusable(true);
            etAmount.setFocusableInTouchMode(true);
            etAmount.requestFocus();

            //  显示输入法
            showInputKeyboard(etAmount);

            playSound(true);

            DetLog.writeLog(TAG, "查询成功：" + cardInfoObj.toString());
            return;
        }
    }


    /**
     * 开始支付
     */
    private void startPayment() {
        Log.d(TAG, "startPayment");

        if(null==cardPaymentObj){
            showToast("先读卡或扫二维码");
            playSound(false);
            return;
        }

        double amount = 0;
        /***
         * 有效性判断
         */
        if (StringUtils.isEmpty(etAmount.getText().toString())) {
            showToast("请输入有效的金额!");
            playSound(false);
            return;
        }
        try {
            amount = Double.parseDouble(etAmount.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
            playSound(false);
            showToast("请输入有效的金额!");
            return;
        }
        etAmount.setText(String.format("%.2f", amount));

        //  隐藏输入法
        hideInputKeyboard(etAmount);

        if (amount > cardPaymentObj.getGremain()) {
            showToast("余额不足支付!");
            playSound(false);
            return;
        }

        AsynTaskPayment taskPayment = new AsynTaskPayment(amount);
        taskPayment.execute();
    }

    /****
     * 异步支付任务
     */
    private class AsynTaskPayment extends AsyncTask<String, Integer, Integer> {
        double famount = 0;
        String errString = "";

        public AsynTaskPayment(double amount) {
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

            // 1. 获取POS流水
            posAudit audit = obj.getPosAuditNo(posInfoBean.getCposno());
            if (null == audit) {
                errString = "网络不通，请检查网络连接！";
                return -1;
            }

            //  设置POS流水号
            posInfoBean.setAuditNo(audit.getPosCno());
            savePosInfoBean(posInfoBean);

            cardPaymentObj.setAmount((int) (famount * 100));

            Log.d(TAG, "cardPaymentObj:" + cardPaymentObj.toString());

            if (cardPaymentObj.getQrType() == 1) {
                // public int qrPayment(int auditNo,int systemId,int qrType,String userId,Date cdate,int cmoney){
                ret = obj.qrPayment(posInfoBean.getAuditNo(), cardPaymentObj.getSystemId(), cardPaymentObj.getQrType(), cardPaymentObj.getUserId(), new Date(), (int) (famount * 100));
                DetLog.writeLog(TAG, "qrPayment:" + ret);
            } else {
                // public int cardPayment(int auditNo,int cardno,Date cdate,int cmoney){
                ret = obj.cardPayment(posInfoBean.getAuditNo(), cardPaymentObj.getCardno(), new Date(), (int) (famount * 100));
                DetLog.writeLog(TAG, "cardPayment:" + ret);
            }

            return ret;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            missProDialog();
            isPaying = false;

            if (0 == integer) {
                tvPayment.setVisibility(View.GONE);
                playSound(true);

                //  在线交易，所以会成功
                savePaymentRecord(famount, 1);

                totalCount++;
                totalAmount = totalAmount + famount;

                refreshStatistics(totalCount, totalAmount);

//                DetLog.writeLog(TAG, "支付成功：" + cardPaymentObj.toString());
                return;
            }

            //  支付失败
            playSound(false);

            showLongToast(errString);
            playSound(false);

            DetLog.writeLog(TAG, "支付失败：" + errString);

            return;
        }
    }

    /***
     * 询问是否退出APP
     */
    private void finishPrompt() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("是否要退出APP？");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.create().show();

        return;
    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 100:       //  时间定时器
                    refreshLocalTime();

                    //  查询到信息后，如果超过一定时间不支付，清除
                    long t1 = SystemClock.elapsedRealtime();
                    if ((t1 - lastQueryTime) >= MAX_IDLE_TIME_MS) {
                        clearLastPayment();
                        lastQueryTime = t1;
                    }

                    mHandler.sendEmptyMessageDelayed(100, 1000);
                    break;

                case 120:       //  刷新

                    break;
            }
        }
    };

    /***
     * 保存支付记录
     * @param amount
     * @param uploadFlag
     */
    private void savePaymentRecord(double amount, int uploadFlag) {
        PaymentRecord record = new PaymentRecord(cardPaymentObj, amount, posInfoBean);
        DetLog.writeLog(TAG,"支付成功："+record.toString());

        record.setUploadFlag(uploadFlag);
        DBManager.getInstance().getPaymentRecordDao().save(record);

        listRecords.add(0, record);
        adapter.notifyDataSetChanged();

        return;
    }


    private void getTodayRecords() {
        Calendar calnow = Calendar.getInstance();
        calnow.set(Calendar.HOUR_OF_DAY, 0);
        calnow.set(Calendar.MINUTE, 0);
        calnow.set(Calendar.SECOND, 0);
        calnow.set(Calendar.MILLISECOND, 0);

        Date today = calnow.getTime();

        calnow.add(Calendar.DAY_OF_YEAR, 1);
        Date tommorw = calnow.getTime();

        listRecords.clear();
        List<PaymentRecord> records = DBManager.getInstance().getPaymentRecordDao().queryBuilder()
                .where(PaymentRecordDao.Properties.TransTime.ge(today))
                .where(PaymentRecordDao.Properties.TransTime.lt(tommorw))
                .list();
        if (records.size() > 0) {
            Collections.reverse(records);
            listRecords.addAll(records);
        }

        totalCount = 0;
        totalAmount = 0.0f;

        for (PaymentRecord record : listRecords) {
            totalCount++;
            totalAmount = totalAmount + record.getAmount();
        }

        return;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(TAG, "onConfigurationChanged");

        DetLog.writeLog(TAG,"onConfigurationChanged "+newConfig.toString());
        //USB 拔插动作, 这个方法都会被调用.
        super.onConfigurationChanged(newConfig);
    }
}