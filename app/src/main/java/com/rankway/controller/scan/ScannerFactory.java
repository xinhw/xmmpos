package com.rankway.controller.scan;

import android.content.Context;
import android.os.Build;
import android.util.Log;

/**
 * 扫描器工厂类
 */
public class ScannerFactory {

    private static final String TAG="ScannerFactory";
    public static ScannerBase getScannerObject(Context context){
        String strModel = Build.MODEL.toUpperCase();
        Log.d(TAG,"MODEL:"+strModel);

        ScannerBase scanobj;

        Log.d(TAG,"iData扫描仪");
        scanobj =  new ScannerInterface(context);
        scanobj.unlockScanKey();
        return scanobj;
    }
}
