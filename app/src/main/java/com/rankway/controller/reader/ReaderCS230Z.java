package com.rankway.controller.reader;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.hc.reader.AndroidUSB;
import com.hc.reader.Card;
import com.rankway.controller.hardware.util.DataConverter;
import com.rankway.controller.hardware.util.DetLog;

import java.util.Arrays;

/**
 * <pre>
 *   author : Xin Hongwei
 *   e-mail : xinhw@wxsemicon.com
 *   time  : 2023/09/23
 *   desc  :
 *   version: 1.0
 * </pre>
 */
public class ReaderCS230Z extends ReaderBase{
    private final String TAG = "ReaderCS230Z";

    private Card reader;
    private UsbManager manager = null;
    private UsbDevice usbDeviceReader = null;
    private String Device_USB_READER = "com.android.example.USB.reader";

    public ReaderCS230Z(Context context) {
        super(context);
    }

    @Override
    public int opneReader() {
        Log.d(TAG,"opneReader");

        manager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        if (manager == null) {
            Log.e(TAG, "UsbManager is null.");
            return -1;
        }

        reader = new AndroidUSB(mContext, manager);
        usbDeviceReader = reader.GetUsbReader();
        if (usbDeviceReader == null) {
            setErrMessage("未找到读卡器，请确保设备已连接");
            return  -2;
        }

        // 判断是否拥有该设备的连接权限
        if (!manager.hasPermission(usbDeviceReader)) {
            // 如果没有则请求权限
            PendingIntent mPermissionIntent = PendingIntent.getBroadcast(mContext, 0,
                    new Intent(Device_USB_READER), PendingIntent.FLAG_UPDATE_CURRENT);
            IntentFilter filter = new IntentFilter(Device_USB_READER);
            /*
             * 在使用USB读写器设备前，应用必须获得权限。
             * 为了确切地获得权限，首先需要创建一个广播接收器。在调用requestPermission()这个方法时从得到的广播中监听这个意图。
             * 通过调用requestPermission()这个方法为用户跳出一个是否连接该设备的对话框。
             */
            mContext.registerReceiver(this.mUsbBroadcast, filter);

            manager.requestPermission(usbDeviceReader, mPermissionIntent);
        } else {
            short st = reader.OpenReader(usbDeviceReader);
            if (st >= 0) {
                setErrMessage("读卡器连接成功");
            } else {
                setErrMessage(reader.GetErrMessage((short) 0, st));
                return st;
            }
        }

        return 0;
    }

    @Override
    public void closeReader() {
        Log.d(TAG,"closeReader");
        if(reader==null){
            setErrMessage("未打开读卡器");
            return;
        }

        try {
            short st = reader.hc_exit();
            if (st == 0) {
                setErrMessage("读卡器已断开.");
            } else {
                setErrMessage(reader.GetErrMessage((short) 0, st));
            }
        } catch (Exception e) {
            setErrMessage(e.getMessage());
        }
        return;
    }

    @Override
    public String getCardNo() {
        Log.d(TAG,"getCard");

        if(reader==null){
            setErrMessage("读卡器未初始化");
            return null;
        }

        try {
            byte[] snr = new byte[7];
            short st = reader.rf_request((byte) 0);
            if (st >= 0) {
                setErrMessage("请求卡片成功, 返回卡类型代码: " + st);
            } else {
                setErrMessage("未找到卡片");
                return null;
            }

            Arrays.fill(snr, (byte) 0x00);
            st = reader.rf_anticoll(snr);
            if (st >= 0) {
                setErrMessage("防冲突成功, 返回卡片序列号: " + DataConverter.bytes2HexString(snr));
            } else {
                setErrMessage(reader.GetErrMessage((short) 0, st));
                return null;
            }

            st = reader.rf_select(snr);
            if (st >= 0) {
                setErrMessage("选卡成功, 返回卡片容量: " + st);
            } else {
                setErrMessage(reader.GetErrMessage((short) 0, st));
                return null;
            }

            reader.rf_halt();
            reader.dv_beep((short) 30);

            byte[] sn = new byte[4];
            System.arraycopy(snr,0,sn,0,4);
            return DataConverter.bytes2HexString(sn).toUpperCase();
        }catch (Exception e){
            DetLog.writeLog(TAG,"读卡异常："+e.getMessage());
            return null;
        }
    }

    @Override
    public byte[] readBlock(byte blockNo) {
        return null;
    }

    @Override
    public int authenticate(byte sectorNo, byte keyType, byte[] key) {
        return -1;
    }

    @Override
    public int writeBlock(byte blockNo, byte[] block) {
        return -1;
    }

    @Override
    public int rats() {
        return -1;
    }

    @Override
    public byte[] apdu(byte[] cmd) {
        return null;
    }

    /**
     * Create a broadcast receiver.
     */
    private final BroadcastReceiver mUsbBroadcast = new BroadcastReceiver() {
        public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent) {
            Log.w(TAG, "Enter the broadcast receiver.");
            String action = paramAnonymousIntent.getAction();

            // 广播类型：读写器
            if (Device_USB_READER.equals(action)) {
                try {
                    mContext.unregisterReceiver(mUsbBroadcast);

                    // 如果用户同意，则对读写器进行操作
                    if (paramAnonymousIntent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        short st = reader.OpenReader(usbDeviceReader);
                        if (st >= 0) {
                            setErrMessage("Connect Reader succeeded.");
                        } else {
                            setErrMessage(reader.GetErrMessage((short) 0, st));
                        }
                    } else {
                        //Toast.makeText(MainActivity.this, "Rejected.", Toast.LENGTH_LONG).show();
                        setErrMessage("操作人员拒绝了授权");
                    }
                } catch (Exception e) {
                    Log.d(TAG,"读卡器打开失败："+e.getMessage());
                }
            }
        }
    };
}
