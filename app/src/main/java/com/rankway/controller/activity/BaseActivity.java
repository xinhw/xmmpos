package com.rankway.controller.activity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.elvishew.xlog.XLog;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.maning.mndialoglibrary.MProgressDialog;
import com.rankway.controller.R;
import com.rankway.controller.activity.service.DownloadUtil;
import com.rankway.controller.common.ActivityCollector;
import com.rankway.controller.common.AppIntentString;
import com.rankway.controller.entity.AppUpdateBean;
import com.rankway.controller.entity.PosInfoBean;
import com.rankway.controller.hardware.util.DetLog;
import com.rankway.controller.hardware.util.SoundPoolHelp;
import com.rankway.controller.persistence.DBManager;
import com.rankway.controller.persistence.entity.PaymentRecord;
import com.rankway.controller.persistence.entity.PaymentShiftEntity;
import com.rankway.controller.persistence.gen.PaymentShiftEntityDao;
import com.rankway.controller.utils.AppUtils;
import com.rankway.controller.utils.AsyncHttpCilentUtil;
import com.rankway.controller.utils.DateStringUtils;
import com.rankway.controller.utils.ToastUtils;
import com.rankway.controller.utils.UpdateAppUtils;
import com.rankway.controller.utils.VibrateUtil;
import com.rankway.controller.widget.MyAlertDialog;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class BaseActivity extends AppCompatActivity {

    private SharedPreferences preferences;

    protected Toolbar mToolbar;

    protected Context mContext;

    protected String TAG = "";
    private ProgressDialog progressDialog;

    protected void showProDialog(String msg) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(msg);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    protected void missProDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        progressDialog = null;
    }

    protected void setProDialogText(String strText) {
        if (null != progressDialog) {
            progressDialog.setMessage(strText);
        }
    }

    protected void showStatusDialog(final String content) {
        runOnUiThread(() -> {
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mContext);
            builder.setTitle(content);
            //设置对话框标题
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            // 4.设置常用api，并show弹出
            builder.setCancelable(true); //设置按钮是否可以按返回键取消,false则不可以取消
            android.support.v7.app.AlertDialog dialog = builder.create(); //创建对话框
            dialog.setCanceledOnTouchOutside(false); //设置弹出框失去焦点是否隐藏,即点击屏蔽其它地方是否隐藏
            dialog.show();
        });

    }

    protected void showProgressDialog(String content) {
    }

    protected void closeProgressDialog() {
        MProgressDialog.dismissProgress();
    }


    protected int getWindowWidth() {
        WindowManager manager = this.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);

        return outMetrics.widthPixels;
    }

    protected String getVersionCode(){
        int versionCode = 0;
        try{
            versionCode = mContext.getPackageManager()
                    .getPackageInfo(mContext.getPackageName(),0).versionCode;
        }catch (Exception e){
            e.printStackTrace();
        }
        return "1.0." + versionCode;
    }

    @Nullable
    protected Activity findActivity() {
        if (mContext instanceof Activity) {
            return (Activity) mContext;
        }
        if (mContext instanceof ContextWrapper) {
            ContextWrapper wrapper = (ContextWrapper) mContext;
            return findActivity();
        } else {
            return null;
        }
    }

    @Nullable
    protected String getTag() {
        if (mContext instanceof Activity) {
            return mContext.getClass().getSimpleName();
        }
        return null;
    }


    @Override
    protected void onResume() {
        super.onResume();
        // set the screen to portrait
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        TAG = mContext.getClass().getSimpleName();
        ActivityCollector.addActivity(this);
        changeAppLanguage();
        initSound();
    }


    @Override
    protected void onDestroy() {
        ActivityCollector.removeActivity(this);
        closeProgressDialog();
        releaseSound();
        super.onDestroy();
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        if (mToolbar != null) {
            mToolbar.setTitle(title);
        }
    }

    protected void initToolBar(int titleResource) {
        mToolbar = findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            mToolbar.setTitle(titleResource);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);//这句代码使启用Activity回退功能，并显示Toolbar上的左侧回退图标
        }

    }


    protected void initSupportActionBar(int title) {
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//这句代码使启用Activity回退功能，并显示Toolbar上的左侧回退图标
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }


    public String getPreInfo(String index) {
        preferences = getSharedPreferences("detInfo", MODE_PRIVATE);

        return preferences.getString(index, "");
    }


    public void setStringInfo(String index, String value) {
        preferences = getSharedPreferences("detInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(index, value);
        editor.apply();
    }


    protected void setIntInfo(String index, int value) {
        preferences = getSharedPreferences("detInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(index, value);
        editor.apply();
    }


    protected void setLongInfo(String index, long value) {
        preferences = getSharedPreferences("detInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(index, value);
        editor.apply();
    }

    protected long getLongInfo(String index) {
        preferences = getSharedPreferences("detInfo", MODE_PRIVATE);
        return preferences.getLong(index, 0L);
    }


    protected String getStringInfo(String index) {
        preferences = getSharedPreferences("detInfo", MODE_PRIVATE);
        return preferences.getString(index, "");
    }

    protected int getIntInfo(String index) {
        preferences = getSharedPreferences("detInfo", MODE_PRIVATE);
        return preferences.getInt(index, 0);
    }

    // 保存延时设置
    protected void setDelaySetting(String key, String delaySetting) {
        preferences = getSharedPreferences("detInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, delaySetting);
        editor.apply();
    }

    // 获取延时设置
    protected String getDelaySetting(String key) {
        preferences = getSharedPreferences("detInfo", MODE_PRIVATE);
        return preferences.getString(key, "");
    }


    protected Boolean getBooleanInfo(String index) {
        preferences = getSharedPreferences("detInfo", MODE_PRIVATE);

        return preferences.getBoolean(index, false);
    }

    protected Boolean getBooleanInfoDefaultTrue(String index) {
        preferences = getSharedPreferences("detInfo", MODE_PRIVATE);

        return preferences.getBoolean(index, true);
    }

    protected void setBooleanInfo(String index, boolean value) {

        preferences = getSharedPreferences("detInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(index, value);
        editor.apply();
    }

    protected void getPixel() {
        // 通过Activity类中的getWindowManager()方法获取窗口管理，再调用getDefaultDisplay()方法获取获取Display对象
        Display display = getWindowManager().getDefaultDisplay();

        // 方法一(推荐使用)使用Point来保存屏幕宽、高两个数据
        Point outSize = new Point();
        // 通过Display对象获取屏幕宽、高数据并保存到Point对象中
        display.getSize(outSize);
        // 从Point对象中获取宽、高
        int x = outSize.x;
        int y = outSize.y;
        // 通过吐司显示屏幕宽、高数据
        showToast("手机像素为：X:" + x + "||Y:" + y);
    }

    public <T> void setDataList(String tag, List<T> dataList) {
        if (null == dataList || dataList.size() <= 0) return;

        Gson gson = new Gson();
        String strJson = gson.toJson(dataList);
        SharedPreferences.Editor editor = preferences.edit();
//        editor.clear();
        editor.putString(tag, strJson);
        editor.commit();
    }

    public <T> List<T> getDataList(String tag, Class<T> cls) {
        List<T> dataList = new ArrayList<T>();
        String strJson = preferences.getString(tag, null);
        if (null == strJson) return dataList;

        JsonArray array = new JsonParser().parse(strJson).getAsJsonArray();
        for (final JsonElement elem : array) {
            dataList.add(new Gson().fromJson(elem, cls));
        }

        return dataList;
    }


    protected int getMyColor(int colorID) {
        return mContext.getResources().getColor(colorID);
    }

    protected String getMyString(int stringId) {
        return mContext.getResources().getString(stringId);
    }

    /*----以下是android6.0动态授权的封装十分好用---------------------------------------------------------------------------*/
    private int mPermissionIdx = 0x10;//请求权限索引
    private SparseArray<GrantedResult> mPermissions = new SparseArray<>();//请求权限运行列表

    @SuppressLint("Override")
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        GrantedResult runnable = mPermissions.get(requestCode);
        if (runnable == null) {
            return;
        }
        ArrayList<String> unGrantedPremList = new ArrayList<String>();
        XLog.d("permissions:" + Arrays.toString(permissions) + " -  grantResults: " + Arrays.toString(grantResults));
        runnable.mGranted = true;
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    unGrantedPremList.add(permissions[i]);
                    runnable.mGranted = false;
                }
            }
        }
        runnable.unGrantedPremission = unGrantedPremList.toArray(new String[0]);
        runOnUiThread(runnable);
    }

    public void requestPermission(String[] permissions, String reason, GrantedResult runnable) {
        if (runnable == null) {
            return;
        }
        runnable.mGranted = false;
        if (Build.VERSION.SDK_INT < 23 || permissions == null || permissions.length == 0) {
            runnable.mGranted = true;//新添加
            runOnUiThread(runnable);
            return;
        }
        final int requestCode = mPermissionIdx++;
        mPermissions.put(requestCode, runnable);

		/*
			是否需要请求权限
		 */
        boolean granted = true;
        for (String permission : permissions) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                granted = granted && checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
            }
        }

        if (granted) {
            runnable.mGranted = true;
            runOnUiThread(runnable);
            return;
        }

		/*
			是否需要请求弹出窗
		 */
        boolean request = true;
        for (String permission : permissions) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                request = request && !shouldShowRequestPermissionRationale(permission);
            }
        }

        if (!request) {
            final String[] permissionTemp = permissions;
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setMessage(reason)
                    .setPositiveButton(R.string.btn_sure, (dialog1, which) -> requestPermissions(permissionTemp, requestCode))
                    .setNegativeButton(R.string.btn_cancel, (dialog12, which) -> {
                        dialog12.dismiss();
                        GrantedResult runnable1 = mPermissions.get(requestCode);
                        if (runnable1 == null) {
                            return;
                        }
                        runnable1.mGranted = false;
                        runOnUiThread(runnable1);
                    }).create();
            dialog.show();
        } else {
            requestPermissions(permissions, requestCode);
        }
    }

    public static abstract class GrantedResult implements Runnable {
        private boolean mGranted;
        private String[] unGrantedPremission;

        public abstract void onResult(boolean granted, String[] unGrantedPremission);

        @Override
        public void run() {
            onResult(mGranted, unGrantedPremission);
        }
    }

    /*----以下是log记录---------------------------------------------------------------------------*/

    /*----TOAST---------------------------------------------------------------------------*/
    public void showToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtils.showCustom(mContext, message);
            }
        });

    }

    public void showLongToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtils.showLongCustom(mContext, message);
            }
        });
    }

    protected void delayAction(final Intent intent, long time) {
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (intent != null) {
                    startActivity(intent);
                }

                finish();
            }
        }, time);
    }

    public void showDialogMessage(String strtext,
                                  String message,
                                  String confirmText, DialogInterface.OnClickListener onClickListener,
                                  String cancelText, DialogInterface.OnClickListener clickListener) {
        showDialogMessage(strtext,message,confirmText,onClickListener,cancelText,clickListener,null);
    }

    /***
     * 显示信息，带View，【确认】，和【取消】按钮
     * @param title
     * @param message
     * @param confirmText
     * @param confirmListener
     * @param cancelText
     * @param cancelListener
     * @param view
     * @return
     */
    public MyAlertDialog showDialogMessage(String title,
                                           CharSequence message,
                                           String confirmText, DialogInterface.OnClickListener confirmListener,
                                           String cancelText, DialogInterface.OnClickListener cancelListener,
                                           View view) {

        MyAlertDialog myAlertDialog = new MyAlertDialog(this);
        myAlertDialog.setCancelable(false);
        myAlertDialog.setCanceledOnTouchOutside(false); //设置弹出框失去焦点是否隐藏,即点击屏蔽其它地方是否隐藏
        myAlertDialog.show();
        if(null!=title) myAlertDialog.setTitle(title);
        if(null!=message) myAlertDialog.setMessage(message);
        if(view != null) myAlertDialog.resetContent(view);

        myAlertDialog.setPositive(confirmText,confirmListener);
        myAlertDialog.setNegative(cancelText,cancelListener);

//        if(confirmListener!=null){
//            myAlertDialog.setPositive(confirmText,confirmListener);
//        }
//        if(cancelListener!=null){
//            myAlertDialog.setNegative(cancelText,cancelListener);
//        }
        return myAlertDialog;
    }


    public void detSleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //  上报状态
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private static int nUploadNum = 0;

    public void resetUploadNum() {
        nUploadNum = 0;
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    //  黑白名单同步
    ////////////////////////////////////////////////////////////////////////////////////////////////


    private String getCheckAppUrl(){
        PosInfoBean posInfoBean = getPosInfoBean();
        if(null==posInfoBean) return "";

        //  http://ip2:serverPort2/api/appVersions/{appType}?sn=10003
        //  sn为pos机编号，appType暂定两种(Desktop-POS/Handset-POS)
        String url = String.format("http://%s/api/appVersions/Handset-POS?sn=%s",
                posInfoBean.getUpgradeUrl(),
                posInfoBean.getCposno());
        return url;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //  APP和主控板程序升级
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private final int appUpdate = 0;

    private AlertDialog updateDialog;

    /***
     * 检查服务器上版本信息
     */
    public void checkAppUpdate() {
        Log.d(TAG,"checkAppUpdate");

        String url = getCheckAppUrl();
        if(StringUtils.isEmpty(url)){
            Log.d(TAG,"APP升级地址为空");
            return;
        }

        UpdateAppUtils.checkAppUpdate(url, this, new UpdateAppUtils.AppUpdateCallback() {
            @Override
            public void onSuccess(AppUpdateBean updateInfo) {
                Log.d(TAG, updateInfo.toString());
                if (updateInfo == null) {
                    showUpdateMessage("已经是最新版本，无需升级");
                    return;
                }
                if (updateInfo.getResult() == null) {
                    showUpdateMessage("已经是最新版本，无需升级");
                    return;
                }

                AppUpdateBean.ResultBean.AppBean app = updateInfo.getResult().getApp();
                if (null == app) {
                    showUpdateMessage("APP已经是最新版本，无需升级");
                    Log.d(TAG, "升级应答包 AppUpdateBean.ResultBean.AppBean 无效！");
                    return;
                }


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        checkAppData(app);
                    }
                });

            }

            @Override
            public void onError() {
                Log.d(TAG, "check app update error");
            }
        });
    }

    private void checkAppData(AppUpdateBean.ResultBean.AppBean app) {
        //app更新
        if (AppUtils.getAppVersion(this) < app.getVersionCode()) {
            showUpdateDialog(appUpdate, app);
            return;
        }

        showUpdateMessage("已经是最新版本，无需升级");
        return;
    }

    /**
     * 根据flag弹更新提示框
     *
     * @param flag      0代表app更新    1代表控制板更新
     * @param app
     */
    private void showUpdateDialog(int flag,
                                  AppUpdateBean.ResultBean.AppBean app) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        switch (flag) {
            case appUpdate:
                builder.setMessage(app.getVersionNote());
                break;
        }

        builder.setCancelable(false);
        builder.setPositiveButton(this.getString(R.string.update), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                String downloadUrl = "";
                switch (flag) {
                    case appUpdate:
                        downloadUrl = app.getDownloadUrl();
                        if (!TextUtils.isEmpty(downloadUrl) && downloadUrl.startsWith("http")) {
                            downLoadFile(flag, downloadUrl,
                                    Environment.getExternalStorageDirectory().getPath() + File.separator + "test",
                                    "posapp.apk");
                        } else {
                            showUpdateMessage("连接错误，无法更新！");
                        }
                        break;

                }
            }
        });

        if (app.getVersionType() == 0) {
            builder.setNegativeButton(this.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    updateDialog.dismiss();
                }
            });
        }

        updateDialog = builder.create();
        updateDialog.show();
    }

    /**
     * 下载文件
     *
     * @param flag        0代表下载.apk文件，  1代表下载.bin文件
     * @param downloadUrl 下载地址
     * @param path        文件保存路径
     * @param fileName    文件名称
     */
    private void downLoadFile(int flag, String downloadUrl, String path, String fileName) {
        showDownLoadDialog();
        File targetFile = new File(path + File.separator + fileName);
        if (targetFile.exists()) {
            targetFile.delete();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                DownloadUtil.get().download(downloadUrl, path, fileName, new DownloadUtil.OnDownloadListener() {
                    @Override
                    public void onDownloadSuccess(File file) {
                        BaseActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "file下载完成: " + file.getName());
                                dissDownLoadDialog();
                                switch (flag) {
                                    case appUpdate:
                                        UpdateAppUtils.installApk(BaseActivity.this, file);
                                        showUpdateMessage("下载完成！");
                                        break;
                                }
                            }
                        });
                    }

                    @Override
                    public void onDownloading(int progress) {
                        BaseActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setDownLoadProgress(progress);
                            }
                        });
                        Log.e(TAG, "progress: " + progress);
                    }

                    @Override
                    public void onDownloadFailed(Exception e) {
                        BaseActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dissDownLoadDialog();
                            }
                        });
                        Log.e(TAG, "Exception: " + e.getMessage());

                        showUpdateMessage("下载失败！");
                    }

                    @Override
                    public void onCancel() {
                        showUpdateMessage("取消下载！");
                    }
                });
            }
        }).start();
    }

    private ProgressDialog pdDialog = null;

    private void showDownLoadDialog() {
        if (pdDialog == null) {
            pdDialog = new ProgressDialog(this);
            pdDialog.setMax(100);
            pdDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pdDialog.setProgressPercentFormat(null);
            pdDialog.setCanceledOnTouchOutside(false);
            pdDialog.setCancelable(false);
            pdDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    cancelDownload();
                }
            });
            pdDialog.setTitle(getString(R.string.h1_downloading));
        }
        pdDialog.show();
    }

    private void setDownLoadProgress(int value) {
        if (pdDialog != null) {
            pdDialog.setProgress(value);
        }
    }

    private void dissDownLoadDialog() {
        if (pdDialog != null) {
            pdDialog.dismiss();
        }
        pdDialog = null;
    }

    private void cancelDownload() {
        DownloadUtil.get().cancelDownload();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // 声音控制
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private SoundPoolHelp soundPoolHelp;

    /***
     * 初始化声音资源
     */
    private void initSound() {
        soundPoolHelp = new SoundPoolHelp(this);
        soundPoolHelp.initSound();
    }

    /***
     * 播放声音
     * @param b
     */
    public void playSound(boolean b) {
        if (soundPoolHelp != null) {
            soundPoolHelp.playSound(b);
            VibrateUtil.vibrate(this, 150);
        }
    }

    /***
     * 释放声音资源
     */
    private void releaseSound() {
        if (soundPoolHelp != null) {
            soundPoolHelp.releaseSound();
        }
    }

    public void changeAppLanguage() {
        String sta = getStringInfo("language");
        Locale myLocale = null;
        if (sta.isEmpty()) {
            sta = Locale.getDefault().getLanguage();
            setStringInfo("language", sta);
//            if(local.equalsIgnoreCase("zh")){
//                myLocale = new Locale("en");
//            }
        }
        myLocale = new Locale(sta);
        // 本地语言设置
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(String msg) {
        switch (msg) {
            case "SWITCH_LANGUAGE":
                changeAppLanguage();
                recreate();//刷新界面
                break;
        }
    }


    protected void showUpdateMessage(String msg) {
        Log.d(TAG, "showUpdateMessage:" + msg);
    }

    /***
     *
     * @param listFileName      待加密的文件列表
     * @param zipFileName       压缩后的文件名称
     * @return
     */
    protected int zipFiles(ArrayList<String> listFileName, String zipFileName) {
        Log.d(TAG, "压缩文件个数：" + listFileName.size());
        Log.d(TAG, "压缩到：" + zipFileName);

        try {
            if (null == listFileName) return 0;

            int nFilesCount = listFileName.size();
            if (0 == nFilesCount) return 0;

            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));

            File[] files = new File[nFilesCount];
            byte[] buffer = new byte[1024];

            for (int i = 0; i < nFilesCount; i++) {
                files[i] = new File(listFileName.get(i));

                out.putNextEntry(new ZipEntry(files[i].getName()));

                int len = 0;
                FileInputStream fis = new FileInputStream(files[i]);
                while ((len = fis.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                }
                out.closeEntry();
                fis.close();
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    /***
     * 上传日志
     */
    public void uploadLog() {
        //  日志文件路径
        String path = Environment.getExternalStorageDirectory() + "/Log/"; //文件路径
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");// HH:mm:ss //获取当前时间
        Date dtnow = new Date();

        //  删除合并的日志文件
        File[] subFiles = new File(path).listFiles();
        if (subFiles != null) {
            for (File subFile : subFiles) {
                if (subFile.isDirectory()) continue;

                String filename = subFile.getName();
                if ("MERGELOG".equalsIgnoreCase(filename.substring(0, 8))) {
                    Log.d(TAG, "删除合并日志文件：" + filename);
                    subFile.delete();
                }
            }
        }

        //  7日内的日志文件名称
        ArrayList<String> logFiles = new ArrayList<String>();
        Calendar c = Calendar.getInstance();
        for (int i = 0; i < 7; i++) {
            c.setTime(dtnow);
            c.add(Calendar.HOUR, -24 * (6 - i));
            long lastmillis = c.getTimeInMillis();
            Date dtlast = new Date(lastmillis);

            String fileName = path + String.format("WXPOS%s.txt", simpleDateFormat.format(dtlast));
            File logfile = new File(fileName);
            if (logfile.exists()) {
                Log.d("LOG", "合并日志：" + fileName);
                logFiles.add(fileName);
            }
        }
        DetLog.writeLog("LOG", "合并文件内容：" + logFiles);

        //  新日志文件格式
        simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String destFile = path + String.format("MERGELOG%s.zip", simpleDateFormat.format(dtnow));

//        mergeLogFiles(logFiles,destFile);

        zipFiles(logFiles, destFile);

        File logfile = new File(destFile);
        if (!logfile.exists()) {
            Log.d(TAG, "合并后的日志文件不存在");
            return;
        }

        httpPostLogFile(logFiles, logfile);

        return;
    }


    /**
     * 获取日志上传地址
     * @return
     */
    protected String getUploadLogUrl(){
        PosInfoBean posInfoBean = getPosInfoBean();
        if(null==posInfoBean) return "";

        //  http://ip2:serverPort2/api/logs/upload/{posno}?gzip=1&appVersion=1.2.0
        String url = String.format("http://%s:%d/api/logs/upload/%s?gzip=1&appVersion=%s",
                posInfoBean.getMenuServerIP(),
                posInfoBean.getMenuPortNo(),
                posInfoBean.getCposno(),
                getVersionCode());
        return url;
    }

    public void httpPostLogFile(final ArrayList<String> logfiles, final File logfile) {
        Log.d(TAG,"httpPostLogFile");

        //  上传地址
        String url = getUploadLogUrl();
        if(StringUtils.isEmpty(url)) return;

        AsyncHttpCilentUtil asyncHttpCilentUtil = new AsyncHttpCilentUtil();
        asyncHttpCilentUtil.httpsPostFile(url, null, "file", logfile, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "日志上传失败！");

                //  将合并文件删除
                logfile.delete();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "日志上传成功！");

                AppUpdateBean resultBean = JSON.parseObject(response.message(),AppUpdateBean.class);
                if(null==resultBean) return;

                if(resultBean.getCode()!=40000) return;

                //  将合并的原始日志文件删除
                for (String filename : logfiles) {
                    File file = new File(filename);
                    Log.d(TAG, "删除日志文件：" + filename);
                    file.delete();
                }

                //  将合并文件删除
                logfile.delete();
            }
        });
    }

    /***
     * 合并日志文件
     * @param logFiles          日志文件名称列表
     * @param mergeLogFile      目标文件名称
     */
    public void mergeLogFiles(List<String> logFiles, String mergeLogFile) {
        try {
            File realFile = new File(mergeLogFile);
            FileOutputStream fos = new FileOutputStream(realFile);
            for (int i = 0; i < logFiles.size(); i++) {
                appendFile(logFiles.get(i), fos);
            }
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }

    private void appendFile(String filename, FileOutputStream fos) {
        try {
            RandomAccessFile ra = new RandomAccessFile(filename, "r");
            byte[] buffer = new byte[1024 * 8];
            int len = 0;
            while ((len = ra.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
                fos.flush();
            }
            ra.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("LOG", e.getMessage());
        }
        return;
    }

    public String getFileNameWithSuffix(String path) {
        if (TextUtils.isEmpty(path)) return "";

        int start = path.lastIndexOf("/");
        if (start == -1) return "";
        return path.substring(start + 1);
    }


    /***
     * 清理数据库数据
     * @return
     */
    protected int zapDatabase() {
        final int cleanMonths = -3;

        return 0;
    }


    protected void startActivity(Class clz) {
        startActivity(new Intent(this, clz));
    }


    /***
     * 高级密码验证函数
     * @param strPassword
     * @return
     */
    protected boolean isAdvancedPasswordRight(String strPassword) {
        // 先判断是否测试用户
        Calendar calendar = Calendar.getInstance();
        int ret = calendar.get(Calendar.YEAR);
        ret = ret + calendar.get(Calendar.MONTH) + 1;     // MONTH是从0-11
        ret = ret + calendar.get(Calendar.DAY_OF_MONTH);
        ret = ret + calendar.get(Calendar.HOUR_OF_DAY);

        //  测试用户，进入到测试模式
        if (strPassword.equals(ret + "")) {
            return true;
        }
        return false;
    }


    /***
     *
     * @return
     */
    public PosInfoBean getPosInfoBean(){
        String str = getPreInfo(AppIntentString.POS_INFO_BEAN);
        Log.d(TAG,"getPosInfoBean "+str);

        if(StringUtils.isEmpty(str)) return null;

        try{
            PosInfoBean obj = JSON.parseObject(str,PosInfoBean.class);
            return obj;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /***
     *
     * @param bean
     */
    public void savePosInfoBean(PosInfoBean bean){
        String str = JSON.toJSONString(bean);
        Log.d(TAG,"savePosInfoBean "+str);
        setStringInfo(AppIntentString.POS_INFO_BEAN,str);
        return;
    }

    /***
     * 隐藏输入键盘
     * @param v
     */
    protected void hideInputKeyboard(View v) {
        if (null == v) return;
        InputMethodManager imm = (InputMethodManager) getSystemService(mContext.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    /***
     * 显示输入软键盘
     * @param v
     */
    protected void showInputKeyboard(View v){
        if(null==v) return;
        v.setFocusable(true);
        v.setFocusableInTouchMode(true);
        v.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(mContext.INPUT_METHOD_SERVICE);
        imm.showSoftInput(v, 0);
    }


    /***
     * 显示交易记录对话框
     * @param type
     * @param record
     */
    protected void showPaymentRecordDialog(int type,PaymentRecord record){
        if(null==record) return;

        View view = getLayoutInflater().inflate(com.rankway.controller.R.layout.dialog_payment_record, null, false);
        TextView tvPosNo = view.findViewById(com.rankway.controller.R.id.tvPosNo);
        TextView tvAuditNo = view.findViewById(com.rankway.controller.R.id.tvAuditNo);
        TextView tvWorkNo = view.findViewById(com.rankway.controller.R.id.tvWorkNo);
        TextView tvWorkName = view.findViewById(com.rankway.controller.R.id.tvWorkName);
        TextView tvPayWay = view.findViewById(com.rankway.controller.R.id.tvPayWay);
        TextView tvRemain = view.findViewById(com.rankway.controller.R.id.tvRemain);
        TextView tvAmount =  view.findViewById(com.rankway.controller.R.id.tvAmount);
        TextView tvTransTime = view.findViewById(com.rankway.controller.R.id.tvTransTime);

        tvPosNo.setText(record.getPosNo());
        tvAuditNo.setText(record.getAuditNo()+"");
        tvWorkNo.setText(record.getWorkNo());
        tvWorkName.setText(record.getWorkName());
        if(record.getQrType()==0){
            tvPayWay.setText("IC卡");
        }else{
            tvPayWay.setText("二维码");
        }
        tvRemain.setText(String.format("%.2f",record.getRemain()));
        tvAmount.setText(String.format("%.2f",record.getAmount()));
        tvTransTime.setText(DateStringUtils.dateToString(record.getTransTime()));

        showDialogMessage(null, null,
                "确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                },
                "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                },
                view
        );
    }

    /***
     * 清除日志
     */
    public void zapLogFile() {
        final int cleanMonths = -3;

        //  获取指定月份之前的文件
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH,cleanMonths);
        long cleanTime = calendar.getTime().getTime();

        //  日志文件路径
        String path = Environment.getExternalStorageDirectory() + "/Log/"; //文件路径

        //  删除合并的日志文件
        File[] subFiles = new File(path).listFiles();
        if (subFiles == null) return;
        if (subFiles.length==0) return;

        for (File subFile : subFiles) {
            if (subFile.isDirectory()) continue;

            //  如果是合并文件，直接删除
            String filename = subFile.getName();
            if ("MERGELOG".equalsIgnoreCase(filename.substring(0, 8))) {
                Log.d(TAG, "删除合并日志文件：" + filename);
                subFile.delete();
                continue;
            }

            //  最后修改时间
            long createTime = subFile.lastModified();
            if(createTime<cleanTime){
                DetLog.writeLog(TAG,String.format("清除日志文件：%s",subFile.getName()));
                subFile.delete();
            }
        }

        return;
    }

    protected boolean handsetHasScanner(){
        String strModel = Build.MODEL.toUpperCase();
        Log.d(TAG,"MODEL:"+strModel);

        if(strModel.contains("SWIFT 1")){
            return false;
        }

        if(strModel.contains("X1")){
            return true;
        }
        return false;
    }



    /***
     * 获取班次对象
     * @param shiftid
     * @return
     */
    protected PaymentShiftEntity getPaymentShiftEntity(long shiftid){
        PaymentShiftEntity shiftEntity = DBManager.getInstance().getPaymentShiftEntityDao()
                .queryBuilder()
                .where(PaymentShiftEntityDao.Properties.Id.eq(shiftid))
                .unique();
        return shiftEntity;
    }

    /***
     * 保存班次对象
     * @param shiftEntity
     */
    protected void savePaymentShiftEntity(PaymentShiftEntity shiftEntity){
        if(null==shiftEntity) return;
        DBManager.getInstance().getPaymentShiftEntityDao().save(shiftEntity);
    }

    /*
    * -1 --> The printer is not connected or powered on
        0 --> The printer is normal
        1 --> The printer is not connected or powered on
        3 --> Print head open
        7 --> No Paper Feed
        8 --> Paper Running Out
        99 --> Other errors
    */
    protected void showPrinterStatus(int ret) {
        String s = "";
        switch (ret){
            case 1:
                s = "未连接打印机或打印机未上电";
                break;
            case 3:
                s = "请先关闭打印机盖";
                break;
            case 7:
                s = "打印机缺纸";
                break;
            case 8:
                s = "打印机用完";
                break;
            default:
                s =String.format("打印机出错:%d",ret);
                break;
        }
        showStatusDialog(s);
        playSound(false);
    }
}
