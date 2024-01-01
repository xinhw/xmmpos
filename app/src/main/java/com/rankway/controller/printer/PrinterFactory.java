package com.rankway.controller.printer;

import android.content.Context;
import android.os.Build;
import android.util.Log;

/**
 * <pre>
 *   author : Xin Hongwei
 *   e-mail : xinhw@wxsemicon.com
 *   time  : 2023/09/23
 *   desc  :
 *   version: 1.0
 * </pre>
 */
public class PrinterFactory {
    private static final String TAG = "PrinterFactory";
    private static PrinterBase printer = null;

    public static PrinterBase getPrinter(Context context){
        String strModel = Build.MODEL.toUpperCase();
        Log.d(TAG,"MODEL:"+strModel);

        if(printer!=null) return printer;

        if(strModel.contains("SWIFT 1")){
            printer = new iminPrinter(context);
        }else{
            printer = new NaPrinter(context);
        }

        return printer;
    }

    public static void setPrinter(PrinterBase printer) {
        PrinterFactory.printer = printer;
    }
}
