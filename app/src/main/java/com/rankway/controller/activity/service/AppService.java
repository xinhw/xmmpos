package com.rankway.controller.activity.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.rankway.controller.activity.BaseActivity;
import com.rankway.controller.activity.project.manager.SpManager;
import com.rankway.sommerlibrary.utils.NetUtil;

/**
 * <pre>
 *   author : Xin Hongwei
 *   e-mail : xinhw@wxsemicon.com
 *   time  : 2022/05/25
 *   desc  :
 *   version: 1.0
 * </pre>
 */
public class AppService extends Service {
    private final String TAG = "AppService";
    private final IBinder binder = new AppBinder();
    private boolean canRun = true;
    private Context mContext;
    private BaseActivity baseActivity;

    public AppService() {
        mContext = null;
        baseActivity = null;
    }

    public void setObjects(Context context, BaseActivity activity) {
        this.mContext = context;
        this.baseActivity = activity;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");

        Thread thread = new Thread(null, new ServiceWorker(), "BackgroundService");
        thread.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");

        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind");

        return super.onUnbind(intent);
    }

    public class AppBinder extends Binder {
        public AppService getService() {
            return AppService.this;
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");

        stopForeground(true);
        canRun = false;

        super.onDestroy();
    }

    private void semiconSleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 服务的线程
     */
    class ServiceWorker implements Runnable {
        @Override
        public void run() {
            //  线程轮询间隔
            int MAX_SLEEP_INTERVAL = 0;
            String keyName = "maxSleepInterval";
            MAX_SLEEP_INTERVAL = SpManager.getIntance().getSpInt(keyName);
            if (0 == MAX_SLEEP_INTERVAL) {
                MAX_SLEEP_INTERVAL = 20000;
                SpManager.getIntance().saveSpInt(keyName, MAX_SLEEP_INTERVAL);
            }
            long t0 = System.currentTimeMillis();

            while (canRun) {
                semiconSleep(100);
                long t1 = System.currentTimeMillis();
                if ((t1 - t0) < MAX_SLEEP_INTERVAL) continue;
                t0 = t1;

//                Log.d(TAG,"服务运行在："+System.currentTimeMillis());

                //  如果Wifi和Mobile data都没有打开
                if (NetUtil.getNetType(mContext) < 0) continue;

                //  上送离线消费交易
                baseActivity.uploadOfflineRecords();
            }
        }
    }

}

