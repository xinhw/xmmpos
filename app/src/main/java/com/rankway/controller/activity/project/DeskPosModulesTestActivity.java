package com.rankway.controller.activity.project;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.rankway.controller.R;
import com.rankway.controller.activity.BaseActivity;
import com.rankway.controller.hardware.util.DetLog;
import com.rankway.controller.printer.PrinterBase;
import com.rankway.controller.printer.PrinterFactory;
import com.rankway.controller.printer.PrinterFormatUtils;
import com.rankway.controller.reader.ReaderBase;
import com.rankway.controller.reader.ReaderFactory;
import com.rankway.controller.utils.DateStringUtils;

import java.util.HashMap;
import java.util.Iterator;

public class DeskPosModulesTestActivity extends BaseActivity {

    private final String TAG = "DeskPosModulesTestActivity";

    private LinearLayout llResult;
    private ScrollView scrollViewResult;
    private Handler mHandler = null;


    private ReaderBase reader = null;
    private PrinterBase printer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modules_test);

        initView();

        initData();

        enumAllUsbDevice();
    }

    private void initView() {

        mHandler = new Handler();

        View view = findViewById(R.id.back_img);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView textView = findViewById(R.id.tvQrTest);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DeskPosModulesTestActivity.this, "连接上二维码扫描头后直接扫描测试", Toast.LENGTH_LONG).show();
                playSound(false);
            }
        });

        textView = findViewById(R.id.tvCardTest);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reader == null) {
                    reader = ReaderFactory.getReader(mContext);
                    int ret = reader.openReader();
                    if (0 != ret) {
                        PutMessage(DEVICE_CARD_READER, reader.getErrMessage());
                    } else {
                        PutMessage(DEVICE_CARD_READER, "读卡器打开成功！");
                    }
                } else {
                    String sn = reader.getCardNo();
                    if (sn == null) {
                        PutMessage(DEVICE_CARD_READER, reader.getErrMessage());
                    } else {
                        PutMessage(DEVICE_CARD_READER, "读取卡片：" + sn);
                    }
                }
            }
        });

        textView = findViewById(R.id.tvPrinterTest);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (printer == null) {
                    printer = PrinterFactory.getPrinter(mContext);
                    int ret = printer.openPrinter();
                    if (0 != ret) {
                        PutMessage(DEVICE_PRINTER, printer.getErrMessage());
                    } else {
                        PutMessage(DEVICE_PRINTER, "打印机打开成功！");
                    }
                } else {
                    printTest();
                }
            }
        });

        textView = findViewById(R.id.tvClear);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != llResult) llResult.removeAllViews();
            }
        });
        scrollViewResult = findViewById(R.id.scrollViewResult);
        llResult = findViewById(R.id.llResult);
    }

    private void initData() {
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (reader != null) reader.closeReader();
        if (printer != null) printer.closePrinter();

    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            int keyCode = event.getKeyCode();
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
        return super.dispatchKeyEvent(event);
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
            if (TextUtils.isEmpty(qrcode)) {
                Log.d(TAG, "qrcode is empty");
                return;
            }
            PutMessage(DEVICE_QR_CODE, qrcode);
        }
    };


    private final int DEVICE_QR_CODE = 0;
    private final int DEVICE_CARD_READER = 1;
    private final int DEVICE_PRINTER = 2;

    /**
     * 提示信息框添加信息
     *
     * @param str 要添加的信息
     */
    private void PutMessage(int type, final String str) {
        mHandler.post(() -> {
            TextView lb = new TextView(this);
            lb.setTextSize(16);
            lb.setTextColor(Color.rgb(20, 20, 20));

            String strTime = DateStringUtils.getCurrentTime();
            String deviceName = "";
            switch (type) {
                case DEVICE_QR_CODE:
                    deviceName = "二维码";
                    break;
                case DEVICE_CARD_READER:
                    deviceName = "读卡器";
                    break;
                case DEVICE_PRINTER:
                    deviceName = "打印机";
                    break;
                default:
                    break;
            }
            lb.setText(String.format("%s %s %s", strTime, deviceName, str));
            llResult.addView(lb);

            if (llResult.getChildCount() > 200) {
                llResult.removeViewAt(0);
            }
            scrollViewResult.post(() -> scrollViewResult.fullScroll(View.FOCUS_DOWN));

        });
    }


    private void printTest() {
        Log.d(TAG, "printTest");

        String s = "";

        //  走纸5行
        s = "--------------------------------";
        printString(s);

        //  放大字体
        printBytes(PrinterFormatUtils.getFontSizeCommand(true));
        s = "上海报业集团餐厅";
        printString(s);

        //  字体正常
        printBytes(PrinterFormatUtils.getFontSizeCommand(false));

        s = "--------------------------------";
        printString(s);

        s = " 红烧带鱼    1   5.00";
        printString(s);
        s = " 炒 青 菜    1   3.00";
        printString(s);
        s = " 米    饭    1   1.00";
        printString(s);
        s = " 汤          1   0.50";
        printString(s);
        s = "--------------------------------";
        printString(s);
        printString("合计：       9.50");
        printString("时间： " + DateStringUtils.getCurrentTime());

        printBytes(PrinterFormatUtils.getFeedCommand(3));
    }

    private void printString(String s) {
        PutMessage(DEVICE_PRINTER, s);
        if (null != printer) printer.printString(s);
    }

    private void printBytes(byte[] bytes) {
        if (null != printer) printer.printBytes(bytes);
    }


    private void enumAllUsbDevice() {
        Log.d(TAG, "enumAllUsbDevice");

        UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        Log.d(TAG, "UsbDeviceCount: " + deviceList.size());

        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();
            if (null != device) Log.d(TAG, devicesString(device));

            //获取设备接口
            for (int i = 0; i < device.getInterfaceCount(); ) {
                // 一般来说一个设备都是一个接口，你可以通过getInterfaceCount()查看接口的个数
                // 这个接口上有两个端点，分别对应OUT 和 IN
                UsbInterface usbInterface = device.getInterface(i);
                if (null == usbInterface) continue;

                Log.d(TAG, "UsbInterface Id:" + usbInterface.getId() + " Name:" + usbInterface.getName()
                        + " Class:" + usbInterface.getClass() + " Procotocol:" + usbInterface.getInterfaceProtocol()
                        + " EndPointCount:" + usbInterface.getEndpointCount());

                for (int j = 0; j < usbInterface.getEndpointCount(); j++) {
                    UsbEndpoint endpoint = usbInterface.getEndpoint(j);
                    if (endpoint == null) continue;

                    Log.d(TAG, "UsbEndpoint Address:" + endpoint.getAddress() +
                            " Attributes:" + endpoint.getAttributes() +
                            " Direction:" + endpoint.getDirection());
                }
                break;
            }
        }
    }


    private String devicesString(UsbDevice device) {
        StringBuilder builder = new StringBuilder("UsbDevice Name=" + device.getDeviceName() +
                " VendorId=" + device.getVendorId() + " ProductId=" + device.getProductId() +
                " mClass=" + device.getClass() + " mSubclass=" + device.getDeviceSubclass() +
                " mProtocol=" + device.getDeviceProtocol() + " mManufacturerName=" + " mSerialNumber=" +
                " InterfaceCount=" + device.getInterfaceCount() +
                "  ");
        return builder.toString();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyDown " + keyCode);

        //  右下角返回键
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            finish();
            return true;
        }
        if (KeyEvent.KEYCODE_HOME == keyCode) {
            return true;
        }

        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(TAG, "onConfigurationChanged");

        DetLog.writeLog(TAG,"onConfigurationChanged "+newConfig.toString());
        //USB 拔插动作, 这个方法都会被调用.
        super.onConfigurationChanged(newConfig);
    }
}