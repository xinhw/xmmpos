package com.rankway.controller;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.elvishew.xlog.XLog;
import com.rankway.controller.common.AliPushServiceRegister;
import com.rankway.controller.common.AppConstants;
import com.rankway.controller.persistence.DBManager;
import com.rankway.controller.utils.location.DLocationUtils;
import com.rankway.sommerlibrary.app.BaseApplication;
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

    /***
     * 初始化阿里移动推送通道
     */
    public void initCloudChannel() {
        Log.d(TAG,"initCloudChannel");

        // 创建notificaiton channel
        this.createNotificationChannel();

        AliPushServiceRegister.getInstance().init(this);
    }

    /***
     * 创建通知推送后自动弹出
     */
    private void createNotificationChannel() {
        Log.d(TAG,"createNotificationChannel");
        Log.d(TAG,"Build.VERSION.SDK_INT = " + Build.VERSION.SDK_INT);
        Log.d(TAG,"Build.VERSION_CODES.O = "+Build.VERSION_CODES.O);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // 通知渠道的id
            String id = AppConstants.NOTIFICATION_CHANNEL_ID;
            // 用户可以看到的通知渠道的名字.
            CharSequence name = "notification channel";
            // 用户可以看到的通知渠道的描述
            String description = "notification description";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(id, name, importance);
            // 配置通知渠道的属性
            mChannel.setDescription(description);
            // 设置通知出现时的闪灯（如果 android 设备支持的话）
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            // 设置通知出现时的震动（如果 android 设备支持的话）
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            //最后在notificationmanager中创建该通知渠道
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }

}
