package com.rankway.controller.printer;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.rankway.controller.hardware.util.DataConverter;

import java.util.HashMap;
import java.util.Iterator;

/**
 * <pre>
 *   author : Xin Hongwei
 *   e-mail : xinhw@wxsemicon.com
 *   time  : 2023/09/23
 *   desc  : GP58 USB打印机驱动
 *   version: 1.0
 * </pre>
 */
public class PrinterGP58 extends PrinterBase{
    private final String TAG = "PrinterGP58";
    private UsbManager manager = null;

    private UsbDevice usbDevicePrinter = null;
    private UsbInterface usbInterface;
    private UsbEndpoint usbEndpointIn;
    private UsbEndpoint usbEndpointOut;
    private UsbDeviceConnection usbConnection;

    private String Device_USB_PRINTER = "com.android.example.USB.printer";

    private final int VENDORID = 26728;
    private final int PRODUCTID = 512;

    private final int TIMEOUT = 500;

    public PrinterGP58(Context context) {
        super(context);
    }

    @Override
    public int openPrinter() {
        manager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        if (manager == null) {
            Log.e(TAG, "UsbManager is null.");
            return -1;
        }
        UsbDevice usbDevice = null;


        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
        Log.d(TAG,"UsbDeviceCount: "+deviceList.size());

        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while(deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();
            if(null!=device) Log.d(TAG,devicesString(device));

            if((device.getVendorId()==VENDORID)&&(device.getProductId()==PRODUCTID)){
                usbDevice = device;
                break;
            }
        }

        if(usbDevice==null){
            setErrMessage("请确保读卡器接入并开机");
            return -2;
        }

        // 判断是否拥有该设备的连接权限
        if (!manager.hasPermission(usbDevice)) {
            // 如果没有则请求权限
            PendingIntent mPermissionIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(Device_USB_PRINTER), 0);

            IntentFilter filter = new IntentFilter(Device_USB_PRINTER);
            filter.addAction(Device_USB_PRINTER);
            /*
             * 在使用USB读写器设备前，应用必须获得权限。
             * 为了确切地获得权限，首先需要创建一个广播接收器。在调用requestPermission()这个方法时从得到的广播中监听这个意图。
             * 通过调用requestPermission()这个方法为用户跳出一个是否连接该设备的对话框。
             */
            mContext.registerReceiver(this.mUsbBroadcast, filter);

            manager.requestPermission(usbDevice, mPermissionIntent);
            return 0;
        }

        usbDevicePrinter = usbDevice;
        return openUsbPrinter();
    }

    private int openUsbPrinter(){
        Log.d(TAG,"openUsbPrinter");

        //获取设备接口，一般只有一个
        usbInterface = usbDevicePrinter.getInterface(0);

        // 打开设备，获取 UsbDeviceConnection 对象，连接设备，用于后面的通讯
        usbConnection = manager.openDevice(usbDevicePrinter);
        if(null==usbConnection){
            setErrMessage("打开UsbConnection失败");
            return -3;
        }

        if (!usbConnection.claimInterface(usbInterface, true)){
            setErrMessage("没有找到 USB 设备接口");
            usbConnection.close();
            usbDevicePrinter = null;
            return -4;
        }

        //获取接口上的两个端点，分别对应 OUT 和 IN
        for (int i = 0; i < usbInterface.getEndpointCount(); ++i) {
            UsbEndpoint end = usbInterface.getEndpoint(i);
            if (end.getDirection() == UsbConstants.USB_DIR_IN) {
                usbEndpointIn = end;
            } else {
                usbEndpointOut = end;
            }
        }
        setErrMessage("Usb打印机打开成功");

        //  复位
        printBytes(PrinterFormatUtils.RESET);
        return 0;
    }

    @Override
    public void closePrinter() {
        if(usbConnection!=null) usbConnection.close();
        usbConnection = null;
        usbDevicePrinter = null;
    }

    @Override
    public int printString(String s) {
        Log.d(TAG,String.format("printString(%s)",s));
        if(null==usbConnection) return -1;
        if(null==usbEndpointOut) return -2;

        byte[] bytes = null;
        try {
            bytes = s.getBytes("gbk");
        }catch (Exception e){
            e.printStackTrace();
        }

        usbConnection.bulkTransfer(usbEndpointOut,bytes,bytes.length,TIMEOUT);
        printBytes(PrinterFormatUtils.NEW_LINE);

        return 0;
    }

    @Override
    public int printBytes(byte[] bytes) {
        Log.d(TAG,String.format("printBytes(%s)", DataConverter.bytes2HexString(bytes)));

        if(null==usbConnection) return -1;
        if(null==usbEndpointOut) return -2;

        usbConnection.bulkTransfer(usbEndpointOut,bytes,bytes.length,TIMEOUT);

        return 0;
    }

    @Override
    public void partialCut() {

    }

    @Override
    public int getStatus() {
        return 0;
    }


    /**
     * Create a broadcast receiver.
     */
    private final BroadcastReceiver mUsbBroadcast = new BroadcastReceiver() {
        public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent) {
            Log.w(TAG, "Enter the broadcast receiver.");
            String action = paramAnonymousIntent.getAction();

            //  打印机
            if(Device_USB_PRINTER.equals(action)){
                // Destroy broadcasts.
                mContext.unregisterReceiver(mUsbBroadcast);

                UsbDevice device = (UsbDevice) paramAnonymousIntent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (device != null) {
                    //call method to set up device communication
                    if (paramAnonymousIntent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        setErrMessage("获取权限成功：" + device.getDeviceName());
                        usbDevicePrinter = device;
                        openUsbPrinter();
                    } else {
                        setErrMessage("获取权限失败：" + device.getDeviceName());
                        usbDevicePrinter = null;
                    }
                }
            }
        }
    };

    private String devicesString(UsbDevice device){
        StringBuilder builder = new StringBuilder("UsbDevice Name=" + device.getDeviceName() +
                " VendorId=" + device.getVendorId() + " ProductId=" + device.getProductId() +
                " mClass=" + device.getClass() + " mSubclass=" + device.getDeviceSubclass() +
                " mProtocol=" + device.getDeviceProtocol() + " mManufacturerName=" +" mSerialNumber=" +
                " InterfaceCount="+device.getInterfaceCount() +
                "  ");
        return builder.toString();
    }
}
