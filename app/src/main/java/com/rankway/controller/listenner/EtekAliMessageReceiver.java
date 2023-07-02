package com.rankway.controller.listenner;

import android.content.Context;
import android.util.Log;

import com.alibaba.sdk.android.push.MessageReceiver;
import com.alibaba.sdk.android.push.notification.CPushMessage;
import com.rankway.controller.activity.project.eventbus.MessageEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

public class EtekAliMessageReceiver extends MessageReceiver {
    // 消息接收部分的LOG_TAG
    public static final String TAG = "EtekAliMessageReceiver";
    @Override
    public void onNotification(Context context, String title, String summary, Map<String, String> extraMap) {
        // TODO 处理推送通知
        Log.d(TAG, "onNotification, title: "
                + title + ", summary: "
                + summary + ", extraMap: "
                + extraMap);
        MessageEvent msg = new MessageEvent(MessageEvent.TYPE_NOTIFICATION,title,summary);
        EventBus.getDefault().post(msg);
    }
    @Override
    public void onMessage(Context context, CPushMessage cPushMessage) {
        Log.d(TAG, "onMessage, messageId: " + cPushMessage.getMessageId()
                + ", title: " + cPushMessage.getTitle()
                + ", content:" + cPushMessage.getContent());
        MessageEvent msg = new MessageEvent(MessageEvent.TYPE_MESSAGE,cPushMessage.getTitle(),cPushMessage.getContent());
        EventBus.getDefault().post(msg);
    }
    @Override
    public void onNotificationOpened(Context context, String title, String summary, String extraMap) {
        Log.d(TAG, "onNotificationOpened, title: " + title + ", summary: " + summary + ", extraMap:" + extraMap);
    }
    @Override
    protected void onNotificationClickedWithNoAction(Context context, String title, String summary, String extraMap) {
        Log.d(TAG, "onNotificationClickedWithNoAction, title: " + title + ", summary: " + summary + ", extraMap:" + extraMap);
    }
    @Override
    protected void onNotificationReceivedInApp(Context context, String title, String summary, Map<String, String> extraMap, int openType, String openActivity, String openUrl) {
        Log.d(TAG, "onNotificationReceivedInApp, title: " + title + ", summary: " + summary + ", extraMap:" + extraMap + ", openType:" + openType + ", openActivity:" + openActivity + ", openUrl:" + openUrl);
    }
    @Override
    protected void onNotificationRemoved(Context context, String messageId) {
        Log.d(TAG, "onNotificationRemoved");
    }
}
