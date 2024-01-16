package com.rankway.controller.printer;

import android.content.Context;
import android.util.Log;

import com.imin.printerlib.IminPrintUtils;

/**
 * <pre>
 *   author : Xin Hongwei
 *   e-mail : xinhw@wxsemicon.com
 *   time  : 2024/01/01
 *   desc  :

    SDK V1.0
    https://yiminoss.neostra.com/docs/Printer.html#id6

    // PrinterSDK
    https://yiminoss.neostra.com/docs/PrinterSDK.html

    特别注意：   一敏打印机调试需要使用TCP adb模式，不能使用USB调试（因为用了同一个端口）

 *   version: 1.0
 * </pre>
 */
public class iminPrinter extends PrinterBase{
    private final String TAG = "iminPrinter";

    private IminPrintUtils printerHelper = null;

    public iminPrinter(Context context) {
        super(context);
    }

    private static boolean bOpened = false;

    @Override
    public int openPrinter() {
        Log.d(TAG,"openPrinter");

        printerHelper = IminPrintUtils.getInstance(mContext);
        if(null==printerHelper){
            Log.d(TAG,"IminPrintUtils.getInstance 返回为空");
            return -1;
        }
        if(bOpened){
            Log.d(TAG,"打印机已经打开");
            return 0;
        }

        //  必须加这个，否则设备状态不对
        printerHelper.resetDevice();

        printerHelper.initPrinter(IminPrintUtils.PrintConnectType.USB);
        /*
        * -1 --> The printer is not connected or powered on
            0 --> The printer is normal
            1 --> The printer is not connected or powered on
            3 --> Print head open
            7 --> No Paper Feed
            8 --> Paper Running Out
            99 --> Other errors
        */
        int i = printerHelper.getPrinterStatus(IminPrintUtils.PrintConnectType.USB);
        if(i==0) bOpened = true;

        Log.d(TAG,"Printer状态："+i);

        //  缺省字体是28
        printerHelper.setTextSize(24);
        return 0;
    }

    @Override
    public void closePrinter() {
        Log.d(TAG,"closePrinter");
    }

    @Override
    public int printString(String s) {
        Log.d(TAG,"printString "+s);

        if(null==printerHelper){
            Log.d(TAG,"IminPrintUtils.getInstance() 为空");
            return -1;
        }

        printerHelper.printText(s + "\n");
        return 0;
    }

    @Override
    public int printBytes(byte[] bytes) {
        return 0;
    }

    @Override
    public void partialCut() {
        Log.d(TAG,"partialCut");

        if(null==printerHelper){
            Log.d(TAG,"IminPrintUtils.getInstance() 为空");
            return;
        }
        printerHelper.printAndLineFeed();
        printerHelper.printAndLineFeed();
        printerHelper.printAndLineFeed();

        printerHelper.partialCut();
    }

    @Override
    public int getStatus() {
        if(printerHelper==null) return -1;
        int i = printerHelper.getPrinterStatus(IminPrintUtils.PrintConnectType.USB);
        return i;
    }

    /*
        6、Set text alignment
        Function：void setAlignment(int alignment)
        parameter：
            alignment --> Set text alignment 0 = left / 1 = center / 2 = right / default = 0


        7、Set text size
        Function：void setTextSize(int size)
        parameter：
            size --> Set text size default 28

    */
}
