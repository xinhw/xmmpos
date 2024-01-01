package com.rankway.controller.printer;

import android.content.Context;
import android.util.Log;

/**
 * <pre>
 *   author : Xin Hongwei
 *   e-mail : xinhw@wxsemicon.com
 *   time  : 2024/01/01
 *   desc  :
 *   version: 1.0
 * </pre>
 */
public class NaPrinter extends PrinterBase{
    private final String TAG = "NaPrinter";

    public NaPrinter(Context context) {
        super(context);
    }

    @Override
    public int openPrinter() {
        Log.d(TAG,"openPrinter 无打印机");
        return 0;
    }

    @Override
    public void closePrinter() {
        Log.d(TAG,"closePrinter 无打印机");
        return;
    }

    @Override
    public int printString(String s) {
        Log.d(TAG,"printString 无打印机");
        return 0;
    }

    @Override
    public int printBytes(byte[] bytes) {
        Log.d(TAG,"printBytes 无打印机");
        return 0;
    }

    @Override
    public void partialCut() {
        Log.d(TAG,"partialCut 无打印机");
        return;
    }
}
