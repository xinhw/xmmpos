package com.rankway.controller.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.elvishew.xlog.XLog;
import com.google.gson.Gson;
import com.rankway.controller.common.AppConstants;
import com.rankway.controller.entity.AppUpdateBean;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * 更新管理器
 * <p>
 */
public class UpdateAppUtils {
    private static final String TAG="UpdateAppUtils";

    public static void checkAppUpdate(String url, Context context, AppUpdateCallback updateCallback) {
        AsyncHttpCilentUtil asyncHttpCilentUtil = new AsyncHttpCilentUtil();
        asyncHttpCilentUtil.httpGet(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: "+ call.request());
                updateCallback.onError();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String updateJson = response.body().string();
                Log.d(TAG,String.format("URL:%s,onSuccess:%s",url,updateJson));
                try {
                    XLog.d( String.format("URL:%s updateJson:%s",url,updateJson));
                    Gson gson = new Gson();
                    AppUpdateBean result = gson.fromJson(updateJson, AppUpdateBean.class);
                    XLog.d( "result:" + result);
                    if (result != null) {
                        updateCallback.onSuccess(result);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    updateCallback.onError();
                }
            }
        });

    }


    /**
     * 下载Apk, 并设置Apk地址,
     * 默认位置: /storage/sdcard0/Download
     *
     * @param context  上下文
     * @param appUrl   更新信息
     * @param infoName 通知名称
     * @param storeApk 存储的Apk
     */
    @SuppressWarnings("unused")
    public static void downloadApk(
            Context context, String appUrl,
            String infoName, String storeApk) {
        if (!isDownloadManagerAvailable()) {
            return;
        }

        if (appUrl == null || appUrl.isEmpty()) {
            XLog.e( "请填写\"App下载地址\"");
            return;
        }

        appUrl = appUrl.trim(); // 去掉首尾空格

        if (!appUrl.startsWith("http")) {
            appUrl = "http://" + appUrl; // 添加Http信息
        }

        XLog.d( "appUrl: " + appUrl);

        DownloadManager.Request request;
        try {
            request = new DownloadManager.Request(Uri.parse(appUrl));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        request.setTitle(infoName);
//        request.setDescription(description);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, storeApk);

        Context appContext = context.getApplicationContext();
        DownloadManager manager = (DownloadManager)
                appContext.getSystemService(Context.DOWNLOAD_SERVICE);

        // 存储下载Key
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(appContext);
        sp.edit().putLong(AppConstants.DOWNLOAD_APK_ID_PREFS, manager.enqueue(request)).apply();
    }

    // 最小版本号大于9
    private static boolean isDownloadManagerAvailable() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    public interface AppUpdateCallback {
        void onSuccess(AppUpdateBean updateInfo);

        void onError();
    }

    /**
     * 调用系统安装器安装apk
     *
     * @param context 上下文
     * @param file apk文件
     */
    public static void installApk(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri apkuri = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            apkuri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            apkuri = Uri.fromFile(file);
        }
        intent.setDataAndType(apkuri, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }
}
