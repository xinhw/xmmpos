package com.rankway.controller;

import android.content.res.Configuration;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.elvishew.xlog.XLog;
import com.rankway.controller.app.BaseApplication;
import com.rankway.controller.persistence.DBManager;
import com.rankway.controller.utils.location.DLocationUtils;
import com.tencent.mmkv.MMKV;


public class DetApplication extends BaseApplication {

    private static final String TAG = "DetApplication";


    @Override
    public void onCreate() {
        super.onCreate();

        // 程序创建的时候执行
        DLocationUtils.init(this);

        // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
        SDKInitializer.initialize(this);

        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);

        MMKV.initialize(this);

        DBManager.init(getApplicationContext());

        //  阿里移动推送初始化
//        initCloudChannel();
    }


    @Override
    public void onTerminate() {
        // 程序终止的时候执行
        XLog.d("onTerminate");

        super.onTerminate();

    }

    @Override
    public void onLowMemory() {
        // 低内存的时候执行
        XLog.d("onLowMemory");
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        // 程序在内存清理的时候执行
        XLog.d("onTrimMemory");
        super.onTrimMemory(level);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        XLog.d("onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
    }

}
