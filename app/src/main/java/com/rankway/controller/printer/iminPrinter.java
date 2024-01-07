package com.rankway.controller.printer;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

import com.imin.printer.INeoPrinterCallback;
import com.imin.printer.PrinterHelper;

/**
 * <pre>
 *   author : Xin Hongwei
 *   e-mail : xinhw@wxsemicon.com
 *   time  : 2024/01/01
 *   desc  :
 *   version: 1.0
 * </pre>
 */
public class iminPrinter extends PrinterBase{
    private final String TAG = "iminPrinter";

    private PrinterHelper printerHelper = null;

    public iminPrinter(Context context) {
        super(context);
    }

    @Override
    public int openPrinter() {
        Log.d(TAG,"openPrinter");

        printerHelper = PrinterHelper.getInstance();
        if(null==printerHelper){
            Log.d(TAG,"PrinterHelper.getInstance()为空");
            return -1;
        }

        printerHelper.initPrinterService(mContext);

        printerHelper.initPrinter(mContext.getPackageName(), new INeoPrinterCallback() {
            @Override
            public void onRunResult(boolean isSuccess) throws RemoteException {
                Log.d(TAG,"onRunResult");
                if(isSuccess){
                    Log.d(TAG,"初始化成功");
                }else{
                    Log.d(TAG,"初始化失败");
                }
            }

            @Override
            public void onReturnString(String result) throws RemoteException {
                Log.d(TAG,"onReturnString " + result);
            }

            @Override
            public void onRaiseException(int code, String msg) throws RemoteException {
                Log.d(TAG,"onRaiseException " + msg);
            }

            @Override
            public void onPrintResult(int code, String msg) throws RemoteException {
                Log.d(TAG,"onPrintResult "+ msg);
            }
        });
        return 0;
    }

    @Override
    public void closePrinter() {
        Log.d(TAG,"closePrinter");

        if(null!=printerHelper) {
            printerHelper.deInitPrinterService(mContext);
        }

        printerHelper = null;
    }

    @Override
    public int printString(String s) {
        Log.d(TAG,"printString "+s);

        if(null==printerHelper){
            Log.d(TAG,"PrinterHelper.getInstance()为空");
            return -1;
        }

        printerHelper.printText(s, new INeoPrinterCallback() {
            @Override
            public void onRunResult(boolean isSuccess) throws RemoteException {

            }

            @Override
            public void onReturnString(String result) throws RemoteException {

            }

            @Override
            public void onRaiseException(int code, String msg) throws RemoteException {

            }

            @Override
            public void onPrintResult(int code, String msg) throws RemoteException {

            }
        });
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
            Log.d(TAG,"PrinterHelper.getInstance()为空");
            return;
        }
        printerHelper.partialCut();
    }

    /*
    设置对齐方式
        函数：setAlignment(alignment)
        参数：
            alignment –>
                0 = 左
                1 = 中
                2 =右
                默认= 0
        示例：
            IminPrintInstance.setAlignment(1);
    */
}
