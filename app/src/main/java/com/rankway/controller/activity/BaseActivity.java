package com.rankway.controller.activity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.elvishew.xlog.XLog;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.maning.mndialoglibrary.MProgressDialog;
import com.rankway.controller.activity.project.NotificationDetail;
import com.rankway.controller.activity.project.comment.AppSpSaveConstant;
import com.rankway.controller.activity.project.eventbus.MessageEvent;
import com.rankway.controller.activity.project.manager.SpManager;
import com.rankway.controller.activity.service.DownloadUtil;
import com.rankway.controller.common.AppConstants;
import com.rankway.controller.common.AppIntentString;
import com.rankway.controller.common.HandesetInfo;
import com.rankway.controller.common.SemiEventList;
import com.rankway.controller.common.SemiRespHeader;
import com.rankway.controller.common.SemiServerAddress;
import com.rankway.controller.dto.PosInfoBean;
import com.rankway.controller.entity.AppUpdateBean;
import com.rankway.controller.entity.PaymentStatisticsRecordEntity;
import com.rankway.controller.hardware.util.DetLog;
import com.rankway.controller.hardware.util.SoundPoolHelp;
import com.rankway.controller.persistence.DBManager;
import com.rankway.controller.persistence.entity.DishEntity;
import com.rankway.controller.persistence.entity.DishSubTypeEntity;
import com.rankway.controller.persistence.entity.DishTypeEntity;
import com.rankway.controller.persistence.entity.MessageDetail;
import com.rankway.controller.persistence.entity.PaymentItemEntity;
import com.rankway.controller.persistence.entity.PaymentRecordEntity;
import com.rankway.controller.persistence.entity.PaymentShiftEntity;
import com.rankway.controller.persistence.entity.PaymentTotal;
import com.rankway.controller.persistence.entity.SemiEventEntity;
import com.rankway.controller.persistence.gen.DishEntityDao;
import com.rankway.controller.persistence.gen.DishSubTypeEntityDao;
import com.rankway.controller.persistence.gen.PaymentItemEntityDao;
import com.rankway.controller.persistence.gen.PaymentRecordEntityDao;
import com.rankway.controller.persistence.gen.PaymentShiftEntityDao;
import com.rankway.controller.persistence.gen.PaymentTotalDao;
import com.rankway.controller.persistence.gen.SemiEventEntityDao;
import com.rankway.controller.pushmessage.ETEKMessageProcess;
import com.rankway.controller.utils.AppUtils;
import com.rankway.controller.utils.AsyncHttpCilentUtil;
import com.rankway.controller.utils.DateStringUtils;
import com.rankway.controller.utils.UpdateAppUtils;
import com.rankway.controller.utils.VibrateUtil;
import com.rankway.controller.webapi.menu.Dish;
import com.rankway.controller.webapi.menu.DishSubType;
import com.rankway.controller.webapi.menu.DishType;
import com.rankway.controller.webapi.menu.Result;
import com.rankway.controller.webapi.payWebapi;
import com.rankway.controller.widget.MyAlertDialog;
import com.rankway.sommerlibrary.R;
import com.rankway.sommerlibrary.common.ActivityCollector;
import com.rankway.sommerlibrary.utils.FileUtils;
import com.rankway.sommerlibrary.utils.ToastUtils;

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
import java.util.Comparator;
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

    /**
     * 上报设备信息
     */
    public void UploadHandsetInfo(String hardver) {
        if (nUploadNum > 0) {
            //Log.d(TAG,"已经上报了设备状态");
            return;
        }

        String userStr = SpManager.getIntance().getSpString(AppSpSaveConstant.USER_INFO);
        if (TextUtils.isEmpty(userStr)) {
            return;
        }
        String loginUser = SpManager.getIntance().getSpString(AppSpSaveConstant.LOGIN_USER);

        HandesetInfo hi = new HandesetInfo();
        hi.initData(getStringInfo(getString(com.rankway.controller.R.string.controller_sno)));
        hi.setAppVersion(getVersionCode());
        hi.setMbVersion(hardver);
        hi.setAppName(AppUtils.getAppName(this));
        hi.setUserName(loginUser);
        String rptJson = JSON.toJSONString(hi, SerializerFeature.WriteMapNullValue);

        Log.d(TAG, "设备状态：" + rptJson);

        String url = AppConstants.ETEK_UPLOAD_HANDSET_INFO;
        AsyncHttpCilentUtil asyncHttpCilentUtil = new AsyncHttpCilentUtil();
        asyncHttpCilentUtil.httpsPostJson(url, rptJson, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "上报状态失败1: " + e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String respStr = null;
                try {
                    nUploadNum++;
                    respStr = response.body().string();
                    Log.d(TAG, "上报状态成功: " + respStr);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG, "上报状态失败2：" + e.getMessage());
                }
            }
        });
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //  APP和主控板程序升级
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private final int appUpdate = 0;

    private AlertDialog updateDialog;

    private String getCheckAppUrl(){
        PosInfoBean posInfoBean = getPosInfoBean();
        if(null==posInfoBean) return "";

        //  http://ip2:serverPort2/api/appVersions/{appType}?sn=10003
        //  sn为pos机编号，appType暂定两种(Desktop-POS/Handset-POS)
        String url = String.format("http://%s/api/appVersions/Desktop-POS?sn=%s",
                posInfoBean.getUpgradeUrl(),
                posInfoBean.getCposno());
        return url;
    }

    /***
     * 检查服务器上版本信息
     */
    public void checkAppUpdate() {
        String url = getCheckAppUrl();
        if(StringUtils.isEmpty(url)) return;

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
    private void showUpdateDialog(int flag, AppUpdateBean.ResultBean.AppBean app) {
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
                            downLoadFile(flag, downloadUrl, FileUtils.ExternalStorageDirectory + File.separator + "test", "posapp.apk");
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
        String url = String.format("http://%s/api/logs/upload/%s?gzip=1&appVersion=%s",
                posInfoBean.getUpgradeUrl(),
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
                String strjson = response.body().string();
                Log.d(TAG, "onSuccess:" + strjson);
                try {
                    Result result = JSON.parseObject(strjson, Result.class);
                    Log.d(TAG,"Result:"+result.toString());
                    if(result.getCode()>=0){
                        DetLog.writeLog(TAG, "日志上传成功！");
                        //  将原始日志文件删除
                        for (String filename : logfiles) {
                            File file = new File(filename);
                            Log.d(TAG, "删除日志文件：" + filename);
                            file.delete();
                        }
                    }else{
                        DetLog.writeLog(TAG, "日志上传失败！");
                    }
                }catch (Exception e){
                    e.printStackTrace();
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
     * 在通知栏显示信息
     * @param title
     * @param content
     */
    public void showNotification(String title, String content, String from) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (null == notificationManager) {
            Log.d(TAG, "无效的消息通知Mananger");
            return;
        }

        int notificationId = (int) ((System.currentTimeMillis() / 1000) & 0xffffffffL);
        Log.d(TAG, "消息ID:" + notificationId);

        //获取PendingIntent
        Intent mainIntent = new Intent(this, NotificationDetail.class);
        mainIntent.putExtra("TITLE", title);
        mainIntent.putExtra("CONTENT", content);
        mainIntent.putExtra("TIME", System.currentTimeMillis());
        mainIntent.putExtra("ID", notificationId);
        mainIntent.putExtra("FROM", from);

        PendingIntent mainPendingIntent = PendingIntent.getActivity(this,
                notificationId,
                mainIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(mContext, AppConstants.NOTIFICATION_CHANNEL_ID)
                .setContentText(content)
                .setContentTitle(title)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(com.rankway.controller.R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setSound(Uri.fromFile(new File("/system/media/audio/ringtones/Luna.ogg")))
                .setVibrate(new long[]{0, 1000, 1000, 1000})
                .setLights(Color.GREEN, 1000, 1000)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(mainPendingIntent)
                .build();
        if (null == notification) {
            Log.d(TAG, "无效的新建消息");
            return;
        }
        Log.d(TAG, "显示消息：" + content);
        notificationManager.notify(notificationId, notification);
//        notificationId++;
    }

    /***
     * 上传一个指定的文件
     * @param filename
     */
    public void updateFile(String filename) {
        String path = Environment.getExternalStorageDirectory() + filename;
        Log.d(TAG, "上传文件：" + path);
        File logfile = new File(path);
        if (!logfile.exists()) {
            DetLog.writeLog(TAG, "文件不存在：" + path);
            return;
        }

        //  起爆器编号
        String strsno = getPreInfo(getString(com.rankway.controller.R.string.controller_sno));
        if (TextUtils.isEmpty(strsno))
            strsno = "F00A8000000";

        //  上传地址
        String url = String.format(SemiServerAddress.getUploadLogURL() + "/%s", strsno);

        AsyncHttpCilentUtil asyncHttpCilentUtil = new AsyncHttpCilentUtil();
        asyncHttpCilentUtil.httpsPostFile(url, null, "file", logfile, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                logfile.delete();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                logfile.delete();
            }
        });
    }

    protected void processPushMessage(MessageEvent event) {
        DetLog.writeLog(TAG, "收到信息：Title:" + event.getTitle() + " Content:" + event.getMessage());
        Log.d(TAG, "消息类型：" + event.getType());
        switch (event.getType()) {
            case MessageEvent.TYPE_NOTIFICATION:
                //  存储通知
                MessageDetail md = new MessageDetail();
                md.setBreaded(false);
                md.setTime(System.currentTimeMillis());
                md.setId(System.currentTimeMillis());
                md.setContent(event.getMessage());
                md.setTitle(event.getTitle());
                md.setFrom("平台");

                DBManager.getInstance().getMessageDetailDao().save(md);

                break;

            case MessageEvent.TYPE_MESSAGE:
                ETEKMessageProcess proc = new ETEKMessageProcess(this);
                proc.Process(event.getTitle(), event.getMessage());

                break;
        }
    }


    /**
     * 获取黑白名单通信过程
     *
     * @param resp
     */
    public void httpPostPushMessageResponse(String resp) {
        //  起爆器
        String sno = getPreInfo(getString(com.rankway.controller.R.string.controller_sno));
        //  用户名
        String user = SpManager.getIntance().getSpString(AppSpSaveConstant.USER_NAME);

        String url = "";
        url = String.format(SemiServerAddress.getPushMessageReponseURL(), sno, user);

        Log.d(TAG, "URL:" + url);

        AsyncHttpCilentUtil asyncHttpCilentUtil = new AsyncHttpCilentUtil();
        asyncHttpCilentUtil.httpPostJson(url, resp, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: " + call.request());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String strjson = response.body().string();
                Log.d(TAG, "onSuccess:" + strjson);
                try {
                    SemiRespHeader result = JSON.parseObject(strjson, SemiRespHeader.class);
                    if (result == null) {
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /***
     * 上送离线消费交易数据
     */
    public void uploadOfflineRecords() {
        Log.d(TAG,"uploadOfflineRecords");

        new Thread(new Runnable() {
            @Override
            public void run() {
                String str = "";
                try {
//                    //  上报设备状态
//                    UploadHandsetInfo(str);
//
//                    //  上报设备事件
//                    uploadEventList(str);

                    //  自动上传离线的IC卡交易和二维码交易
                    uploadPaymentRecords();

                    //  自动上传支付明细
                    uploadPaymentItems();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        return;
    }

    /***
     * 自动上传支付明细
     */
    public void uploadPaymentItems(){
        Log.d(TAG,"uploadPaymentItems");
        List<PaymentTotal> items = DBManager.getInstance().getPaymentTotalDao()
                .queryBuilder()
                .where(PaymentTotalDao.Properties.UploadFlag.eq(PaymentTotal.UNUPLOAD))
                .list();

        if(items==null) return;
        if(items.size()==0) return;

        PosInfoBean posInfoBean = getPosInfoBean();
        payWebapi obj = payWebapi.getInstance();
        if(null!=posInfoBean){
            obj.setServerIP(posInfoBean.getServerIP());
            obj.setPortNo(posInfoBean.getPortNo());

            obj.setMenuServerIP(posInfoBean.getMenuServerIP());
            obj.setMenuPortNo(posInfoBean.getMenuPortNo());
        }

        for(PaymentTotal item:items){
            obj.uploadPaymentItems(item);
            detSleep(100);
        }
        return;
    }


    /***
     * 自动上传支付记录
     */
    public void uploadPaymentRecords(){
        Log.d(TAG,"uploadPaymentRecords");

        PosInfoBean posInfoBean = getPosInfoBean();
        payWebapi obj = payWebapi.getInstance();
        if(null!=posInfoBean){
            obj.setServerIP(posInfoBean.getServerIP());
            obj.setPortNo(posInfoBean.getPortNo());

            obj.setMenuServerIP(posInfoBean.getMenuServerIP());
            obj.setMenuPortNo(posInfoBean.getMenuPortNo());
        }

        //  上传离线交易数据
        //  5. 未上传的记录上传
        //  5.1 未上传的IC记录
        List<PaymentRecordEntity> listCardRecord = DBManager.getInstance().getPaymentRecordEntityDao()
                .queryBuilder()
                .where(PaymentRecordEntityDao.Properties.UploadFlag.eq(0))
                .where(PaymentRecordEntityDao.Properties.QrType.eq(0))
                .list();
        if(listCardRecord.size()>0){
            int n = 0;
            for(PaymentRecordEntity record:listCardRecord){
                n++;
                int ret = obj.pushOfflineCardPaymentRecords(record);
                if(0!=ret){
                    DetLog.writeLog(TAG,"IC卡离线记录上送失败："+record.toString());

                    record.setUploadFlag(PaymentTotal.UNUPLOAD);
                    record.setUploadTime(new Date());
                }else{
                    DetLog.writeLog(TAG,"IC卡离线记录上送成功："+record.toString());

                    record.setUploadFlag(PaymentTotal.UPLOADED);
                    record.setUploadTime(new Date());
                }
            }
            DBManager.getInstance().getPaymentRecordEntityDao().saveInTx(listCardRecord);
        }


        //  5.2 未上传的QR记录
        List<PaymentRecordEntity> listQrRecord = DBManager.getInstance().getPaymentRecordEntityDao()
                .queryBuilder()
                .where(PaymentRecordEntityDao.Properties.UploadFlag.eq(0))
                .where(PaymentRecordEntityDao.Properties.QrType.notEq(0))
                .list();
        if(listQrRecord.size()>0){
            int n = 0;
            for(PaymentRecordEntity record:listQrRecord){
                n++;
                int ret = obj.pushOfflineQRPaymentRecords(record);
                if(0!=ret){
                    DetLog.writeLog(TAG,"二维码离线记录上送失败："+record.toString());

                    record.setUploadFlag(PaymentTotal.UNUPLOAD);
                    record.setUploadTime(new Date());
                }else{
                    DetLog.writeLog(TAG,"二维码离线记录上送成功："+record.toString());

                    record.setUploadFlag(PaymentTotal.UPLOADED);
                    record.setUploadTime(new Date());
                }
            }
            DBManager.getInstance().getPaymentRecordEntityDao().saveInTx(listQrRecord);
        }
    }


    /***
     * 上传未上传结班记录
     */
    protected void uploadShiftRecords(){
        Log.d(TAG,"uploadShiftRecord");

        PosInfoBean posInfoBean = getPosInfoBean();
        payWebapi obj = payWebapi.getInstance();
        if(null!=posInfoBean){
            obj.setServerIP(posInfoBean.getServerIP());
            obj.setPortNo(posInfoBean.getPortNo());

            obj.setMenuServerIP(posInfoBean.getMenuServerIP());
            obj.setMenuPortNo(posInfoBean.getMenuPortNo());
        }

        List<PaymentShiftEntity> list0 = DBManager.getInstance().getPaymentShiftEntityDao()
                .queryBuilder()
                .where(PaymentShiftEntityDao.Properties.Status.eq(PaymentShiftEntity.SHIFT_STATUS_OFF))
                .list();

        if(list0.size()==0) return;

        for(PaymentShiftEntity entity:list0){
            DetLog.writeLog(TAG,"上传结班记录："+entity.toString());
            obj.uploadShiftOff(entity);
            detSleep(100);
        }
        return;
    }


    /***
     * 推送本地事件
     * @param hardver
     */
    protected void uploadEventList(String hardver) {
        final int EACH_EVENT_COUNT = 100;

        int level = SpManager.getIntance().getSpInt(AppSpSaveConstant.UPLOAD_EVENT_LEVEL);
//        Log.d(TAG,"上传事件级别："+level);

        //  查询是否有事件
        List<SemiEventEntity> allevents = DBManager.getInstance().getSemiEventEntityDao().queryBuilder()
                .where(SemiEventEntityDao.Properties.Status.eq(0))
                .where(SemiEventEntityDao.Properties.EventLevel.ge(level))
                .orderAsc(SemiEventEntityDao.Properties.Id)
                .list();
        if (null == allevents) {
            return;
        }

        if (0 == allevents.size()) {
//            Log.d(TAG,"没有事件需要上报");
            return;
        }
        Log.d(TAG, "事件个数：" + allevents.size());

        List<SemiEventEntity> events = new ArrayList<>();
        if (allevents.size() > EACH_EVENT_COUNT) {
            for (int i = 0; i < EACH_EVENT_COUNT; i++) {
                events.add(allevents.get(i));
            }
        } else {
            events.addAll(allevents);
        }

        String userName = SpManager.getIntance().getSpString(AppSpSaveConstant.LOGIN_USER);
        String sno = getPreInfo(getString(com.rankway.controller.R.string.controller_sno));
        SemiEventList eventList = new SemiEventList(sno,
                userName,
                AppUtils.getAppName(this),
                getVersionCode(),
                hardver);
        eventList.setEventList(events);

        String rptJson = JSON.toJSONString(eventList, SerializerFeature.WriteMapNullValue);
        Log.d(TAG, "上报事件：" + rptJson);

        String url = AppConstants.ETEK_UPLOAD_EVENT;
        AsyncHttpCilentUtil asyncHttpCilentUtil = new AsyncHttpCilentUtil();
        asyncHttpCilentUtil.httpsPostJson(url, rptJson, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "上报事件失败1: " + e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String respStr = null;
                try {
                    DetLog.writeLog(TAG, "上报事件：" + rptJson);

                    respStr = response.body().string();
                    DetLog.writeLog(TAG, "上报事件返回: " + respStr);

                    SemiRespHeader result = JSON.parseObject(respStr, SemiRespHeader.class);
                    if (result == null) {
                        return;
                    }

                    //  上报成功就删除事件，并上传日志
                    if (result.getCode().equalsIgnoreCase("40000")) {
                        DBManager.getInstance().getSemiEventEntityDao().deleteInTx(events);
                        if (allevents.size() <= EACH_EVENT_COUNT) uploadLog();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "上报事件失败2：" + e.getMessage());
                }
            }
        });
    }

    /***
     * 存储事件
     * @param projectId
     * @param process
     * @param nLevel
     * @param description
     */
    public void saveEvent(long projectId, String process, int nLevel, String description) {
        SemiEventEntity event = new SemiEventEntity(projectId, process, nLevel, description);
        DetLog.writeLog("上报事件", JSON.toJSONString(event));
        DBManager.getInstance().getSemiEventEntityDao().save(event);
    }

    /***
     * 清理数据库数据
     * @return
     */
    protected int zapDatabase() {
        final int cleanMonths = -3;

        //  获取指定月份之前的文件
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH,cleanMonths);
        Date cleanDate = calendar.getTime();

        //  清除 PaymentRecordEntity
        List<PaymentRecordEntity> records = DBManager.getInstance().getPaymentRecordEntityDao()
                .queryBuilder()
                .where(PaymentRecordEntityDao.Properties.TransTime.lt(cleanDate))
                .where(PaymentRecordEntityDao.Properties.UploadFlag.eq(PaymentTotal.UPLOADED))
                .orderDesc(PaymentRecordEntityDao.Properties.Id)
                .list();

        Log.d(TAG,"PaymentRecordEntity "+records.size());
        if(records.size()>0){
            long id = records.get(0).getId();
            DetLog.writeLog(TAG,String.format("PaymentRecordEntity 清除%d之前的数据",id));
            DBManager.getInstance().getPaymentRecordEntityDao()
                    .queryBuilder()
                    .where(PaymentRecordEntityDao.Properties.Id.lt(id))
                    .buildDelete()
                    .executeDeleteWithoutDetachingEntities();
        }

        //  清除PaymentTotal和PaymentItemEntity
        List<PaymentTotal> totals = DBManager.getInstance().getPaymentTotalDao()
                .queryBuilder()
                .where(PaymentTotalDao.Properties.Timestamp.lt(cleanDate.getTime()))
                .where(PaymentTotalDao.Properties.UploadFlag.eq(PaymentTotal.UPLOADED))
                .orderDesc(PaymentTotalDao.Properties.Id)
                .list();

        Log.d(TAG,"PaymentTotal "+totals.size());
        if(totals.size()>0){
            long id = totals.get(0).getId();

            DetLog.writeLog(TAG,String.format("PaymentItemEntity 清除%d之前的数据",id));
            DBManager.getInstance().getPaymentItemEntityDao()
                    .queryBuilder()
                    .where(PaymentItemEntityDao.Properties.PaymentTotalId.lt(id))
                    .buildDelete()
                    .executeDeleteWithoutDetachingEntities();

            DetLog.writeLog(TAG,String.format("PaymentTotal 清除%d之前的数据",id));
            DBManager.getInstance().getPaymentTotalDao()
                    .queryBuilder()
                    .where(PaymentTotalDao.Properties.Id.lt(id))
                    .buildDelete()
                    .executeDeleteWithoutDetachingEntities();
        }

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
    protected void showPaymentRecordDialog(int type, PaymentRecordEntity record){
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
     * 获取本地的菜品种类信息
     * @return
     */
    protected List<DishTypeEntity> getLocalDishType(){
        List<DishTypeEntity> allTypes = new ArrayList<>();
        allTypes.clear();

        List<DishTypeEntity> list = DBManager.getInstance().getDishTypeEntityDao().loadAll();
        list.sort(new Comparator<DishTypeEntity>() {
            @Override
            public int compare(DishTypeEntity o1, DishTypeEntity o2) {
                return o1.getDishTypeName().compareTo(o2.getDishTypeName());
            }
        });

        allTypes.addAll(list);
        return allTypes;
    }

    protected List<DishSubTypeEntity> getLocalDishSubType(long typeid){
        List<DishSubTypeEntity> allTypes = new ArrayList<>();
        allTypes.clear();

        List<DishSubTypeEntity> list = DBManager.getInstance().getDishSubTypeEntityDao()
                .queryBuilder()
                .where(DishSubTypeEntityDao.Properties.TypeId.eq(typeid))
                .list();
        list.sort(new Comparator<DishSubTypeEntity>() {
            @Override
            public int compare(DishSubTypeEntity o1, DishSubTypeEntity o2) {
                return o1.getDishSubTypeName().compareTo(o2.getDishSubTypeName());
            }
        });

        allTypes.addAll(list);
        return allTypes;
    }

    /***
     * 获取本地的菜品信息（参数为菜品种类）
     * @param dishSubTypeEntity
     * @return
     */
    protected List<DishEntity> getLocalDish(DishSubTypeEntity dishSubTypeEntity){
        List<DishEntity> allDishEntities = new ArrayList<>();
        allDishEntities.clear();

        List<DishEntity> list = DBManager.getInstance().getDishEntityDao()
                .queryBuilder()
                .where(DishEntityDao.Properties.SubTypeId.eq(dishSubTypeEntity.getId()))
                .list();
        list.sort(new Comparator<DishEntity>() {
            @Override
            public int compare(DishEntity o1, DishEntity o2) {
                return (int)(o1.getId()-o2.getId());
            }
        });
        allDishEntities.addAll(list);
        return allDishEntities;
    }


    /***
     * 获取逐日统计的记录信息
     * @return
     */
    protected List<PaymentStatisticsRecordEntity> getStatisticsRecord() {
        Log.d(TAG, "getStatisticsRecord");
        List<PaymentStatisticsRecordEntity> listStatistics = new ArrayList<>();

        listStatistics.clear();
        List<PaymentRecordEntity> records = DBManager.getInstance().getPaymentRecordEntityDao()
                .queryBuilder()
                .list();
        if (records.size() == 0) return listStatistics;
        Log.d(TAG, "记录总数：" + records.size());

        records.sort(new Comparator<PaymentRecordEntity>() {
            @Override
            public int compare(PaymentRecordEntity o1, PaymentRecordEntity o2) {
                if (o1.getTransTime().before(o2.getTransTime())) return 1;
                return -1;
            }
        });

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        int seqNo = 1;
        for (int i = 0; i < records.size(); i++) {
            PaymentRecordEntity record = records.get(i);
            String s = format.format(record.getTransTime());

            boolean bexist = false;
            for (PaymentStatisticsRecordEntity entity : listStatistics) {
                if (s.equals(entity.getCdate())) {
                    bexist = true;
                    entity.setSubCount(entity.getSubCount() + 1);
                    entity.setSubAmount(entity.getSubAmount() + record.getAmount());
                    entity.getRecordList().add(record);
                    break;
                }
            }
            if (bexist) continue;

            PaymentStatisticsRecordEntity entity = new PaymentStatisticsRecordEntity();
            entity.setSeqNo(seqNo);
            entity.setSubCount(1);
            entity.setCdate(s);
            entity.setSubAmount(record.getAmount());
            entity.getRecordList().add(record);
            listStatistics.add(entity);
            seqNo++;
        }
        return listStatistics;
    }

    /**
     * 处理菜品类型和菜品信息
     * @param result
     */
    protected void saveDishType(Result result){
        Log.d(TAG,"saveDishType");

        if(null==result) return;
        if(40000!=result.getCode()){
            DetLog.writeLog(TAG,String.format("同步菜品信息失败："+result.getMessage()));
            return;
        }

        //  缓存菜品版本信息
        String s = result.getResult().getSiteVersion();
        if(s==null) return;

        //  没有菜品主类
        if(null==result.getResult().getDishTypes()) return;
        if(result.getResult().getDishTypes().size()==0) return;

        SpManager.getIntance().saveSpString(AppIntentString.DISH_TYPE_VER,s);

        //  清除数据库里的菜品类型和菜品
        DBManager.getInstance().getDishEntityDao().deleteAll();
        DBManager.getInstance().getDishSubTypeEntityDao().deleteAll();
        DBManager.getInstance().getDishTypeEntityDao().deleteAll();

        //  按菜品类型逐一处理
        for(DishType dishType:result.getResult().getDishTypes()){
            Log.d(TAG,"菜品主类型："+dishType.getDishTypeName());

            DishTypeEntity typeitem = new DishTypeEntity(dishType);
            DBManager.getInstance().getDishTypeEntityDao().save(typeitem);

            //  菜品子类为空
            if(null==dishType.getDishSubTypes()) continue;
            if(dishType.getDishSubTypes().size()==0) continue;

            Log.d(TAG,"菜品子类型个数："+dishType.getDishSubTypes().size());
            for(DishSubType dishSubType:dishType.getDishSubTypes()){
                Log.d(TAG,"菜品子类型："+dishSubType.getDishSubTypeName());

                DishSubTypeEntity subtypeitem = new DishSubTypeEntity(typeitem,dishSubType);
                DBManager.getInstance().getDishSubTypeEntityDao().save(subtypeitem);

                //  菜品子类中菜品为空
                if(null==dishSubType.getDishs()) continue;
                if(dishSubType.getDishs().size()==0) continue;

                Log.d(TAG,"菜品个数："+dishSubType.getDishs().size());

                //  子类型所属菜品
                List<DishEntity> listDishes = new ArrayList<>();
                for(Dish dish:dishSubType.getDishs()){
                    Log.d(TAG,"菜品："+dish.getDishName());
                    DishEntity dishitem = new DishEntity(typeitem,subtypeitem,dish);
                    listDishes.add(dishitem);
                }
                DBManager.getInstance().getDishEntityDao().saveInTx(listDishes);
            }

        }
        return;
    }


    /***
     * 设置清理日期
     */
    protected void selectCleanDate() {
        Calendar c = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, AlertDialog.THEME_HOLO_LIGHT,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Date date = new Date(year, month, dayOfMonth, 0, 0, 0);
                        String s = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                        DetLog.writeLog(TAG, "清理日期之前记录：" + s);

                        cleanDataPromptDialog(date, s);
                    }

                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                Log.d(TAG, "onKey " + keyCode);
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    dialog.dismiss();

                    DatePicker datePicker = datePickerDialog.getDatePicker();
                    Date date = new Date(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), 0, 0, 0);
                    String s = String.format("%04d-%02d-%02d", datePicker.getYear(), datePicker.getMonth() + 1, datePicker.getDayOfMonth());
                    DetLog.writeLog(TAG, "清理日期之前记录：" + s);

                    cleanDataPromptDialog(date, s);

                    return true;
                }

                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                    return true;
                }
                return false;
            }
        });
        datePickerDialog.show();
    }

    /***
     * 清理确认对话框
     * @param date
     * @param strdate
     */
    private void cleanDataPromptDialog(Date date, String strdate) {
        showDialogMessage("删除", String.format("是否清除%s之前的交易明细？", strdate),
                "确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        CleanDataAsyncTask task = new CleanDataAsyncTask(date);
                        task.execute();
                    }
                },
                "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
    }

    /***
     * 异步清除数据任务
     */
    private class CleanDataAsyncTask extends AsyncTask<String, Integer, Integer>{
        Date date;
        public CleanDataAsyncTask(Date date){
            this.date = date;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProDialog("开始清除，稍等...");
        }

        @Override
        protected Integer doInBackground(String... strings) {
            DetLog.writeLog(TAG,"PayRecordEntity清除之前数据："+date.toString());
            DBManager.getInstance().getPaymentRecordEntityDao()
                    .queryBuilder()
                    .where(PaymentRecordEntityDao.Properties.TransTime.lt(date))
                    .where(PaymentRecordEntityDao.Properties.UploadFlag.eq(1))
                    .buildDelete()
                    .executeDeleteWithoutDetachingEntities();

            List<PaymentTotal> paymentTotals = DBManager.getInstance().getPaymentTotalDao()
                    .queryBuilder()
                    .where(PaymentTotalDao.Properties.UploadFlag.eq(1))
                    .where(PaymentTotalDao.Properties.Timestamp.lt(date.getTime()))
                    .list();

            DetLog.writeLog(TAG,"PayRecordTotal: 数量 "+paymentTotals.size());

            if(paymentTotals.size()>0){
                for(PaymentTotal paymentTotal:paymentTotals) {
                    List<PaymentItemEntity> items = paymentTotal.getDishTransRecordDatas();
                    DBManager.getInstance().getPaymentItemEntityDao().deleteInTx(items);
                }
                DBManager.getInstance().getPaymentTotalDao().deleteInTx(paymentTotals);
            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            missProDialog();

            playSound(true);
            showToast("清除完成！");
        }
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


    /***
     * 清除日志
     */
    public void zapLogFile() {
        final int cleanMonths = -3;

        //  获取指定月份之前的文件
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH,cleanMonths);
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
}
