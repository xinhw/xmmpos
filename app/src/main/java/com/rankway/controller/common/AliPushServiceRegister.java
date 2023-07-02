package com.rankway.controller.common;

import android.content.Context;
import android.util.Log;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.rankway.controller.hardware.util.DetLog;

public class AliPushServiceRegister {
    private final String TAG ="AliPushServiceRegister";
    private boolean registered = false;

    public static AliPushServiceRegister getInstance(){
        return SingletonHoler.sIntance;
    }
    private static class SingletonHoler{
        private static final AliPushServiceRegister sIntance = new AliPushServiceRegister();
    }

    public void run(Context context){
        Log.d(TAG,"AliPushServiceRegister.run");

        aliCheckStatus();

        return;
    }

    public void init(Context context){
        Log.d(TAG,"AliPushServiceRegister init " );
        PushServiceFactory.init(context);
        aliRegister(context);
    }

    private void aliRegister(Context context){
        Log.d(TAG,"AliPushServiceRegister aliRegister");

        final CloudPushService pushService = PushServiceFactory.getCloudPushService();
        //  初始化并注册推送通道
        pushService.register(context, new CommonCallback() {
            @Override
            public void onSuccess(String response) {
                Log.d(TAG, "init cloudchannel success");
                Log.d(TAG, "设备ID:" + pushService.getDeviceId());
            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {
                DetLog.writeLog(TAG, "init cloudchannel failed -- errorcode:" + errorCode + " -- errorMessage:" + errorMessage);
            }
        });
        return;
    }

    private void aliCheckStatus(){
        Log.d(TAG,"AliPushServiceRegister aliCheckStatus");

        final CloudPushService pushService = PushServiceFactory.getCloudPushService();
        pushService.checkPushChannelStatus(new CommonCallback() {
            @Override
            public void onSuccess(String s) {
                Log.d(TAG,"checkPushChannelStatus Ok:"+s);
                registered = true;
            }

            @Override
            public void onFailed(String s, String s1) {
                Log.d(TAG,"checkPushChannelStatus Failure:" + s + " " + s1);
            }
        });
        return;
    }

    public boolean isRegistered() {
        return registered;
    }
}
